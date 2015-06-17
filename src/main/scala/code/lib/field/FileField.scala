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

  val uploadPath = "/upload"
  val downloadPath = "/files"
  val id = nextFuncName
  val hiddenId = "hidden-" + id
  val hiddenDeleteId = "hiddenDelete-" + id
  val containerInputId = "inputContainer-" + id
  val containerFieldId = "fieldContainer-" + id
  private var deletedIds: List[String] = List()

  override def afterSave = {
    deleteFiles()
  }

  override def toForm = {
    toEditForm
  }

  def toEditForm = {

    val file = this.get

    val uploadedData = (
      ".link-item [href]" #> (downloadPath +"/"+  file.fileId.get+ "/"+ file.fileName.get ) &
      ".preview-item *" #> previewFile &
      ".link-item *" #> file.fileName.get &
      ".size-item *" #> file.fileSize.get &
      ".remove-item [data-file-id]" #>  file.fileId.get
    ).apply(templateItem)

    S.appendJs(onClickRemoveScript)
    S.appendJs(script)

    Full(
      <div class={containerFieldId} >
        <div class={containerInputId} style="display: none" >
          <input class="fileupload" type="file" name="files" data-url={uploadPath} id={id} />
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

  def toListElement = {

    val file = this.get
    val uploadedData = (
      ".link-item [href]" #> (downloadPath +"/"+  file.fileId.get) &
        ".preview-item *" #> previewFile &
        ".link-item *" #> file.fileName.get &
        ".size-item *" #> file.fileSize.get &
        ".remove-item " #>  ""
      ).apply(templateItem)

    Full(uploadedData)
  }

  def previewFile = {
    val f = this.get
    val previewData = f.fileType.get match {
      case "image/png" =>
        Some(<img src={s"/file/preview/${f.fileId.get}"} title={f.fileName.get}/>)

      case "image/jpeg" =>
        Some(<img src={s"/file/preview/${f.fileId.get}"} title={f.fileName.get}/>)

      case "image/gif" =>
        Some(<img src={s"/file/preview/${f.fileId.get}"} title={f.fileName.get}/>)

      case "application/pdf" =>
        Some(<i class="fa fa-file-pdf-o fa-3x" title={f.fileName.get}/>)

      case "application/zip" =>
        Some(<i class="fa fa-file-zip-o fa-3x" title={f.fileName.get}/>)

      case "application/rar" =>
        Some(<i class="fa fa-file-pdf-o fa-3x" title={f.fileName.get}/>)

      case "application/msword" =>
        Some(<i class="fa fa-file-word-o fa-3x" title={f.fileName.get}/>)

      case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" =>
        Some(<i class="fa fa-file-excel-o fa-3x" title={f.fileName.get}/>)

      case "application/octet-stream" =>
        Some(<i class="fa fa-file fa-3x" title={f.fileName.get}/>)

      case _ =>
        Some(<i class="fa fa-file fa-3x" title={f.fileName.get}/>)
    }

    previewData
  }

  private def saveFileIds(s: String) = {
    implicit lazy val formats: Formats = DefaultFormats

    if (s.trim.nonEmpty){
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
    deletedIds = List() // empty each time

    if (s.trim.nonEmpty) {
      val fileList = parse(s).extract[List[ItemFiles2Delete]]

      fileList.map(f => {
        deletedIds = f.fileId :: deletedIds
      })

    }
  }

  def deleteFiles(): Box[Unit] = tryo {
    MongoDB.use(MongoConfig.defaultId.vend) {
      db =>
        val fs = new GridFS(db)
        deletedIds.map( fId => {
          fs.remove(new ObjectId(fId))
        })
    }
  }

  def templateItem = {
    <span class="data-item">
      <span class="preview-item"></span>
      <i class="fa fa-cloud-download fa-fw"></i>
      <a class="link-item" href="#">%fileName</a>
      <span class="size-item">%fileSize</span>
      <button class="btn btn-danger btn-xs remove-item" data-file-id="%fileId" type="button">
        <i class="fa fa-trash-o fa-fw"></i>&nbsp;Remover
      </button><br/>
    </span>
  }

  private def script = Run{

    val (downloadTemplateItem, _) = fixHtmlAndJs("temp", templateItem)
    """
      $(function () {
          var $uploadInput = $('#""" + id + """'),
              $fieldContainer = $uploadInput.parents('.""" + containerFieldId + """'),
              $inputContainer = $uploadInput.parents('.""" + containerInputId + """'),
              $itemsToSave = $('#""" + hiddenId + """'),
              $itemsToDelete = $('#""" + hiddenDeleteId + """'),
              $containerInfo = $('div.uploadedData', $fieldContainer),
              $progress = $('.progress', $fieldContainer),
              $progressBar = $('.progress-bar', $fieldContainer);

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

              var $row = $(""" + downloadTemplateItem + """);
              var downloadPath =  '""" + downloadPath + """/' + f.fileId +'/'+ f.fileName

              $row.find(".link-item")
                .attr("href", downloadPath )
                .html(f.fileName);

              $row.find(".preview-item").html(previewHtml(f))

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

          function previewHtml(f){
            var html = "";
            switch(f.fileType){
              case "image/png":
              case "image/jpeg":
              case "image/gif":
              html = '<img src="/file/preview/'+ f.fileId+ '" title="'+ f.fileName +'" />';
              break;

              case "application/pdf":
                html = '<i class="fa fa-file-pdf-o fa-3x" title="" />';
                break;

              case "application/zip":
                html = '<i class="fa fa-file-zip-o fa-3x" title="" />';
                break;

              case "application/rar":
                html = '<i class="fa fa-file-zip-o fa-3x" title="" />';
                break;

              case "application/msword":
                html = '<i class="fa fa-file-word-o fa-3x" title="" />';
                break;

              case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                html = '<i class="fa fa-file-excel-o fa-3x" title="" />';
                break;

              case "application/octet-stream":
                html = '<i class="fa fa-file fa-3x" title="" />';
                break;

              default:
                html = '<i class="fa fa-file fa-3x" title="" />';
                break;
            }
            return html;
          }
        });
                                                        """.stripMargin
  }

  private def onClickRemoveScript = Run(
    """
      $(function () {

        var $uploadInput = $('#""" + id + """'),
            $fieldContainer = $uploadInput.parents('.""" + containerFieldId + """'),
            $inputContainer = $uploadInput.parents('.""" + containerInputId + """'),
            $itemsToSave = $('#""" + hiddenId + """'),
            $itemsToDelete  = $('#""" + hiddenDeleteId + """');

        $(".remove-item", $fieldContainer).click(function(e){

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