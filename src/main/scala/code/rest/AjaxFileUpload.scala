package code
package rest

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.UUID
import javax.imageio.ImageIO

import code.config.MongoConfig
import code.model.FileRecord
import com.mongodb.gridfs.{GridFSDBFile, GridFS}
import net.liftmodules.imaging.ImageResizer
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{LiftResponse, JsonResponse, FileParamHolder, OkResponse}
import net.liftweb.json.JsonAST.JValue
import net.liftweb.mongodb.MongoDB
import net.liftweb.util.Helpers._
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

    case "service":: "images" :: fileName :: Nil Get req =>
      serveFile(fileName, req)

    case "file" :: "preview" :: fileName :: Nil Get req =>
      servePreviewFile(fileName, req)
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

  private def serveFile(fileName:String, req: Req): Box[LiftResponse] = {

    MongoDB.use(MongoConfig.defaultId.vend){
      db =>
        val fs = new GridFS(db)
        fs.findOne(fileName) match {

          case file: GridFSDBFile =>
            val lastModified = file.getUploadDate.getTime
            Full(req.testFor304(lastModified, "Expires" -> toInternetDate(millis + 10.days)) openOr {
              val headers = ("Content-Type" -> file.getContentType) ::
                  ("Pragma" -> "") ::
                  ("Cache-Control" -> "") ::
                  ("Last-Modified" -> toInternetDate(lastModified)) ::
                  ("Expires" -> toInternetDate(millis + 10.days)) ::
                  ("Date" -> nowAsInternetDate) :: Nil

              val stream = file.getInputStream

              StreamingResponse(
                stream,
                () => stream.close,
                file.getLength,
                headers, Nil, 200)
            })
          case _ =>
            println("file record empty")
            Empty
        }
    }
  }

  private def servePreviewFile(fileName:String, req: Req): Box[LiftResponse] = {

    MongoDB.use(MongoConfig.defaultId.vend){

      db =>
        val fs = new GridFS(db)
        fs.findOne(fileName) match {

          case file: GridFSDBFile =>
            val lastModified = file.getUploadDate.getTime
            Full(req.testFor304(lastModified, "Expires" -> toInternetDate(millis + 10.days)) openOr {
              val headers =
                ("Content-Type" -> file.getContentType) ::
                  ("Pragma" -> "") ::
                  ("Cache-Control" -> "") ::
                  ("Last-Modified" -> toInternetDate(lastModified)) ::
                  ("Expires" -> toInternetDate(millis + 10.days)) ::
                  ("Date" -> nowAsInternetDate) :: Nil

              val img = ImageIO.read(file.getInputStream)
              val im: BufferedImage = ImageResizer.max(Empty, img, 500, 500)
              val baos: ByteArrayOutputStream = new ByteArrayOutputStream()
              ImageIO.write(im, file.getContentType.split("/")(1), baos)
              val b = baos.toByteArray
              baos.close()
              val stream = new ByteArrayInputStream(b)

              StreamingResponse(
                stream,
                () => stream.close,
                b.length,
                headers, Nil, 200
              )
            })

          case _ =>
            Empty
        }
    }
  }
}