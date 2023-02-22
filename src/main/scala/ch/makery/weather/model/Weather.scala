package ch.makery.weather.model

import scalafx.beans.property.{StringProperty}
import scalafx.collections.ObservableBuffer
import scala.collection.mutable.ListBuffer
import java.time.{LocalDateTime, LocalDate, DayOfWeek}
import java.time.temporal.{TemporalField, WeekFields, TemporalAdjusters}
import java.time.format.{ DateTimeFormatter }
import scala.io.Source
import java.time
import scala.Tuple3
import java.util.Locale
import java.time.temporal.IsoFields

class Weather ( loc : String, lat: String, long : String, var listOfTemp: ObservableBuffer[Data] ){
  var location: StringProperty = StringProperty(loc)
  var longtitude: StringProperty = StringProperty(long)
  var latitude: StringProperty = StringProperty(lat)

  /*
  Loop through temperature list created from csv and calculate average temperature by dividing the total temperature with the 
  total number of days.
  */
  def avgTemperatureInRange: StringProperty = {
    var totalTemp: Double = 0.0
    listOfTemp.foreach(totalTemp += _.temperature.value)
    StringProperty("%.2f".format(totalTemp/(listOfTemp.size)))
  }

  var dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")


  /*
  Loops through the temperature list created from the csv to create a linked list with date and the average temperature of that given date
  */
  private def sortByDay: ObservableBuffer[Tuple2[LocalDate, Double]] = {
    var dailyTempList: ObservableBuffer[Tuple3[LocalDate, Double, Int]] = new ObservableBuffer[Tuple3[LocalDate, Double, Int]]()

    listOfTemp.foreach(data => {
      
      var localDate = data.dateTime.value.toLocalDate()

      var dayIndex = dailyTempList.indexWhere((element) => element._1 == localDate )

      if (dayIndex != -1) {
        val newtotalTemp = dailyTempList(dayIndex)._2 + data.temperature.value
        val newLengthOfTempDataInThisDay= dailyTempList(dayIndex)._3 + 1
        dailyTempList(dayIndex) = (localDate, newtotalTemp, newLengthOfTempDataInThisDay)
      } else {
        dailyTempList += new Tuple3(localDate, data.temperature.value, 1)
      }
    })

    var dailyAvgTemp: ObservableBuffer[Tuple2[LocalDate, Double]] = dailyTempList.map( dayData => {
      new Tuple2(dayData._1, (dayData._2/dayData._3))
    })

    dailyAvgTemp
  }

  /*
  Maps the data from getAvgData to be presentable in the UI.
  */
  def avgDailyTemp: ObservableBuffer[Tuple2[StringProperty, StringProperty]] = {
    var getAvgData = sortByDay

    var tempByDay: ObservableBuffer[Tuple2[StringProperty, StringProperty]] = getAvgData.map( data => {
        var dayInfo: String = dateFormatter.format(data._1)

        new Tuple2(StringProperty(dayInfo), StringProperty("%.2f".format(data._2)))

    })
    tempByDay
  }

  /*
  Loop through sorted daily temperature list and calculate weekly average temperature by dividing the total temperature from the 
  same week with the number of records from the same week.
  */
  def avgWeeklyTemperature: ObservableBuffer[Tuple2[StringProperty, StringProperty]] = {
    var getAvgDailyData = sortByDay
    var weeklyTemp: ObservableBuffer[Tuple3[String, Double, Int]] = new ObservableBuffer[Tuple3[String, Double, Int]]()
    var totalTemp: Double = 0.0
   
    sortByDay.foreach(data => {
      var weekAndYear = isSameWeek(data._1)
      var weekIndex = weeklyTemp.indexWhere((element) => element._1 == weekAndYear)

      if (weekIndex != -1) {
        val newtotalTemp = weeklyTemp(weekIndex)._2 + data._2
        val newLengthOfTempDataInThisWeek = weeklyTemp(weekIndex)._3 + 1
        weeklyTemp(weekIndex) = (weekAndYear, newtotalTemp, newLengthOfTempDataInThisWeek)
      } else {
        weeklyTemp += new Tuple3(weekAndYear, data._2, 1)
      }
    })

    var tempByWeek: ObservableBuffer[Tuple2[StringProperty, StringProperty]] = weeklyTemp.map( data => {
        var weekOfYear: Long = data._1.split(",")(0).trim().toLong
        var year: Int = data._1.split(",")(1).trim().toInt
        var weekInfo: String = "Week %s \n%s".format(weekOfYear, getRangeFromWoy(weekOfYear, year))

        new Tuple2(StringProperty(weekInfo), StringProperty("%.2f".format(data._2 / data._3)))
    })
    tempByWeek
  }

  // return week and year of the temperature data
  private def isSameWeek(dateTime: LocalDate): String = {
    var woy: TemporalField = IsoFields.WEEK_OF_WEEK_BASED_YEAR
    var week: String = "%d, %d".format(dateTime.get(woy), dateTime.getYear())

    return week
  }

  /*
  Function to get range of week.
  */
  private def getRangeFromWoy(weekOfYear: Long, year: Int): String = {
    val weekRangeFrom: LocalDate = LocalDate.ofYearDay(year, 10)
      .`with`(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekOfYear)
      .`with`(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    val weekRangeTo: LocalDate = LocalDate.ofYearDay(year, 10)
      .`with`(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekOfYear)
      .`with`(TemporalAdjusters.next(DayOfWeek.SUNDAY));
    
    "(%s  -  %s)".format(dateFormatter.format(weekRangeFrom), dateFormatter.format(weekRangeTo))
    
  }
}
