package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class Area private() extends MongoRecord[Area] with ObjectIdPk[Area] {

  override def meta = Area

  object name extends StringField(this, 200)
  object description extends StringField(this, 500)

}

object Area extends Area with RogueMetaRecord[Area]
