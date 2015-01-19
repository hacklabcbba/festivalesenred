package code
package model
package institution

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.record.field.StringField

class Institution private() extends MongoRecord[Institution] with ObjectIdPk[Institution] {

  override def meta = Institution

  object name extends StringField(this, 500)
}

object Institution extends Institution with RogueMetaRecord[Institution]
