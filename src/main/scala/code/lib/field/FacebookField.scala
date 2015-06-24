package code
package lib
package field

import net.liftweb.common.{Full, Box}
import net.liftweb.http.S
import net.liftweb.http.S.SFuncHolder
import net.liftweb.record.Record
import net.liftweb.record.field.StringField
import net.liftweb.util.Helpers
import Helpers._

import scala.xml.NodeSeq

class FacebookField[OwnerType <: Record[OwnerType]](rec: OwnerType, override val maxLength: Int)
  extends StringField[OwnerType](rec, maxLength) {

  val placeHolder: String = displayName

  private def elem = S.fmapFunc(SFuncHolder(this.setFromAny(_))) {
    funcName =>
      <input type={formInputType} maxlength={maxLength.toString}
             name={funcName}
             value={valueBox openOr ""}
             placeholder={placeHolder}
             id={uniqueFieldId openOr nextFuncName}
             tabindex={tabIndex toString}/>
  }

  override def toForm: Box[NodeSeq] = Full(elem)
}