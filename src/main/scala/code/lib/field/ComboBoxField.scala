package code
package lib
package field

import dispatch.classic.json.JsFalse
import net.liftmodules.combobox.{ComboBox, ComboItem}
import net.liftweb.common.{Full, Box}
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.{Str, JsTrue}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.mongodb.AsObjectId
import net.liftweb.mongodb.record.field.{ObjectIdPk, ObjectIdRefListField}
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord, BsonRecord}
import net.liftweb.util.Helpers
import scala.xml.NodeSeq
import Helpers._

abstract class ComboBoxField[OwnerType <: BsonRecord[OwnerType], RefType <: MongoRecord[RefType] with ObjectIdPk[RefType]](
  rec: OwnerType, override val refMeta: MongoMetaRecord[RefType]
) extends ObjectIdRefListField[OwnerType, RefType](rec, refMeta) {

  val placeholder: String

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
    setFromAny(items.flatMap(s => AsObjectId.asObjectId(s.id)))
    Noop
  }

  private def elem = {
    ComboBox(Nil, onSearching _, onItemsSelected _, true, comboBoxOptions).comboBox
  }

  override def toForm: Box[NodeSeq] = Full(elem)
}

abstract class SingleComboBoxField[OwnerType <: BsonRecord[OwnerType], RefType <: MongoRecord[RefType] with ObjectIdPk[RefType]](
rec: OwnerType, override val refMeta: MongoMetaRecord[RefType]) extends ObjectIdRefListField[OwnerType, RefType](rec, refMeta) {

  val placeholder: String

  def toString(in: RefType): String

  override def options = refMeta.findAll.map(s => s.id.get -> toString(s))
  def items = options.map(c => ComboItem(c._1.toString, c._2))
  def selectedItems = objs.map(s => ComboItem(s.id.get.toString, toString(s)))

  val comboBoxOptions = List(
    "placeholder" -> Str(placeholder),
    "width" -> Str("100%")
  )

  def onSearching(term: String): List[ComboItem] = {
    items.filter(_.text.contains(term))
  }

  def onItemsSelected(items: List[ComboItem]): JsCmd = {
    setFromAny(items.flatMap(s => AsObjectId.asObjectId(s.id)))
    Noop
  }

  private def elem = {
    ComboBox(Nil, onSearching _, onItemsSelected _, false, comboBoxOptions).comboBox
  }

  override def toForm: Box[NodeSeq] = Full(elem)
}

