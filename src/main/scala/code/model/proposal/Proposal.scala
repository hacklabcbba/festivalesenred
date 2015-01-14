package code
package model
package proposal

import code.model.field.Field
import code.model.proposal.budget.Budget
import net.liftweb.mongodb.record.field.{MongoListField, BsonRecordField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}

class Proposal private () extends BsonRecord[Proposal] {
  override def meta = Proposal

  object participants extends BsonRecordField(this, Participant)
  object goals extends BsonRecordField(this, Goal)
  object schedules extends MongoListField[Proposal, Schedule](this)
  object budgets extends BsonRecordField(this, Budget)
  object otherDescriptions extends BsonRecordField(this, Field) {
    override def optional_? = true
  }
}

object Proposal extends Proposal with BsonMetaRecord[Proposal]
