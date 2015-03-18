package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.StringField

class PublicKind private() extends MongoRecord[PublicKind] with ObjectIdPk[PublicKind] {

  override def meta = PublicKind

  object name extends StringField(this, 200)

}

object PublicKind extends PublicKind with RogueMetaRecord[PublicKind]
