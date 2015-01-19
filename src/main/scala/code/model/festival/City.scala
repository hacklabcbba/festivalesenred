package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class City private() extends MongoRecord[City] with ObjectIdPk[City] {

  override def meta = City

  object country extends StringField(this, 200)
  object nameCity extends StringField(this, 200)

}

object City extends City with RogueMetaRecord[City]
