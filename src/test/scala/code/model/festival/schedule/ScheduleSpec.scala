package code
package model
package festival
package schedule

import java.util.Calendar

import code.model.development.Development
import code.model.development.RoleType._
import code.model.field.{DecimalDataType, Field, StringDataType}
import code.model.proposal.schedule.{Schedule, Activity, Spending}
import net.liftweb.common.Box
import net.liftweb.util.Helpers._

class ScheduleSpec extends BaseMongoSessionWordSpec {

  "Schedule" should {
    "create, validate, save, and retrieve properly" in {
      // Development
      val newDevelopment = Development.createRecord
        .name("Jhon Charles")
        .role(Responsible)

      val dateFormat : java.text.DateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")
      val date : Box[java.util.Date] = tryo {
        val calendar = Calendar.getInstance()
        calendar.setTime(dateFormat.parse("20-05-2001"))
        calendar.getTime
      }

      // otherDescriptions
      val names = StringDataType("Name", List("Neil", "Nicolas"))
      val lastNames = StringDataType("Last Name", List("Hudson", "Harrison"))

      val cost = DecimalDataType("Cost", List(80.00, 20.00))

      val newField = Field.createRecord
        .fieldList(names :: lastNames :: cost :: Nil)


      val errsField = newField.validate
      if (errsField.length > 1) {
        fail("Validation error: " + errsField.mkString(", "))
      }

      val spending = Spending.createRecord
        .name("Transport")
        .listField(newField)
        .total(100.00)

      //Activity
      val activity1 = Activity.createRecord
        .date(date)
        .description("Espectáculos")
        .name("Activity1")
        .responsibles(newDevelopment :: Nil)
        .spending(spending :: Nil)

      val newSchedule = Schedule.createRecord
        .activities(activity1 :: Nil)

      newSchedule.validate.length should equal (0)
    }
  }
}
