package code
package  lib
package field

import net.liftweb.common.{Full, Box}
import net.liftweb.http.S
import net.liftweb.http.S.SFuncHolder
import net.liftweb.record.Record
import net.liftweb.record.field.StringField
import net.liftweb.util.{FieldError, Helpers}
import Helpers._

import scala.xml.{Text, NodeSeq}

class LinkField[OwnerType <: Record[OwnerType]](rec: OwnerType, override val maxLength: Int)
  extends StringField[OwnerType](rec, maxLength) {

  val placeHolder: String = displayName

  private def elem = S.fmapFunc(SFuncHolder(this.setFromAny(_))) {
    funcName =>
        <input type={formInputType} maxlength={maxLength.toString}
               name={funcName}
               value={valueBox openOr ""}
               placeholder={placeHolder}
               tabindex={tabIndex toString}/>
  }

  override def toForm: Box[NodeSeq] =
    uniqueFieldId match {
      case Full(id) => Full(elem % ("id" -> id))
      case _ => Full(elem)
    }

  override def validations = valUrl(this.value, "Sitio web no válido") _ :: super.validations

  def valUrl(url: String, msg: => String)(value: ValueType): List[FieldError] = {
    if (url.startsWith("http://") || url.startsWith("https://")) Nil
    else List(FieldError(this, Text(msg)))
  }
}