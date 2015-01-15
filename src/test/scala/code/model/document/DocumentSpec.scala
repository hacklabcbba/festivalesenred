package code
package model
package document

import code.model.document.DocumentType._
import code.model.field._

class DocumentSpec extends BaseMongoSessionWordSpec {
  "Document" should {
    "create, validate, save, and retrieve properly" in {

      val values = ListStringDataType(List("https://docs.google.com/document/d/1K1gcXhgMgnECa7UKpzGlln_lbSwk6aaZWaEMtsjPYeQ/edit",
        "https://docs.google.com/a/foradoeixo.org.br/document/d/1EkAXNXeK1TbN0mHormapo4mAuYVspXB8LpWOXoUuzFg/edit#",
        "https://docs.google.com/a/foradoeixo.org.br/document/d/1zOFTxn6Wi3qlusN8F75PFfllsux9boZxGRMQIZ45EH4/edit"))

      val newDocument = Document.createRecord
        .documentType(Link)
        .title("Plataform Moodle")
        .values(values)

      val errsField = newDocument.validate
      if (errsField.length > 1) {
        fail("Validation error: " + errsField.mkString(", "))
      }
    }
  }
}
