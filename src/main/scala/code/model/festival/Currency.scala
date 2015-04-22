package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{CountryField, StringField}

class Currency private() extends MongoRecord[Currency] with ObjectIdPk[Currency] {

  override def meta = Currency

  object code extends StringField(this, 8)
  object name extends StringField(this, 128)

  override def toString = code.get

}

object Currency extends Currency with RogueMetaRecord[Currency]
