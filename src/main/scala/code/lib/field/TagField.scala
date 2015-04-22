package code
package lib
package field

import code.model.festival.Tag
import net.liftweb.common.Full
import net.liftweb.http.{SHtml, S}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.mongodb.record.BsonRecord
import net.liftweb.mongodb.record.field.BsonRecordListField
import net.liftweb.util.Helpers

class TagField[OwnerType <: BsonRecord[OwnerType]](rec: OwnerType) extends BsonRecordListField(rec, Tag) {
  
  def script: JsCmd = Run(
    """
      $('.""" + "tm-input" + """').tagsManager({
        prefilled: [""" + this.value.filter(_.tag.get.nonEmpty).map(t => "'" + t.tag.get + "'").mkString(",") + """],
        CapitalizeFirstLetter: true,
        AjaxPush: null,
        AjaxPushAllTags: null,
        AjaxPushParameters: null,
        delimiters: [9, 13, 44],
        backspace: [8],
        output: '#""" + hiddenTagListId + """',
        blinkBGColor_1: '#FFFF9C',
        blinkBGColor_2: '#CDE69C',
        hiddenTagListName: null,
        hiddenTagListId: null,
        deleteTagsOnBackspace: true,
        tagsContainer: null,
        tagCloseIcon: '×',
        tagClass: '',
        validator: null,
        onlyTagList: false
      |    });
    """.stripMargin
  )

  def id = uniqueFieldId openOr Helpers.nextFuncName

  def hiddenTagListId = "hidden-"+id

  def toTags(in: String): List[Tag] = {
    this.set(in.split(",").filter(_.nonEmpty).map(Tag.createRecord.tag(_)).toList)
  }
  
  override def toForm = Full{
    val inst = this.valueBox openOr Tag.createRecord

    S.appendJs(script)

    <div class="row collapse">
      <div class="small-12 large-12 columns">
        <input type="text" autocomplete="off" name="tags" placeholder="Tags" class="input-medium tm-input tm-input-info tm-input-01" data-original-title="" hvkeyboarddefined="true"/>
        {SHtml.hidden(s => toTags(s), this.value.map(t => "'" + t.tag.get + "'").mkString(","), "id" -> hiddenTagListId)}
      </div>
    </div>
  }
}
