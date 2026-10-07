// Ajout Skull-box (absent de l'amont jpenilla/MiniMOTD) : appliqué par platform/velocity/build.gradle.kts, seul sous-projet publié.
//
// SNAPSHOT mobile fr.skullbox:minimotd-velocity:2.2.4-SNAPSHOT sur les GitHub Packages de CE dépôt
// (.github/scripts/publish-snapshot.sh, à chaque push sur master). artifactId déjà en minuscules : GitHub refuse les
// majuscules (HTTP 422). On publie le jar shadé (celui des serveurs Velocity), sans « from(components["java"]) » : le pom
// publié n'a ainsi aucune dépendance, donc rien de transitif chez les consommateurs (tout est dans le jar).
//
// minimotd.base-conventions suffixe la version SNAPSHOT du projet d'un hash de commit (2.2.4-SNAPSHOT+112d118) : on le
// retire pour la publication, sinon chaque push créerait une nouvelle version au lieu de mettre à jour le SNAPSHOT mobile.
// Une version de release (-Pversion=2.2.4) n'a pas de suffixe : le substringBefore ne change rien.
//
// Le groupId du projet (xyz.jpenilla, amont) n'est pas touché : seul celui de la publication est fr.skullbox.
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

apply(plugin = "maven-publish")

configure<PublishingExtension> {
  publications {
    register<MavenPublication>("maven") {
      groupId = "fr.skullbox"
      artifactId = "minimotd-velocity"
      version = project.version.toString().substringBefore('+')
      artifact(tasks.named("shadowJar"))
      pom {
        name.set("MiniMOTD (Velocity)")
        description.set("MiniMOTD du réseau Skullbox, plateforme Velocity (jar shadé, sans dépendances transitives)")
      }
    }
  }
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/Skull-box/MiniMOTD")
      credentials {
        username = System.getenv("MAVEN_USERNAME")
        password = System.getenv("MAVEN_TOKEN")
      }
    }
  }
}
