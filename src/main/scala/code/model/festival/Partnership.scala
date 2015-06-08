package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.common.Box
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{EnumNameField, StringField}

class Partnership private() extends MongoRecord[Partnership] with ObjectIdPk[Partnership] {

  override def meta = Partnership

  object name extends StringField(this, 200)
  object kind extends EnumNameField(this, PartnershipKind)

}

object Partnership extends Partnership with RogueMetaRecord[Partnership] {
  def findByName(name: String): Box[Partnership] = Partnership.where(_.name eqs name).fetch().headOption
}

object PartnershipKind extends Enumeration {
  type PartnershipKind = Value
  val Public, Private, CivilSociety = Value
}