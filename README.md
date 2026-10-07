![MiniMOTD logo](resources/minimotd-logo.png)

[![build](https://img.shields.io/github/checks-status/jpenilla/MiniMOTD/master?label=build)](https://github.com/jpenilla/MiniMOTD/actions) [![latest release](https://img.shields.io/github/v/release/jpenilla/MiniMOTD)](https://github.com/jpenilla/MiniMOTD/releases)

### MiniMOTD is a basic server list MOTD plugin/mod for Minecraft servers and proxies

- MiniMOTD supports RGB colors and gradients through [MiniMessage](https://docs.papermc.io/adventure/minimessage/), which is also where MiniMOTD gets its name.
- For more detailed info on formatting text, refer to the [MiniMessage docs](https://docs.papermc.io/adventure/minimessage/format/).
- RGB colors are automatically downsampled for outdated clients.
- RGB colors are only able to be sent by proxies and 1.16+ servers, and can only be seen by 1.16+ clients.

#### Server Platforms
- [Paper](https://papermc.io/)
- [Sponge API 8](https://www.spongepowered.org/)
- [Sponge API 7](https://www.spongepowered.org/)
- [Fabric](https://fabricmc.net/) (requires [Fabric API](https://modrinth.com/mod/fabric-api))
- [NeoForge](https://neoforged.net/)

#### Proxy Platforms
- [Velocity](https://velocitypowered.com/)
- [Waterfall](https://papermc.io/downloads#Waterfall) / Bungeecord

#### Downloads
Downloads can be obtained from any of:
 - [Modrinth](https://modrinth.com/plugin/minimotd)
 - [Hangar](https://hangar.papermc.io/jmp/MiniMOTD)
 - [GitHub releases](https://github.com/jpenilla/MiniMOTD/releases)

There is a separate jar for each platform. Waterfall and Bungeecord share the same jar.
There are two distributions for Bukkit-based servers: one for Paper >=1.21.8 only (`-paper` jar),
and one for all other supported versions (1.8.8 - 1.21.7, Spigot and Paper) (`-bukkit` jar).

#### Configuration
See the [wiki](https://github.com/jpenilla/MiniMOTD/wiki) for configuration details

#### Screenshots
![demo motd image](resources/minimotd-demo.png)

---

## Fork Skull-box

Fork de [jpenilla/MiniMOTD](https://github.com/jpenilla/MiniMOTD) maintenu par l'org Skull-box. Le réseau n'utilise que la
plateforme **Velocity** : c'est la seule construite, testée et publiée ici.

### Compiler en local

Prérequis : JDK 21 (le wrapper Gradle 9.4.1 est dans le dépôt ; les JDK 17 et 25 des tests multi-versions, activés quand
la variable d'environnement `CI` est définie, sont téléchargés par Gradle si besoin). Aucun jeton : toutes les dépendances sont
publiques.

```bash
./gradlew :minimotd-common:build :minimotd-velocity:build
```

Le jar du plugin est `build/libs/minimotd-velocity-2.2.4-SNAPSHOT+<hash du commit>.jar` (le suffixe `+<hash>` est ajouté
par `gradle/build-logic` aux versions SNAPSHOT). Pour un nom de version propre, comme dans la CI :
`./gradlew clean :minimotd-common:build :minimotd-velocity:build -Pversion=2.2.4`.
Un `./gradlew build` sans cible construit toutes les plateformes (Paper, Fabric, NeoForge, Sponge...), ce qui est long
et inutile ici.

### CI/CD

Fichiers : `.github/workflows/ci.yml`, `.github/scripts/`, `.releaserc.json`, `gradle/skullbox-publish.gradle.kts`. Runner
`blacksmith-2vcpu-ubuntu-2404`, JDK 21. Le `build.yml` de l'amont (publication Modrinth / Hangar) a été remplacé.

- **Chaque push et PR** : `./gradlew clean :minimotd-common:build :minimotd-velocity:build` (tests, checkstyle et
  spotless compris). Une PR ne publie rien.
- **Push sur `master`** : release GitHub par [semantic-release](https://github.com/semantic-release/semantic-release)
  (`feat:` = mineure, `fix:` = correctif, `feat!:` ou `BREAKING CHANGE` = majeure ; `ci:`, `build:`, `docs:`, `chore:`
  ne produisent aucune release). La Release porte `minimotd-velocity-<version>.jar` ; `velocity-plugin.json` du jar
  porte la même version. La version est injectée au build du runner (`-Pversion`), jamais committée. La première
  Release (`v2.2.4`) a été créée d'après la version du projet (`2.2.4-SNAPSHOT`).
- **Push sur `master`, aussi** : le jar shadé est publié en SNAPSHOT mobile `fr.skullbox:minimotd-velocity:2.2.4-SNAPSHOT`
  sur `https://maven.pkg.github.com/Skull-box/MiniMOTD` (pom sans dépendances transitives). Élagage : au 10e dépôt
  du SNAPSHOT le paquet est supprimé puis republié (~0,85 Mo par publication).
- **Toute autre branche** : prérelease GitHub glissante `build-<branche>` (titre = nom de la branche), écrasée à
  chaque push, supprimée avec la branche.

L'historique amont ne suit pas toujours les conventional commits : après une synchronisation avec l'amont, peu de
versions sortent seules ; un `feat:` ou `fix:` explicite en provoque une.
