package code
package model
package proposal
package schedule

import code.model.development.Development
import net.liftweb.mongodb.record.field.{DateField, MongoListField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{EnumNameField, IntField, StringField}

class Activity private() extends BsonRecord[Activity] {
  override def meta = Activity

  object name extends StringField(this, 1000)
  object description extends StringField(this, 1000)
  object date extends DateField(this)
  object responsibles extends MongoListField[Activity, Development](this)
  // Quantity in hours implemented
  object hours extends IntField(this, 0)
  object state extends EnumNameField(this, StateActivityType)
  object spending extends MongoListField[Activity, Spending](this)

}

object Activity extends Activity with BsonMetaRecord[Activity]
