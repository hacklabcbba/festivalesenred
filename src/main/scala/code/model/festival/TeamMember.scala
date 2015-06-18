package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{EmailField, StringField}
class TeamMember private() extends MongoRecord[TeamMember] with ObjectIdPk[TeamMember] {

  override def meta = TeamMember

  object name extends StringField(this, 200) {
    override def displayName = "Nombre"
  }
  object function extends StringField(this, 200) {
    override def displayName = "Función"
  }
  object email extends EmailField(this, 200) {
    override def displayName = "Correo electrónico"
  }
  object cellphone extends StringField(this, 200) {
    override def displayName = "Celular"
  }


}

object TeamMember extends TeamMember with RogueMetaRecord[TeamMember]
