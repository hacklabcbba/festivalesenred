package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.common.Box
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class Area private() extends MongoRecord[Area] with ObjectIdPk[Area] {

  override def meta = Area

  object code extends StringField(this, 8)
  object name extends StringField(this, 200)
  object description extends StringField(this, 500)

}

object Area extends Area with RogueMetaRecord[Area] {
  def findByName(name: String): Box[Area] = Area.where(_.name eqs name).fetch().headOption
}
