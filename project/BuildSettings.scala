import sbt._
import Keys._

object BuildSettings {

  lazy val repos = Seq(
    "Sonatype OSS Repo" at "https://oss.sonatype.org/content/repositories/releases",
    "Concurrent Maven Repo" at "http://conjars.org/repo",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Twitter Maven Repo" at "http://maven.twttr.com",
    "Maven Repository" at "http://mvnrepository.com/artifact/",
    "releases" at "http://oss.sonatype.org/content/repositories/releases",
    "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Repo 2" at "https://oss.sonatype.org/content/groups/scala-tools")

  lazy val basicSettings = Seq[Setting[_]](
    organization  := "com.ivan",
    version       := "0.0.1",
    description   := "Example of service based on finagle memcached protocol to aggregate time series stream of data into (min, avg, max) into hbase",
    scalaVersion  := "2.10.4",
    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    resolvers     ++= repos
  )

  import sbtassembly.Plugin._
  import AssemblyKeys._
  lazy val sbtAssemblySettings = assemblySettings ++ Seq(
    jarName in assembly := { name.value + "-" + version.value + ".jar" },

    excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
      val excludes = Set[String]()
      cp filter { jar => excludes(jar.data.getName) }
    },

    mergeStrategy in assembly <<= (mergeStrategy in assembly) {
      (old) => {
        case x => old(x)
      }
    }
  )

  lazy val buildSettings = basicSettings ++ sbtAssemblySettings
}
