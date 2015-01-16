package code
package model
package proposal

import code.model.development.Development
import code.model.festival.Place
import net.liftweb.mongodb.record.field.{BsonRecordField, MongoListField, DateField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{EnumNameField, StringField}

class Activity private() extends BsonRecord[Activity] {
  override def meta = Activity

  //Name of activity
  object name extends StringField(this, 1000)
  //Description of activity
  object description extends StringField(this, 1000)
  //when will be
  object place extends BsonRecordField(this, Place)
  //who is the responsible or responsibles
  object responsibles extends MongoListField[Activity, Development](this)
  //spendings To-do when create Spending Class
  //object spending extends MongoListField[Activity, Spending]

}

object Activity extends Activity with BsonMetaRecord[Activity]
