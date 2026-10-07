#!/usr/bin/env bash
# Publie le SNAPSHOT mobile fr.skullbox:minimotd-velocity:2.2.4-SNAPSHOT (jar shadé Velocity, pom sans dépendances) sur
# GitHub Packages, après avoir élagué. Lancé après le build, sur la branche par défaut. La version de release passe par
# -Pversion, absente ici : la publication (gradle/skullbox-publish.gradle.kts) porte la version committée
# (-SNAPSHOT), sans le suffixe « +<hash> » que minimotd.base-conventions ajoute aux jars.
#
# Élagage : chaque publication AJOUTE des fichiers horodatés (minimotd-velocity-2.2.4-AAAAMMJJ.hhmmss-N.*) à la MÊME
# version 2.2.4-SNAPSHOT du paquet ; l'API ne permet pas d'en supprimer un seul. Quand le compteur de
# publications (buildNumber de maven-metadata.xml) atteint SNAPSHOT_MAX, on supprime donc le paquet entier
# (derniers fichiers compris), puis on republie : il repart à 1 publication.
# Quota : 500 Mo pour toute l'org. Une publication = jar shadé (~0,8 Mo) : 10 publications ~ 8,5 Mo, sous les
# ~20 Mo visés pour ce dépôt (SNAPSHOT_MAX = floor(20 Mo / taille d'une publication), plafonné à 10).
# Seul le sous-projet minimotd-velocity est publié (:minimotd-velocity:publish) : aucune autre plateforme.
# Environnement : GH_TOKEN (packages: write), MAVEN_USERNAME/MAVEN_TOKEN (auth du dépôt « GitHubPackages »),
#                 GITHUB_REPOSITORY ; SNAPSHOT_MAX (défaut 10).
set -euo pipefail
max="${SNAPSHOT_MAX:-10}"
org="${GITHUB_REPOSITORY%%/*}"
registry="https://maven.pkg.github.com/$GITHUB_REPOSITORY"
# groupId:artifactId des publications
packages=(fr.skullbox:minimotd-velocity)

# Version du projet racine (gradle.properties) : -SNAPSHOT, sans « +<hash> ».
version=$(./gradlew -q --console=plain :properties | sed -n 's/^version: //p')
case "$version" in *-SNAPSHOT) ;; *) echo "::error::version '$version' n'est pas un SNAPSHOT"; exit 1 ;; esac

for p in "${packages[@]}"; do
  group="${p%%:*}"; artifact="${p##*:}"
  meta="$registry/${group//.//}/$artifact/$version/maven-metadata.xml"
  n=$(curl -sS -u "x:$GH_TOKEN" "$meta" | sed -n 's#.*<buildNumber>\([0-9]*\)</buildNumber>.*#\1#p' | head -1)
  n="${n:-0}"
  echo "$group.$artifact $version : $n publication(s) depuis la création de la version"
  if [ "$n" -ge "$max" ]; then
    echo "Élagage : suppression du paquet $group.$artifact (seuil $max)"
    gh api -X DELETE "orgs/$org/packages/maven/$group.$artifact"
  fi
done

./gradlew --console=plain :minimotd-velocity:publish -x test
