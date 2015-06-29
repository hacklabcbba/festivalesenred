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
  val downloadPath = "/service/images"
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

    S.appendJs(onClickRemoveScript)
    S.appendJs(script)

    Full(
      <input class="fileupload" type="file" name="files" data-url={uploadPath} id={id} /> ++
      {SHtml.hidden(s => saveFileIds(s), "", "id" -> hiddenId)} ++
      {SHtml.hidden(s => setDeletedFileIds(s), "", "id" -> hiddenDeleteId)}
    )
  }

  def previewUrl = if (value.fileId.get != "" ) s"/file/preview/${this.get.fileId.get}" else "/img/logo_festivalesenred.png"

  def fileUrl = s"/service/images/${this.get.fileId.get}"

  def previewFile = {
    val f = this.get
    val previewData = f.fileType.get match {
      case "image/png" =>
        Some(<a target="_blank" href={fileUrl}><img src={previewUrl} title={f.fileName.get}/></a>)

      case "image/jpeg" =>
        Some(<a target="_blank" href={fileUrl}><img src={previewUrl} title={f.fileName.get}/></a>)

      case "image/gif" =>
        Some(<a target="_blank" href={fileUrl}><img src={previewUrl} title={f.fileName.get}/></a>)

      case "application/pdf" =>
        Some(<a target="_blank" href={fileUrl}><i class="fa fa-file-pdf-o fa-3x" title={f.fileName.get}/> Descargar </a>)

      case "application/zip" =>
        Some(<a target="_blank" href={fileUrl}><i class="fa fa-file-zip-o fa-3x" title={f.fileName.get}/> Descargar </a>)

      case "application/rar" =>
        Some(<a target="_blank" href={fileUrl}><i class="fa fa-file-pdf-o fa-3x" title={f.fileName.get}/> Descargar </a>)

      case "application/msword" =>
        Some(<a target="_blank" href={fileUrl}><i class="fa fa-file-word-o fa-3x" title={f.fileName.get}/> Descargar </a>)

      case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" =>
        Some(<a target="_blank" href={fileUrl}><i class="fa fa-file-excel-o fa-3x" title={f.fileName.get}/> Descargar </a>)

      case "application/octet-stream" =>
        Some(<a target="_blank" href={fileUrl}><i class="fa fa-file fa-3x" title={f.fileName.get}/> Descargar </a>)

      case _ =>
        Some(<a target="_blank" href={fileUrl}><i class="fa fa-file fa-3x" title={f.fileName.get}/> Descargar </a>)
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

  private def script = Run{
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
              showInfoFiles(data.response().result.files);
              $inputContainer.fadeOut(function(){
                $containerInfo.show();
              });
            }
          });

          function showInfoFiles(files){

            var $rows = $();
            $.each(files, function(i, f){

              var downloadPath =  '""" + downloadPath + """/' + f.fileId;


              $('.""" + containerFieldId + """ .preview-item').replaceWith(previewHtml(f));
              $('.""" + containerFieldId + """ .item-url').attr('href', downloadPath);

              $('.""" + containerFieldId + """ .remove-item')
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
                    $('.""" + containerFieldId + """ .preview-item').replaceWith('<i class="fa fa-upload fa-5x preview-item" title="" />');
                    $currentLink2Delete.parents("span.data-item").fadeOut(function(){
                      $inputContainer.show();
                    });
                });
            });
          }

          function previewHtml(f){
            var html = "";
            switch(f.fileType){
              case "image/png":
              case "image/jpeg":
              case "image/gif":
              html = '<img src="/file/preview/'+ f.fileId+ '" title="'+ f.fileName +'" class="img-responsive preview-item" />';
              break;

              case "application/pdf":
                html = '<i class="fa fa-file-pdf-o fa-3x preview-item" title="" />';
                break;

              case "application/zip":
                html = '<i class="fa fa-file-zip-o fa-3x preview-item" title="" />';
                break;

              case "application/rar":
                html = '<i class="fa fa-file-zip-o fa-3x preview-item" title="" />';
                break;

              case "application/msword":
                html = '<i class="fa fa-file-word-o fa-3x preview-item" title="" />';
                break;

              case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                html = '<i class="fa fa-file-excel-o fa-3x preview-item" title="" />';
                break;

              case "application/octet-stream":
                html = '<i class="fa fa-file fa-3x preview-item" title="" />';
                break;

              default:
                html = '<i class="fa fa-file fa-3x preview-item" title="" />';
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