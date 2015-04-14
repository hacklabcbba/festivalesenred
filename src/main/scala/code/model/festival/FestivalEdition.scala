package code
package model
package festival

import code.lib.field.DatepickerField
import com.foursquare.rogue.LatLong
import net.liftweb.common.Full
import net.liftweb.http.{SHtml, S}
import net.liftweb.mongodb.record.field.{MongoCaseClassField, ObjectIdRefField, DateField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{IntField, EnumNameField, StringField}
import net.liftweb.http.js.JsCmds.{Run, Noop}
import net.liftweb.util.Helpers

class FestivalEdition extends BsonRecord[FestivalEdition] {

  override def meta = FestivalEdition

  object date extends DatepickerField(this) {
    override def displayName = "Fecha"
    private def elem =
      S.fmapFunc(S.SFuncHolder(s=> this.setBox(parse(s)))){funcName => {
        <div class="input-append date" data-date="12-02-2012" data-date-format="mm/dd/yyyy">
          {SHtml.ajaxText(valueBox.map(v => dateFormat.format(v)) openOr "", s => {
            this.setBox(parse(s))
            Noop
          }, "tabindex" -> tabIndex.toString, "id" -> uniqueFieldId.openOr(Helpers.nextFuncName))}
          <span class="add-on"><i class="fa fa-th"></i></span>
        </div>
      }}

    override def toForm = {
      S.appendJs(Run(s"$$('#${uniqueFieldId openOr Helpers.nextFuncName}').fdatepicker();"))
      Full(elem)
    }
  }

  object name extends StringField(this, 128) {
    override def displayName = "Nombre"
    override def toForm = Full(
      SHtml.ajaxText(this.value, s => {
        this.set(s)
        Noop
      })
    )
  }

}

object FestivalEdition extends FestivalEdition with BsonMetaRecord[FestivalEdition]
