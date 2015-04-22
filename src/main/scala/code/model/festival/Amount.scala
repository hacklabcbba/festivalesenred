package code
package model
package festival

import code.model.field.StringDataType
import net.liftweb.common.Full
import net.liftweb.http.SHtml
import net.liftweb.json.{ShortTypeHints, DefaultFormats, Formats}
import net.liftweb.mongodb.record.field.{ObjectIdRefField, MongoCaseClassListField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.DecimalField

class Amount private() extends BsonRecord[Amount] {
  override def meta = Amount

  object currency extends ObjectIdRefField(this, Currency) {
    implicit lazy val formats: Formats =
      DefaultFormats.withHints(
        ShortTypeHints(List(classOf[Currency]))
      )
    override def toForm = {
      Full(SHtml.selectElem[Currency](Currency.findAll, this.obj, "tabindex" -> "1")((s: Currency) => {
        this.set(s.id.get)
      }))
    }
  }
  object amount extends DecimalField(this, 0)
}

object Amount extends Amount with BsonMetaRecord[Amount]
