package code
package model
package festival

import code.lib.field.DatepickerField
import com.foursquare.rogue.LatLong
import net.liftweb.common.{Box, Full}
import net.liftweb.http.{SHtml, S}
import net.liftweb.mongodb.record.field.{MongoCaseClassField, ObjectIdRefField, DateField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{IntField, EnumNameField, StringField}
import net.liftweb.http.js.JsCmds.{Run, Noop}
import net.liftweb.util.Helpers

import scala.xml.NodeSeq

import Helpers._

class FestivalEdition extends BsonRecord[FestivalEdition] {

  override def meta = FestivalEdition

  object date extends DatepickerField(this) {
    override def displayName = "Fecha"
    override def uniqueFieldId: Box[String] = Full("edition_"+name+"_id")
    private def elem: NodeSeq = {
      val dateButtonId: String = "edition_date_button"
      val dateId: String = uniqueFieldId.map(id => s"${id}_date").openOr(randomString(12))
      val date: NodeSeq = {


        S.fmapFunc((s: String) => setBox(parse(s))) { funcName =>
          <div class="input-group date">
            <input
            class="form-control"
            id={dateId}
            type="text"
            name={funcName}
            value={valueBox.map(v => dateFormat.format(v)).openOr("")}
            tabindex={tabIndex toString}
            readonly=""
            />
            <span class="input-group-btn">
              <button onclick="$('#edition_date_id_date').datepicker('show');" id={dateButtonId} class="btn btn-default" type="button"><i class="fa fa-calendar"></i></button>
            </span>
          </div>
        }
      }

      val script =
        Run(
          "$('#" + dateId + "').datepicker().on('changeDate', function(ev) {" +
            "$('#"+ dateId + "').datepicker('hide')" +
            "});" +
            s"$$('#"+ dateButtonId + "').on('click', function(ev) { $('#"+ dateId + "').datepicker('show')});"
        )

      S.appendJs(script)

      date

    }
    override def toForm = {
      Full(elem)
    }
  }

  object name extends StringField(this, 128) {
    override def displayName = "Nombre"
  }

}

object FestivalEdition extends FestivalEdition with BsonMetaRecord[FestivalEdition]
