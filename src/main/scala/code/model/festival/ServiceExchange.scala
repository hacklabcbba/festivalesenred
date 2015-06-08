package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.common.Box
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class ServiceExchange private() extends MongoRecord[ServiceExchange] with ObjectIdPk[ServiceExchange] {

  override def meta = ServiceExchange

  object name extends StringField(this, 200)

}

object ServiceExchange extends ServiceExchange with RogueMetaRecord[ServiceExchange] {
  def findByName(name: String): Box[ServiceExchange] = ServiceExchange.where(_.name eqs name).fetch().headOption
}
