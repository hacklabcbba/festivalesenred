package code
package model
package development

import code.lib.RogueMetaRecord
import code.model.festival.Festival
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.{LongRefField, LongPk}
import net.liftweb.record.field.{EnumNameField, StringField}

class Development private() extends MongoRecord[Development] with LongPk[Development] {
  override def meta = Development

  object name extends StringField(this, 500)
  object festival extends LongRefField(this, Festival)
  object role extends EnumNameField(this, TypeRole)
}

object Development extends Development with RogueMetaRecord[Development]
