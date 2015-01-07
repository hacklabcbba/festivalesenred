package code
package model
package festival

/**
 * Created by Nataly on 07/01/2015.
 */
trait DataType

case class DateDataType(
  nameField : String,
  values : Seq[String]) extends DataType

case class StringDataType(
  nameField : String,
  values : Seq[String]) extends DataType