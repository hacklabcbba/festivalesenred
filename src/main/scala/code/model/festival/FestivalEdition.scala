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
  }

  object name extends StringField(this, 128) {
    override def displayName = "Nombre"
  }

}

object FestivalEdition extends FestivalEdition with BsonMetaRecord[FestivalEdition]
