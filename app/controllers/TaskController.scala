package controllers

import play.api._
import db.DB
import play.api.mvc._
import model.{Tasks, Task}
import slick.lifted.Query
import java.sql.{Time, Date}
import play.api.Play.current

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.util.{Failure, Success, Try}
import slick.lifted
import java.util.Calendar

import misc.Util._

object TaskController extends Controller {

  def timeMillis = Calendar.getInstance().getTimeInMillis


  implicit val writeTask: OWrites[Task] = (
    (__ \ "id").write[Option[Int]] ~
      (__ \ "name").write[String] ~
      (__ \ "startDate").write[Time]
    )(unlift(Task.unapply))

  implicit val readTask = (
      (__ \ "name").read[String]
    ).map(Task(None, _ , new Time(0)))


  def index = Action {
    Ok(views.html.index())
  }

  def list = Action {

    dataBase withSession {

      val result = for {
        t <- Tasks
      } yield (t)

      Ok(Json.toJson(result.sortBy(t => t.id.desc).list()))

    }

  }

  def add = Action(parse.json) {
    implicit request =>
      request.body.validate[Task].fold(
        valid = {
          t =>
           dataBase withSession {
              Try(Tasks.insert(t)) match {
                case Success(t) => Ok(s"inserted ${t}")
                case Failure(e) => BadRequest("Detected error:" + (e))
              }
            }

        },
        invalid = (e => BadRequest("Detected error:" + JsError.toFlatJson(e)))
      )
  }

  def delete(id: Integer) = Action {
    dataBase withSession {
      val task = for {
        t <- Tasks if t.id == id
      } yield t

      task.delete
    }
    Ok("")
  }

}