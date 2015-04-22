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

class TwitterField[OwnerType <: Record[OwnerType]](rec: OwnerType, override val maxLength: Int)
  extends StringField[OwnerType](rec, maxLength) {

  val placeHolder: String = displayName

  private def elem = S.fmapFunc(SFuncHolder(this.setFromAny(_))) {
    funcName =>
      <div class="row collapse">
        <div class="small-12 large-3 columns">
          <span class="prefix">https://wwww.twitter.com/</span>
        </div>
        <div class="small-12 large-9 columns">
          <input type={formInputType} maxlength={maxLength.toString}
                 name={funcName}
                 value={valueBox openOr ""}
                 placeholder={placeHolder}
                 id={uniqueFieldId openOr nextFuncName}
                 tabindex={tabIndex toString}/>
        </div>
      </div>
  }

  override def toForm: Box[NodeSeq] = Full(elem)
}