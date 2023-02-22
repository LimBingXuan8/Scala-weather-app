package ch.makery.weather
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{NoDependencyResolver, FXMLView, FXMLLoader}
import java.time.format.DateTimeFormatter
import scalafx.collections.ObservableBuffer
import java.util.Locale
import ch.makery.weather.model.{Data, Weather}
import javafx.{scene => jfxs}
import scala.io.Source
import scala.collection.mutable.ListBuffer
import java.time.LocalDateTime;

object MainApp extends JFXApp {
  // transform path of RootLayout.fxml to URI for resource location.
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  // initialize the loader object.
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  // Load root layout from fxml file.
  loader.load();
  // retrieve the root component BorderPane from the FXML 
  val roots = loader.getRoot[jfxs.layout.BorderPane]
  // initialize stage
  stage = new PrimaryStage {
    title = "WeatherApp"
    scene = new Scene {
      root = roots
    }
  }
  // actions for display weather overview window 
  def showWeatherOverview() = {
    val resource = getClass.getResource("view/WeatherOverview.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  } 

  def csvParser(): Weather = {
    // CSV file is stored in resource folder. fromFile() is not used because it doesn't take relative paths as parameter.
    val f = io.Source.fromResource("dataexport_20200525T133810.csv");

    var tempList = new ListBuffer[Tuple2[String, String]]();
    var linkedList = new ListBuffer[Tuple2[String, String]]();

    // The first three lines of the csv files which contain the location, longitude and latitude data are stored in a list
    for (line <- f.getLines.take(3)) {
      val cols = line.split(",").map(_.trim)
      linkedList += new Tuple2(cols(0), cols(1))
    }

    // The iterator is already at the third line of the file, hence we skip the next 7 lines to start to get data from the 10th
    // line of the file which contains temperature data
    for (line <- f.getLines.drop(7)) { 
      val cols = line.split(",").map(_.trim)
      tempList += new Tuple2(cols(0), cols(1))
    }
    
    f.close

    var location, latitude, longtitude = ""
    var listOfTemp: ObservableBuffer[Data] = new ObservableBuffer[Data]()
    var dataTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm")

    linkedList.foreach(line => {
      line._1 match {
        case "location" => {
          location = line._2
        }
        case "lat" => {
          latitude = line._2
        }
        case "lon" => {
          longtitude = line._2
        }
      }
    })

    tempList.foreach(line => {
        listOfTemp += new Data(LocalDateTime.parse(line._1, dataTimeFormatter), line._2.toDouble)
    })
    
    new Weather(location, latitude, longtitude, listOfTemp)
  }

  var csvData: Weather = csvParser()

  // call to display WeatherOverview when app start
  showWeatherOverview()
}
