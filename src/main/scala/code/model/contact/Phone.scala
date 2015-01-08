package code.model.contact

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.IntField

/**
 * Created by Nataly on 08/01/2015.
 */
class Phone private() extends BsonRecord[Phone]{
  override def meta = Phone

  object number extends IntField(this) // it will be change with IntField List Case Clase for more numbers
}

object Phone extends Phone with BsonMetaRecord[Phone]
