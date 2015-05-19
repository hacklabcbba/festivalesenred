package code
package config

import code.model.festival.Festival
import model.User

import net.liftweb._
import net.liftweb.http.{Templates, S}
import omniauth.Omniauth
import sitemap._
import sitemap.Loc._

import net.liftmodules.mongoauth.Locs

object MenuGroups {
  val SettingsGroup = LocGroup("settings")
  val TopBarGroup = LocGroup("topbar")
  val UserGroup = LocGroup("usertopbar")
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

  // locations (menu entries)
  val home = MenuLoc(Menu.i("Inicio") / "index" >> TopBarGroup)
  val loginToken = MenuLoc(buildLoginTokenMenu)
  val logout = MenuLoc(buildLogoutMenu)
  private val profileParamMenu = Menu.param[User]("User", "Profile",
    User.findByUsername _,
    _.username.get
  ) / "user" >> Loc.CalcValue(() => User.currentUser)
  lazy val profileLoc = profileParamMenu.toLoc

  val password = MenuLoc(Menu.i("Password") / "settings" / "password" >> RequireLoggedIn )
  val account = MenuLoc(Menu.i("Account") / "settings" / "account" >> RequireLoggedIn)
  val editProfile = MenuLoc(Menu("EditProfile", "Profile") / "settings" / "profile" >> RequireLoggedIn )
  val register = MenuLoc(Menu.i("Registrarse") / "register" >> RequireNotLoggedIn >> UserGroup)
  val enRed = MenuLoc(Menu("EnRed", "En Red") / "en_red" >> TopBarGroup)
  val misFestivales = MenuLoc(Menu("Mis Festivales", "Mis Festivales") / "admin" / "festivales" >> RequireLoggedIn >> SettingsGroup)
  val myAccount = MenuLoc(Menu.i("Mi Cuenta") / "myaccount" >> RequireLoggedIn >> SettingsGroup submenus(
    account.menu, password.menu, editProfile.menu, profileParamMenu
  ))
  val queEs = MenuLoc(Menu("Quees", "¿Qué es?") / "que_es" >> TopBarGroup)
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
    enRed.menu,
    queEs.menu,
    calendar.menu,
    festivalEdit,
    festival,
    myAccount.menu,
    misFestivales.menu,
    Menu.i("logout") / "logout" >> RequireLoggedIn >> SettingsGroup,
    Menu.i("Error") / "error" >> Hidden,
    Menu.i("404") / "404" >> Hidden,
    Menu.i("Throw") / "throw"  >> EarlyResponse(() => throw new Exception("This is only a test."))
  ) ++ Omniauth.sitemap

  /*
   * Return a SiteMap needed for Lift
   */
  def siteMap: SiteMap = SiteMap(menus:_*)
}
