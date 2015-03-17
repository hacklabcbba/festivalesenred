package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class Space private() extends MongoRecord[Space] with ObjectIdPk[Space] {

  override def meta = Space

  object name extends StringField(this, 200)

}

object Space extends Space with RogueMetaRecord[Space]
