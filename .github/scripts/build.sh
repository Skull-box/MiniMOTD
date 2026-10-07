#!/usr/bin/env bash
# Build Gradle (tests compris) limité à ce que Velocity exige : minimotd-common (+ ses tests) et minimotd-velocity.
# Les autres plateformes (paper, bukkit, sponge, bungeecord, fabric, neoforge) ne sont ni construites ni publiées.
# La version de release est injectée par -Pversion : le jar shadé et velocity-plugin.json portent <version>. Rien n'est
# committé. Une version sans -SNAPSHOT échappe aussi au suffixe « +<hash> » que minimotd.base-conventions ajoute aux
# versions SNAPSHOT (2.2.4-SNAPSHOT+112d118) : la Release porte donc une version propre.
# « clean » : un seul minimotd-velocity-<version>.jar dans build/libs (les globs de release et d'assets en dépendent).
# Usage : build.sh <version>
set -euo pipefail
version="${1:?usage: build.sh <version>}"

./gradlew --console=plain clean :minimotd-common:build :minimotd-velocity:build -Pversion="$version"
