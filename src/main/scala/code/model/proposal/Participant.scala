package code
package model
package proposal

import net.liftweb.mongodb.record.field.BsonRecordField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{StringField, EnumNameField}
import code.model.field.Field

class Participant private () extends BsonRecord[Participant] {

  override def meta = Participant

  //With National, Local and International values
  object scope extends EnumNameField(this, ScopeType)
  //A description of who can participate
  object accessibility extends StringField(this, 250)
  //Who can attend to view
  object audience extends StringField(this, 200)
  //Type of convocatory : Open Convocatory, Colective curatorship
  object convocatory extends StringField(this, 300)
  object otherDescriptions extends BsonRecordField(this, Field) {
    override def optional_? = true
  }
}

object Participant extends Participant with BsonMetaRecord[Participant]
