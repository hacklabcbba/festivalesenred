package code
package model
package field

import java.util.Date

trait DataType

case class DateDataType(
  nameField : String,
  values : Seq[Date]
) extends DataType

case class StringDataType(
  nameField : String,
  values : Seq[String]
) extends DataType

case class IntDataType(
  nameField : Int,
  values : Seq[String]
) extends DataType