package ch.makery.weather.view
import ch.makery.weather.model.Data
import ch.makery.weather.model.Weather
import ch.makery.weather.MainApp
import scalafx.scene.control.{TableView, TableColumn, Label}
import scalafxml.core.macros.sfxml
import scala.Tuple2
import scalafx.beans.property.{StringProperty} 

@sfxml
class WeatherOverviewController(

    private val cityLabel : Label,
    
    private val latitudeLabel : Label,
  
    private val longtitudeLabel : Label,

    private val avgTemperatureLabel : Label,

    private val weeklyTempTable :  TableView[Tuple2[StringProperty,StringProperty]],

    private val dailyTempTable :  TableView[Tuple2[StringProperty,StringProperty]],

    private val weekColumn :  TableColumn[Tuple2[StringProperty,StringProperty], String],

    private val dayColumn :  TableColumn[Tuple2[StringProperty,StringProperty], String],

    private val avgWeeklyTempColumn :  TableColumn[Tuple2[StringProperty,StringProperty], String],

    private val avgDailyTempColumn :  TableColumn[Tuple2[StringProperty,StringProperty], String],

    ) {
      var csvData: Weather = MainApp.csvData

      cityLabel.text <== csvData.location
      latitudeLabel.text <== csvData.latitude
      longtitudeLabel.text <== csvData.longtitude
      avgTemperatureLabel.text <== csvData.avgTemperatureInRange 
      weeklyTempTable.items = csvData.avgWeeklyTemperature
      dailyTempTable.items = csvData.avgDailyTemp
      weekColumn.cellValueFactory = {_.value._1}
      dayColumn.cellValueFactory = {_.value._1}
      avgWeeklyTempColumn.cellValueFactory  = {_.value._2} 
      avgDailyTempColumn.cellValueFactory  = {_.value._2} 
}
