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
        liftMongoauth,
        liftFacebook,
        logback,
        rogueField,
        rogueCore,
        rogueLift,
        rogueIndex
      ) ++
      test(scalatest) ++
      container(jettyWebapp)
    )
}
