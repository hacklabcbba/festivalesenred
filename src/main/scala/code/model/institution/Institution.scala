package code
package model
package institution

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField

class Institution extends BsonRecord[Institution]{

  override def meta = Institution

  object name extends StringField(this, 500)
}

object Institution extends Institution with BsonMetaRecord[Institution]
