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
        // Test
        "org.specs2" %% "specs2" % "2.4.2" % "test"
      )
    )
}
