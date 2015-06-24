package code
package rest


import java.text.SimpleDateFormat

import code.config.Site
import code.model.festival.{City, Area, Festival, Place}
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

object FestivalApi extends RestHelper {

  object AsFestival {
    def unapply(in: String): Option[Festival] = Festival.find(in)
  }

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

    case "api" :: "festival" :: "localizations" :: AsFestival(festival) ::  _ JsonGet _ => {
      val placesJsonList =  "locs" -> festival.places.get.map(Place.asJValue(_, festival))
      response(placesJsonList)
    }

    case "api" :: "localizations" :: Nil JsonPost json -> _ => {
      val dateFormat = new SimpleDateFormat("MM/dd/yyyy")

      val areas: List[Area] =  (json \\ "areas").extractOpt[List[String]].getOrElse(Nil)
        .flatMap(s => Area.findByName(s))
      val cities: List[City] =  (json \\ "cities").extractOpt[List[String]].getOrElse(Nil)
        .flatMap(City.findByName(_))
      val begins =  (json \\ "begins").extractOpt[String].flatMap(s => Helpers.tryo(dateFormat.parse(s)))
      val ends =  (json \\ "ends").extractOpt[String].flatMap(s => Helpers.tryo(dateFormat.parse(s)))

      val festivales =
        if (areas.isEmpty && cities.isEmpty && begins.isEmpty && ends.isEmpty)
          Festival.fetch()
        else
          Festival.findAllForMapByAreasCitiesAndDates(areas, cities, begins, ends)
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