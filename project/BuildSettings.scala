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
    mainClass in assembly := Some("com.ivan.ServiceApp"),

    jarName in assembly := { name.value + "-" + version.value + ".jar" },

    excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
      val excludes = Set[String](
        "servlet-api-2.5-20081211.jar",
        "servlet-api-2.5-6.1.14.jar",
        "jsp-api-2.1-6.1.14.jar",
        "jsp-2.1-6.1.14.jar",
        "jsr311-api-1.1.1.jar",
        "stax-api-1.0-2.jar",
        "libthrift-0.5.0-1.jar"
      )
      cp filter { jar => excludes(jar.data.getName) }
    },

    mergeStrategy in assembly <<= (mergeStrategy in assembly) {
      (old) => {
        case "com/twitter/common/args/apt/cmdline.arg.info.txt.1" => MergeStrategy.first
        case x => old(x)
      }
    }
  )

  lazy val buildSettings = basicSettings ++ sbtAssemblySettings
}
