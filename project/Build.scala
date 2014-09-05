import sbt._
import Keys._

object FinagleMemcacheHBaseTest1ProjectBuild extends Build {
  import BuildSettings._

  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }

  lazy val project = Project("finagle-memcache-hbase-test1", file("."))
    .settings(buildSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "com.twitter" %% "finagle-memcached" % "6.20.0",
        "com.gravity" %% "gravity-hpaste" % "0.1.24"
      )
    )
}
