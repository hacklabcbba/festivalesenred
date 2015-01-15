package code
package model
package document

import code.model.field.DataType
import net.liftweb.mongodb.record.field.MongoCaseClassField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{StringField, EnumNameField}

class Document private() extends BsonRecord[Document] {

  override def meta = Document

  object documentType extends EnumNameField(this, DocumentType)
  object title extends StringField(this, 500)
  object values extends MongoCaseClassField[Document, DataType](this)

}

object Document extends Document with BsonMetaRecord[Document]
