package code.model.proposal.schedule

import code.model.development.Development
import net.liftweb.mongodb.record.field.{DateField, MongoListField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{EnumNameField, IntField, StringField}

class Activity private() extends BsonRecord[Activity] {
  override def meta = Activity

  //Name of activity
  object name extends StringField(this, 1000)
  //Description of activity
  object description extends StringField(this, 1000)
  //when will be
  object date extends DateField(this)
  //who is the responsible or responsibles
  object responsibles extends MongoListField[Activity, Development](this)
  // Quantity in hours implemented
  object hours extends IntField(this, 0)
  // State for a activity
  object state extends EnumNameField(this, StateActivityType)
  //spendings
  object spending extends MongoListField[Activity, Spending](this)

}

object Activity extends Activity with BsonMetaRecord[Activity]
