package code
package model
package festival

import net.liftweb.mongodb.record.field.{ObjectIdRefField, DateField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}

class Place extends BsonRecord[Place] {

  override def meta = Place

  object cityId extends ObjectIdRefField(this, City)
  object date extends DateField(this)
}

object Place extends Place with BsonMetaRecord[Place]