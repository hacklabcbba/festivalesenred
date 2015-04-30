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
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format._

object CalendarApi extends RestHelper {

  serve {

    case "api" :: "festivals"  :: Nil Post req => {
      response(JNothing)
    }

    case "api" :: "feeds" :: Nil Get req => {

      val formatter = DateTimeFormat forPattern "yyyy-MM-dd"
      val d1 = formatter parseDateTime S.param("start").getOrElse(DateTime.now().toString)
      val d2 = formatter parseDateTime S.param("end").getOrElse(DateTime.now().toString)

      println("parametros: ", d1, d2)

      val festivales = Festival.where(_.places.subfield(_.date) between (d1.toDate, d2.toDate)).fetch()
      println("places: ", festivales)
      val placesJsonList = festivales.flatMap(
        //f => f.get.places.map ( (f: Festival, p: Place) => ("title" -> p.name ) ~ ("start" -> p.date ) )
        //f => f.get.places.subfield(_.date).map ( (f: Festival, p: Place) => ("title" -> p.name ) ~ ("start" -> p.date ) )
        //f => f.places.subfield(_.date).map ( (f: Festival, p: Place) => ("title" -> p.name ) ~ ("start" -> p.date ) )
        f => f.places.get.map(
          (p: Place) =>
            ("id" -> f.id.toString()) ~
            ("title" -> f.name.asJValue) ~
            ("start" -> p.date.toString) ~
            ("url" -> Site.festival.calcHref(f))
        )
      )
      response(placesJsonList)
    }
  }

  private def response(jvalue: JValue): LiftResponse = {
    JsonResponse(jvalue, 200)
  }
}