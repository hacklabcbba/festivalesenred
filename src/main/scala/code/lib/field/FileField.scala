package code.lib.field

import code.config.MongoConfig
import code.model.FileRecord
import com.mongodb.gridfs.GridFS
import net.liftweb.common.{Box, Full}
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.{S, SHtml}
import net.liftweb.json._
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.record.BsonRecord
import net.liftweb.mongodb.record.field.BsonRecordField
import net.liftweb.record.LifecycleCallbacks
import net.liftweb.util.Helpers._
import net.liftweb.util.Html5
import org.bson.types.ObjectId
import org.joda.time.DateTime
import net.liftweb.http.js.HtmlFixer

class FileField[OwnerType <: BsonRecord[OwnerType]](rec: OwnerType)
  extends BsonRecordField(rec, FileRecord)
  with LifecycleCallbacks with HtmlFixer {

  val id = nextFuncName
  val hiddenId = "hidden-" + id
  val hiddenDeleteId = "hiddenDelete-" + id
  val containerInputId = "inputContainer-" + id
  val containerFieldId = "fieldContainer-" + id
  private var deletedIds: List[String] = List()

  override def afterSave = {
    println("after Save ejecturado")
    deleteFiles()
  }

  override def toForm = {

    S.appendJs(script)
    Full(
      <div class={containerFieldId}>
        <div class={containerInputId}>
          <input class="fileupload" type="file" name="files" data-url="/upload" id={id} />
        </div>
        <div class="progress" style="width:20em; border: 1pt solid silver; display: none">
          {SHtml.hidden(s => saveFileIds(s), "", "id" -> hiddenId)}
          {SHtml.hidden(s => setDeletedFileIds(s), "", "id" -> hiddenDeleteId)}
          <div class="progress-bar" style="background: green; height: 1em; width:0%"></div>
        </div>
        <div class="uploadedData" style="display:none;"></div>
      </div>
    )
  }

  def toEditForm = {

    val file = this.get
    val tempItem = templateItem

    val uploadedData = (
      ".link-item [href+]" #>  file.fileId.get &
        ".link-item *" #>  file.fileName.get &
        ".size-item *" #>  file.fileSize.get &
        ".remove-item [data-file-id]" #>  file.fileId.get
      ).apply(tempItem)


    S.appendJs(onClickRemoveScript)
    S.appendJs(script)
    Full(
      <div class={containerFieldId} >
        <div class={containerInputId} style="display: none" >
          <input class="fileupload" type="file" name="files" data-url="/upload" id={id} />
        </div>
        <div class="progress" style="width:20em; border: 1pt solid silver; display: none" >
          {SHtml.hidden(s => saveFileIds(s), "", "id" -> hiddenId)}
          {SHtml.hidden(s => setDeletedFileIds(s), "", "id" -> hiddenDeleteId)}
          <div class="progress-bar" style="background: green; height: 1em; width:0%" ></div>
        </div>
        <div class="uploadedData" >
          {uploadedData}
        </div>
      </div>
    )
  }

  private def saveFileIds(s: String) = {
    implicit lazy val formats: Formats = DefaultFormats
    println("RECORDS:"+s)

    if (s.trim.nonEmpty){

      println("deberia llegar algo:", s)
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
        this.set(res)
      }
    }
  }

  private def setDeletedFileIds(s: String) = {
    implicit lazy val formats: Formats = DefaultFormats
    println("RECORDS 2 delete:"+s)
    deletedIds = List() // empty each time

    if (s.trim.nonEmpty) {
      println("deberia llegar lista de datos a borrar...", s)
      val fileList = parse(s).extract[List[ItemFiles2Delete]]
      println("extraido con el case class: "+ fileList )

      fileList.map(f => {
        println("seteando para borrar...", f)
        deletedIds = f.fileId :: deletedIds
      })

      println("lista seteada para borrar.." ,deletedIds)
    }
  }

  //callback triggered on afterSave field
  def deleteFiles(): Box[Unit] = tryo {
    MongoDB.use(MongoConfig.defaultId.vend) {
      db =>
        val fs = new GridFS(db)
        deletedIds.map( fId => {
          println("aqui se deberia eliminar el file", fId)
          val id: ObjectId = new ObjectId(fId)
          fs.remove(id)
        })
    }
  }

  def templateItem = {

    <span class="data-item">
      <a class="link-item" href="files/">%fileName</a>
      <span class="size-item">%fileSize</span>
      [ <a href="#" class="remove-item" data-file-id="%fileId">x</a> ]<br/>
    </span>

  }

  private def script = Run{

    val (tmp, _) = fixHtmlAndJs("temp", templateItem)
    """
      $(function () {
          var $uploadInput = $('#""" + id + """');
          var $fieldContainer = $uploadInput.parents('.""" + containerFieldId + """')
          var $inputContainer = $uploadInput.parents('.""" + containerInputId + """')
          var $itemsToSave = $('#""" + hiddenId + """')
          var $itemsToDelete = $('#""" + hiddenDeleteId + """');
          var $containerInfo = $('div.uploadedData', $fieldContainer);
          var $progress = $('.progress', $fieldContainer);
          var $progressBar = $('.progress-bar', $fieldContainer);


          $uploadInput.fileupload({
            dataType: 'json',
            add: function (e, data) {
              console.log(data.context);
              $progressBar.css('width', '0%');
              $progress.show();
              data.submit();
            },

            progressall: function (e, data) {
              var progress = parseInt(data.loaded / data.total * 100, 10) + '%';
              $progressBar.css('width', progress);
            },

            done: function (e, data) {

              console.log("respuesta server: ", data.response().result.files);
              $itemsToSave.val(JSON.stringify(data.response().result.files));
              $progress.hide();
              $containerInfo.html('');
              var rows = showInfoFiles(data.response().result.files);
              $containerInfo.html(rows);
              $inputContainer.fadeOut(function(){
                $containerInfo.show();
              });
            }
          });

          function showInfoFiles(files){

            var $rows = $();
            $.each(files, function(i, f){
              var $row = $(""" + tmp + """);

              $row.find(".link-item")
                .attr("href", "files/"+f.fileId)
                .html(f.fileName);

              $row.find(".size-item")
                .html("("+ f.fileSize +")("+ f.fileType +")");

              $row.find(".remove-item")
                .attr("data-file-id", f.fileId)
                .click(function(e){
                    e.preventDefault();

                    var $currentLink2Delete = $(this);
                    var idToDelete = $currentLink2Delete.attr("data-file-id");
                    var lastList = $itemsToDelete.val() || "[]";
                    lastList = JSON.parse(lastList);
                    lastList.push({fileId: idToDelete});
                    $itemsToDelete.val(JSON.stringify(lastList));
                    $itemsToSave.val('');
                    $currentLink2Delete.parents("span.data-item").fadeOut(function(){
                      $inputContainer.show();
                    });
                    console.log("remove..", idToDelete);
                });

              $rows = $rows.add($row);

            });
            return $rows;
          }
        });
                                       """.stripMargin
  }

  private def onClickRemoveScript = Run(
    """
      $(function () {

        var $uploadInput = $('#""" + id + """'),
            $inputContainer = $uploadInput.parents('.""" + containerInputId + """'),
            $itemsToSave = $('#""" + hiddenId + """'),
            $itemsToDelete  = $('#""" + hiddenDeleteId + """');

        $("a.remove-item").click(function(e){

            e.preventDefault();
            var $currentLink2Delete = $(this);
            var idToDelete = $currentLink2Delete.attr("data-file-id");
            var lastList = $itemsToDelete.val() || "[]";
            lastList = JSON.parse(lastList);
            lastList.push({fileId: idToDelete});
            $itemsToDelete.val(JSON.stringify(lastList));
            $itemsToSave.val('');
            $currentLink2Delete.parents("span.data-item").fadeOut(function(){
              $inputContainer.show();
            });
            console.log("remove onclick isolated..", idToDelete);
        });
    });
                                                         """.stripMargin
  )
}

case class ItemFiles2Delete (fileId: String)