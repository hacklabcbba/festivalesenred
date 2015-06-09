package code
package config

import code.model.festival.Festival
import model.User

import net.liftweb._
import net.liftweb.common.Full
import net.liftweb.http.{Templates, S}
import omniauth.Omniauth
import sitemap._
import sitemap.Loc._

import net.liftmodules.mongoauth.{MongoAuth, Locs}

object MenuGroups {
  val SettingsGroup = LocGroup("settings")
  val TopBarGroup = LocGroup("topbar")
  val UserGroup = LocGroup("usertopbar")
  val AccountGroup = LocGroup("account")
}

/*
 * Wrapper for Menu locations
 */
case class MenuLoc(menu: Menu) {
  lazy val url: String = S.contextPath+menu.loc.calcDefaultHref
  lazy val fullUrl: String = S.hostAndPath+menu.loc.calcDefaultHref
}

object Site extends Locs {
  import MenuGroups._

  override def buildLogoutMenu = Menu(Loc(
    "Salir",
    MongoAuth.logoutUrl.vend.split("/").filter(_.length > 0).toList,
    S.?("Salir"), logoutLocParams
  ))

  protected override def logoutLocParams = RequireLoggedIn :: SettingsGroup ::
    EarlyResponse(() => {
      if (MongoAuth.authUserMeta.vend.isLoggedIn) { MongoAuth.authUserMeta.vend.logUserOut() }
      Full(RedirectToIndexWithCookies)
    }) :: Nil


  // locations (menu entries)
  val home = MenuLoc(Menu.i("Inicio") / "index")
  val loginToken = MenuLoc(buildLoginTokenMenu)
  val logout = MenuLoc(buildLogoutMenu )
  private val profileParamMenu = Menu.param[User]("User", "Profile",
    User.findByUsername _,
    _.username.get
  ) / "user" >> Loc.CalcValue(() => User.currentUser)
  lazy val profileLoc = profileParamMenu.toLoc

  val password = MenuLoc(Menu.i("Contraseña") / "settings" / "password" >> RequireLoggedIn >> AccountGroup)
  val account = MenuLoc(Menu.i("Mi Cuenta") / "settings" / "account" >> RequireLoggedIn >> SettingsGroup >> AccountGroup)
  val editProfile = MenuLoc(Menu("EditProfile", "Perfil") / "settings" / "profile" >> RequireLoggedIn >> AccountGroup)
  val register = MenuLoc(Menu.i("Registrarse") / "register" >> RequireNotLoggedIn >> UserGroup)
  val enRed = MenuLoc(Menu("EnRed", "En Red") / "en_red" >> TopBarGroup)
  val misFestivales = MenuLoc(Menu("Mis Festivales", "Mis Festivales") / "admin" / "festivales" >> RequireLoggedIn >> SettingsGroup >> AccountGroup)


  val queEs = MenuLoc(Menu("Quees", "¿Qué es?") / "que_es" >> TopBarGroup)
  val festivales = MenuLoc(Menu("Festivales", "Festivales") / "festivales" >> TopBarGroup)
  val calendar = MenuLoc(Menu("Calendar", "Calendario") / "calendar" >> TopBarGroup)
  lazy val festivalEdit = Menu.param[Festival]("Formulario Festival", "Formulario Festival",
    Festival.findOrNew _,
    _.id.get.toString
  ) / "festival-form" / * >> TemplateBox(() => Templates("festival-form" :: Nil)) >> RequireLoggedIn

  lazy val festival = Menu.param[Festival]("Festival", "Festival",
    Festival.find _,
    _.id.get.toString
  ) / "festival" / * >> TemplateBox(() => Templates("festival" :: Nil))


  private def menus = List(
    home.menu,
    Menu.i("Iniciar Sesion") / "login" >> RequireNotLoggedIn >> UserGroup,
    register.menu,
    loginToken.menu,
    queEs.menu,
    festivales.menu,
    calendar.menu,
    festivalEdit,
    festival,
    account.menu,
    password.menu,
    editProfile.menu,
    profileParamMenu,
    misFestivales.menu,
    logout.menu,
    Menu.i("Error") / "error" >> Hidden,
    Menu.i("404") / "404" >> Hidden,
    Menu.i("Throw") / "throw"  >> EarlyResponse(() => throw new Exception("This is only a test."))
  ) ++ Omniauth.sitemap

  /*
   * Return a SiteMap needed for Lift
   */
  def siteMap: SiteMap = SiteMap(menus:_*)
}
