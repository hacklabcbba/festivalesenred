package code
package rest


import code.config.Site
import code.model.festival.{Festival, Place}
import net.liftweb.http.rest.RestHelper
import net.liftweb.http._
import net.liftweb.json.JsonAST.JValue

import net.liftweb.json._
import net.liftweb.json.JsonDSL._

import com.foursquare.rogue.LiftRogue._
import net.liftweb.util.Helpers
import org.joda.time._
import org.joda.time.format._
import Helpers._

object CalendarApi extends RestHelper {

  serve {

    case "api" :: "festivals"  :: Nil Post req => {
      response(JNothing)
    }

    case "api" :: "feeds" :: Nil Get req => {

      val formatter = DateTimeFormat forPattern "yyyy-MM-dd"
      val d1 = formatter parseDateTime S.param("start").getOrElse(DateTime.now().toString)
      val d2 = formatter parseDateTime S.param("end").getOrElse(DateTime.now().toString)

      val festivales = Festival
        .or(
          _.where(_.begins between (d1.toDate, d2.toDate)),
          _.where(_.ends between (d1.toDate, d2.toDate))
        ).fetch

      val jsonList = festivales.map(
        f => {
         
          ("id" -> f.id.toString()) ~
          ("title" -> f.name.asJValue) ~
          ("start" -> f.begins.toString) ~
          ("end" -> f.ends.toString) ~
          ("url" -> Site.festival.calcHref(f))
        }
      )

      response(jsonList)
    }

    case "api" :: "localizations" :: Nil Get req => {

      val festivales = Festival.fetch()
      println("places: ", festivales)
      val placesJsonList =  "locs" -> festivales.flatMap(
        f => f.places.get.map(Place.asJValue(_, f))
      )
      response(placesJsonList)
    }
  }

  private def response(jvalue: JValue): LiftResponse = {
    JsonResponse(jvalue, 200)
  }
}