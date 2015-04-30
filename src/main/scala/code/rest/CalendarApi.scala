package code
package rest

import java.util.UUID

import code.config.MongoConfig
import code.model.FileRecord
import com.mongodb.gridfs.GridFS
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{LiftResponse, JsonResponse, FileParamHolder, OkResponse}
import net.liftweb.json.JsonAST.JValue
import net.liftweb.mongodb.MongoDB
import org.joda.time.DateTime
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.common._
import net.liftweb.http._

object CalendarApi extends RestHelper {

  serve {

    case "api" :: "festivals"  :: Nil Post req => {

      response(JNothing)
    }

  }

  private def response(jvalue: JValue): LiftResponse = {
    JsonResponse(jvalue, 200)
  }
}