package code.lib.field

import net.liftmodules.combobox.{ComboBox, ComboItem}
import net.liftweb.common.{Box, Full}
import net.liftweb.http.js.JE.{JsTrue, Str}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.mongodb.AsObjectId
import net.liftweb.mongodb.record.field.{ObjectIdPk, ObjectIdRefListField}
import net.liftweb.mongodb.record.{BsonRecord, MongoMetaRecord, MongoRecord}
import net.liftweb.record.LifecycleCallbacks
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

abstract class OpenComboBoxField[OwnerType <: BsonRecord[OwnerType], RefType <: MongoRecord[RefType] with ObjectIdPk[RefType]](
  rec: OwnerType, override val refMeta: MongoMetaRecord[RefType]
) extends ObjectIdRefListField[OwnerType, RefType](rec, refMeta) with LifecycleCallbacks {

  val placeholder: String
  private var _tempItems: List[ComboItem] = Nil

  def tempItems = _tempItems

  def toString(in: RefType): String

  override def options = refMeta.findAll.map(s => s.id.get -> toString(s))
  def items = options.map(c => ComboItem(c._1.toString, c._2))
  def selectedItems = objs.map(s => ComboItem(s.id.get.toString, toString(s)))

  val comboBoxOptions = List(
    "placeholder" -> Str(placeholder),
    "multiple" -> JsTrue,
    "width" -> Str("100%")
  )

  def onSearching(term: String): List[ComboItem] = {
    items.filter(_.text.contains(term))
  }

  def onItemsSelected(items: List[ComboItem]): JsCmd = {
    val realItems = items.filter(s => AsObjectId.asObjectId(s.id).nonEmpty)
    val tempItems = items.filter(s => AsObjectId.asObjectId(s.id).isEmpty)
    setFromAny(realItems.flatMap(s => AsObjectId.asObjectId(s.id)))
    _tempItems = tempItems
    Noop
  }

  private def elem = {
    ComboBox(selectedItems, onSearching _, onItemsSelected _, true, comboBoxOptions).comboBox
  }

  override def toForm: Box[NodeSeq] = Full(elem)

  
}
