package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class Network private() extends MongoRecord[Network] with ObjectIdPk[Network] {

  override def meta = Network

  object name extends StringField(this, 200)

}

object Network extends Network with RogueMetaRecord[Network]
