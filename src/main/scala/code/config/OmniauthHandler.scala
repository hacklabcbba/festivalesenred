package code
package config

import model.User

import net.liftweb._
import common.{Loggable, MDC}
import http.{Factory, LiftRules, RedirectResponse, Req, S, XhtmlResponse}
import net.liftweb.http.rest.RestHelper
import omniauth.Omniauth
import util.Props

object OmniauthHandler extends RestHelper with Loggable {

  def init = {
    LiftRules.dispatch.append(OmniauthHandler)
  }

  serve {
    case "loginsuccess" :: Nil Get request => {
      Omniauth.currentAuth.foreach(User.loginWithOmniauth(_))
      RedirectResponse("/")
    }
  }

}
