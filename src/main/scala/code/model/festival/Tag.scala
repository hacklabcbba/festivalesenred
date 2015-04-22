package code
package model
package festival

import code.model.field.StringDataType
import net.liftweb.common.Full
import net.liftweb.http.SHtml
import net.liftweb.json.{ShortTypeHints, DefaultFormats, Formats}
import net.liftweb.mongodb.record.field.{ObjectIdRefField, MongoCaseClassListField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField

class Tag private() extends BsonRecord[Tag] {
  override def meta = Tag

  object tag extends StringField(this, 128)
}

object Tag extends Tag with BsonMetaRecord[Tag]
