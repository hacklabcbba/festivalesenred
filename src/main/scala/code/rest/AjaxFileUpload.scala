package code
package rest

import java.util.UUID

import code.config.MongoConfig
import code.model.FileRecord
import com.mongodb.gridfs.GridFS
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{LiftResponse, JsonResponse, FileParamHolder, OkResponse}
import net.liftweb.json.JsonAST.JValue
import net.liftweb.mongodb.MongoDB
import org.joda.time.DateTime
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.common._
import net.liftweb.http._

object AjaxFileUpload extends RestHelper {

  serve {

    case "upload" :: Nil Post req => {
      val jvalue = "files" -> req.uploadedFiles.map(file => {
        println("Received: " + file.fileName)
        saveFile(file)
      })

      println(jvalue)

      response(jvalue)
    }

  }

  private def response(jvalue: JValue): LiftResponse = {
    JsonResponse(jvalue,200)
  }

  private def saveFile(fph: FileParamHolder) = {
    val inst: FileRecord = FileRecord.createRecord
    val auxName = fph.fileName
    val auxLength = fph.length
    val auxType = fph.mimeType

    //Get current date using Joda Time
    val today = DateTime.now()
    val fileCreationDate = today.toDate

    val fileMongoName = org.apache.commons.codec.digest.DigestUtils.md5Hex(fph.fileStream) + UUID.randomUUID()
    val file = inst.fileId(fileMongoName).fileName(auxName).
      fileType(auxType).fileSize(auxLength).creationDate(fileCreationDate)

    //Write file in Gridfs
    writeFile(fph, fileMongoName)
    ("fileId" -> fileMongoName) ~
      ("fileName" -> auxName) ~
      ("fileType" -> auxType) ~
      ("fileSize" -> auxLength)
  }

  private def writeFile(fp: FileParamHolder, fileMongoName: String) = {
    MongoDB.use(MongoConfig.defaultId.vend) {
      db =>
        val fs = new GridFS(db)
        val mongoFile = fs.createFile(fp.fileStream)
        mongoFile.setFilename(fileMongoName)
        mongoFile.setContentType(fp.mimeType)
        mongoFile.save()
    }
  }

}