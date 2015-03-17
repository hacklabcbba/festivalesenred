package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{EnumNameField, StringField}

class Partnership private() extends MongoRecord[Partnership] with ObjectIdPk[Partnership] {

  override def meta = Partnership

  object name extends StringField(this, 200)
  object kind extends EnumNameField(this, PartnershipKind)

}

object Partnership extends Partnership with RogueMetaRecord[Partnership]

object PartnershipKind extends Enumeration {
  type PartnershipKind = Value
  val Public, Private, CivilSociety = Value
}