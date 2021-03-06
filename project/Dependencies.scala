import sbt._

object Dependencies {

  val resolutionRepos = Seq(
    "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases",
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

  def compile(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  object Ver {
    val lift = "2.6.1"
    val lift_edition = "2.6"
    val jetty = "9.2.2.v20140723"
  }

  // Lift
  val liftWebkit = "net.liftweb" %% "lift-webkit" % Ver.lift
  val liftMongodb = "net.liftweb" %% "lift-mongodb-record" % Ver.lift
  val liftFacebook = "net.liftmodules" %% "facebook_2.6" % "1.3-SNAPSHOT"
  val liftOmniauth = "net.liftmodules" % "omniauth_2.6_2.11" % "0.17"
  val liftFobo = "net.liftmodules" %% "fobo_2.6" % "1.3"


  // Lift modules
  val liftExtras = "net.liftmodules" %% ("extras_"+Ver.lift_edition) % "0.4-SNAPSHOT"
  val liftMongoauth = "net.liftmodules" %% ("mongoauth_"+Ver.lift_edition) % "0.6-SNAPSHOT"

  // Rogue
  val rogueField      = "com.foursquare" %% "rogue-field"         % "2.4.0" intransitive()
  val rogueCore       = "com.foursquare" %% "rogue-core"          % "2.4.0" intransitive()
  val rogueLift       = "com.foursquare" %% "rogue-lift"          % "2.4.0" intransitive()
  val rogueIndex      = "com.foursquare" %% "rogue-index"         % "2.4.0" intransitive()

  // Jetty
  val jettyWebapp = "org.eclipse.jetty" % "jetty-webapp" % Ver.jetty
  val jettyPlus = "org.eclipse.jetty" % "jetty-plus" % Ver.jetty
  val servlet = "javax.servlet" % "javax.servlet-api" % "3.0.1"

  // Misc
  val logback = "ch.qos.logback" % "logback-classic" % "1.1.2"
  val scalatest = "org.scalatest" %% "scalatest" % "2.2.1"
  val combobox = "net.liftmodules" %% "combobox" % "2.6"
  val imaging = "net.liftmodules" % "imaging_2.6_2.10" % "1.3"
}
