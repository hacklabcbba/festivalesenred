package code
package model
package festival

import net.liftweb.mongodb.record.field.DateField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField

class Place extends BsonRecord[Place] {

  override def meta = Place

  object country extends StringField(this, 500)
  object city extends StringField(this, 500)
  object date extends DateField(this)
}

object Place extends Place with BsonMetaRecord[Place]