package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.common.Box
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{CountryField, StringField}

class City private() extends MongoRecord[City] with ObjectIdPk[City] {

  override def meta = City

  object country extends CountryField(this)
  object name extends StringField(this, 200)

}

object City extends City with RogueMetaRecord[City] {
  def findByName(name: String): Box[City] = City.where(_.name eqs name).fetch().headOption
}
