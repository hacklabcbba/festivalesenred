package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.common.Box
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class TrainingActivity private() extends MongoRecord[TrainingActivity] with ObjectIdPk[TrainingActivity] {

  override def meta = TrainingActivity

  object name extends StringField(this, 200)

}

object TrainingActivity extends TrainingActivity with RogueMetaRecord[TrainingActivity] {
  def findByName(name: String): Box[TrainingActivity] = TrainingActivity.where(_.name eqs name).fetch().headOption
}
