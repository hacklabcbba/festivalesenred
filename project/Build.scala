import sbt._
import sbt.Keys._

object LiftProjectBuild extends Build {

  import Dependencies._
  import BuildSettings._

  lazy val root = Project("fer", file("."))
    .settings(liftAppSettings: _*)
    .settings(libraryDependencies ++=
      compile(
        liftWebkit,
        liftMongodb,
        liftExtras,
        liftFacebook,
        liftMongoauth,
        liftOmniauth,
        logback,
        rogueField,
        rogueCore,
        rogueLift,
        rogueIndex,
        combobox
      ) ++
      test(scalatest) ++
      container(jettyWebapp)
    )
}
