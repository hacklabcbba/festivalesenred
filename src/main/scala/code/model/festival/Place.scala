package code
package model
package festival

import com.foursquare.rogue.LatLong
import net.liftweb.mongodb.record.field.{MongoCaseClassField, ObjectIdRefField, DateField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField

class Place extends BsonRecord[Place] {

  override def meta = Place

  object name extends StringField(this, 300)
  object cityId extends ObjectIdRefField(this, City)
  object date extends DateField(this)
  object geoLatLng extends MongoCaseClassField[Place, LatLong](this)
}

object Place extends Place with BsonMetaRecord[Place]