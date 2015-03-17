package code
package model
package festival

import com.foursquare.rogue.LatLong
import net.liftweb.mongodb.record.field.{MongoCaseClassField, ObjectIdRefField, DateField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{IntField, EnumNameField, StringField}

class Staff extends BsonRecord[Staff] {

  override def meta = Staff

  object kind extends EnumNameField(this, StaffKind)
  object quantity extends IntField(this)

}

object Staff extends Staff with BsonMetaRecord[Staff]

object StaffKind extends Enumeration {
  type StaffKind = Value
  val Permanent, Eventual, Interns, Volunteers = Value
}