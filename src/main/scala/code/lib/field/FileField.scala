package code
package lib
package field

import java.util.UUID

import code.config.MongoConfig
import code.model.FileRecord
import code.model.festival.Tag
import com.mongodb.gridfs.GridFS
import net.liftweb.common.Full
import net.liftweb.http.{FileParamHolder, SHtml, S}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.record.BsonRecord
import net.liftweb.mongodb.record.field.BsonRecordField
import net.liftweb.util.Helpers._
import org.joda.time.DateTime
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

class FileField[OwnerType <: BsonRecord[OwnerType]](rec: OwnerType) extends BsonRecordField(rec, FileRecord) {

  val id = nextFuncName

  val hiddenId = "hidden-" + id


  override def toForm = {
    S.appendJs(script)
    Full(
      <input class="fileupload" type="file" name="files" data-url="/upload" id={id}/>
      <div id="progress" style="width:20em; border: 1pt solid silver; display: none">
        {SHtml.hidden(s => saveFileIds(s), "", "id" -> hiddenId)}
        <div id="progress-bar" style="background: green; height: 1em; width:0%"></div>
      </div>
    )
  }

  private def saveFileIds(s: String) = {
    implicit lazy val formats: Formats = DefaultFormats
    println("RECORDS:"+s)
    if (s.trim.nonEmpty) {
      for{
        fileId <- tryo((parse(s) \ "fileId").extract[String])
        fileName <- tryo((parse(s) \ "fileName").extract[String])
        fileType <- tryo((parse(s) \ "fileType").extract[String])
        fileSize <- tryo((parse(s) \ "fileSize").extract[Int])
      } yield {
        val res =
          FileRecord
            .createRecord
            .creationDate(DateTime.now().toDate)
            .fileId(fileId)
            .fileName(fileName)
            .fileType(fileType)
            .fileSize(fileSize)
        println("YES:"+res)
        this.set(res)
      }
    }
  }

  private def script = Run{
    """
      $(function () {
          $('#""" + id + """').fileupload({
            dataType: 'json',
            add: function (e,data) {
              $('#progress-bar').css('width', '0%');
              $('#progress').show();
              data.submit();
            },
            progressall: function (e, data) {
              var progress = parseInt(data.loaded / data.total * 100, 10) + '%';
              $('#progress-bar').css('width', progress);
            },
            done: function (e, data) {
              console.log(data.response().result.files);
              $('#""" + hiddenId + """').val(JSON.stringify(data.response().result.files));
              $('#progress').fadeOut();
            }
          });
        });
    """.stripMargin
  }
}
