package ch.makery.weather.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
import java.time.LocalDateTime;
import java.time.format.{ DateTimeFormatter }

class Data ( _dateTime : LocalDateTime, _temperature : Double ){
  var dateTime = ObjectProperty[LocalDateTime](_dateTime)
  var temperature = ObjectProperty[Double](_temperature)

  def formatDateTime: StringProperty = {
    var dataTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    StringProperty(dataTimeFormat.format(dateTime.value))
  }
  
}
