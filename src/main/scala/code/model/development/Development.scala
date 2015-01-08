package code.model.development

import code.model.festival.Festival
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{LongRefField, LongPk}
import net.liftweb.record.field.StringField

/**
 * Created by Nataly on 08/01/2015.
 */
class Development private() extends MongoRecord[Development] with LongPk[Development]{
  override def meta = Development

  object name extends StringField(this, 500)
  object festivalId extends LongRefField(this, Festival)
}

object Development extends Development with MongoMetaRecord[Development]
