package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class EquipmentDetail private() extends MongoRecord[EquipmentDetail] with ObjectIdPk[EquipmentDetail] {

  override def meta = EquipmentDetail

  object name extends StringField(this, 200)

}

object EquipmentDetail extends EquipmentDetail with RogueMetaRecord[EquipmentDetail]
