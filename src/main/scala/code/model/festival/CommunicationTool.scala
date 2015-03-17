package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class CommunicationTool private() extends MongoRecord[CommunicationTool] with ObjectIdPk[CommunicationTool] {

  override def meta = CommunicationTool

  object name extends StringField(this, 200)

}

object CommunicationTool extends CommunicationTool with RogueMetaRecord[CommunicationTool]
