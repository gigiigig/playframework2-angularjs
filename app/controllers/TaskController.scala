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
import scala.util.{Failure, Success, Try}
import slick.lifted

object TaskController extends Controller {

  implicit val writeTask: OWrites[Task] = (
    (__ \ "id").write[Option[Int]] ~
      (__ \ "name").write[String] ~
      (__ \ "startDate").write[Date]
    )(unlift(Task.unapply))

  implicit val readTask = (
    (__ \ "id").read[Option[Int]] ~
      (__ \ "name").read[String] ~
      (__ \ "startDate").read[Date]
    )(Task)


  def index = Action {
    Ok(views.html.index("Simple timer with Play 2"))
  }

  def list = Action {

    Database.forDataSource(DB.getDataSource()) withSession {

      val result = for {
        t <- Tasks
      } yield (t)

      Ok(Json.toJson(result.sortBy(t=> t.id.desc).list()))

    }

  }

  def add = Action(parse.json) {
    implicit request =>
      request.body.validate[Task].fold(
        valid = {
          t =>
            Database.forDataSource(DB.getDataSource()) withSession {
              Try(Tasks.insert(t)) match {
                case Success(t) => Ok(s"inserted ${t}")
                case Failure(e) => BadRequest("Detected error:" + (e))
              }
            }

        },
        invalid = (e => BadRequest("Detected error:" + JsError.toFlatJson(e)))
      )
  }


}