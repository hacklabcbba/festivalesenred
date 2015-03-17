package code
package model
package festival

import com.foursquare.rogue.LatLong
import net.liftweb.mongodb.record.field.{MongoCaseClassField, ObjectIdRefField, DateField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{IntField, EnumNameField, StringField}

class FestivalEdition extends BsonRecord[FestivalEdition] {

  override def meta = FestivalEdition

  object date extends DateField(this)

}

object FestivalEdition extends FestivalEdition with BsonMetaRecord[FestivalEdition]
