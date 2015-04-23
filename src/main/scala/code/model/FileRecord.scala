package code
package model

import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import net.liftweb.http.{RequestVar, FileParamHolder}
import net.liftweb.common.{Empty, Box}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonAST.JString


class FileRecord private () extends BsonRecord[FileRecord] {
  def meta = FileRecord
  
  object fileId extends StringField(this, 50)
  object fileName extends StringField(this, 50)
  object fileType extends StringField(this, 50)
  object fileSize extends LongField(this)
  object creationDate extends DateField(this)
}

object FileRecord extends FileRecord with BsonMetaRecord[FileRecord]
