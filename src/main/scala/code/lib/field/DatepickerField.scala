package code
package lib
package field

import java.text.SimpleDateFormat
import java.util.{Locale, Date}

import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.http.js.JsCmds.{Script, Run}
import net.liftweb.mongodb.record.BsonRecord
import net.liftweb.mongodb.record.field.DateField
import net.liftweb.util.Helpers
import Helpers._

import scala.xml.NodeSeq

class DatepickerField[OwnerType <: BsonRecord[OwnerType]](rec: OwnerType)
  extends DateField[OwnerType](rec) {

  protected val dateFormat = new SimpleDateFormat("MM/dd/yyyy", new Locale("es", ""))

  val dateFieldId = nextFuncName

  protected def parse(s: String) = tryo(dateFormat.parse(s))

  override def toForm = {
    Full(elem)
  }

  private def elem: NodeSeq = {
    val dateButtonId: String = randomString(12)
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
            <button id={dateButtonId} class="btn btn-default" type="button"><i class="fa fa-calendar"></i></button>
          </span>
        </div>
      }
    }

    val script = Script(
      Run(
        "$(function(){" +
          "$('#" + dateId + "').datepicker().on('changeDate', function(ev) {" +
          "$('#"+ dateId + "').datepicker('hide')" +
          "});" +
          s"$$('#"+ dateButtonId + "').on('click', function(ev) { $('#"+ dateId + "').datepicker('show')});" +
          "})"
      )
    )

    date ++ script

  }

  override def toString = dateFormat.format(this.value)

  val dayFormat = new SimpleDateFormat("EEEE d")

  val monthFormat = new SimpleDateFormat("MMMM")

  val yearFormat = new SimpleDateFormat("yyyy")


  def literalDate = {
    dayFormat.format(value) + " de " + monthFormat.format(value) + " del " + yearFormat.format(value)
  }

}