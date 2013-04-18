package controllers

import play.api._
import db.DB
import play.api.mvc._
import model.{Tasks, Task}
import slick.lifted.Query
import java.sql.Date
import play.api.Play.current

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

import play.api.libs.json._
import play.api.libs.functional.syntax._

object Application extends Controller {

  implicit val worksJson: OWrites[Task] = (
    (__ \ "id").write[Option[Int]] ~
      (__ \ "name").write[String] ~
      (__ \ "startDate").write[Date]
    )(unlift(Task.unapply))

  def index = Action {
    Ok(views.html.index("Simple timer with Play 2"))
  }

  def rest = Action {

    Database.forDataSource(DB.getDataSource()) withSession {

      val result = for {
        t <- Tasks
      } yield (t)

      Ok(Json.toJson(result.list()))

    }

  }

  //  def time = Action {
  //    val df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
  //    Ok(df.format(new Date()))
  //  }

  case class Work(name: String, price: Float, date: Date)

}