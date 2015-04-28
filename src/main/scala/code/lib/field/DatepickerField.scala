package code
package lib
package field

import java.text.SimpleDateFormat
import java.util.Date

import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.mongodb.record.BsonRecord
import net.liftweb.mongodb.record.field.DateField
import net.liftweb.util.Helpers
import Helpers._

class DatepickerField[OwnerType <: BsonRecord[OwnerType]](rec: OwnerType)
  extends DateField[OwnerType](rec) {

  protected val dateFormat = new SimpleDateFormat("MM/dd/yyyy")

  val dateFieldId = nextFuncName

  protected def parse(s: String) = tryo(dateFormat.parse(s))

  private def elem =
    S.fmapFunc(S.SFuncHolder(s=> this.setBox(parse(s)))){funcName => {
      <div class="input-append date" data-date="12-02-2012" data-date-format="mm/dd/yyyy">
        <input class="span2"
               size="16"
               id={dateFieldId}
               type="text"
               name={funcName}
               value={valueBox.map(v => dateFormat.format(v)) openOr ""}
               tabindex={tabIndex.toString}
        />
          <span class="add-on"><i class="fa fa-th"></i></span>
      </div>
    }}

  override def toForm = {
    S.appendJs(Run(s"$$('#${dateFieldId}').fdatepicker();"))
    Full(elem)
  }

  override def toString = dateFormat.format(this.value)

}