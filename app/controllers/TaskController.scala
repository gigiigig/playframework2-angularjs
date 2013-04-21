package controllers

import play.api._
import play.api.mvc._
import model.{Tasks, Task}
import java.sql.{Time, Date}
import play.api.Play.current

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.util.{Failure, Success, Try}
import java.util.Calendar

import misc.Util._
import misc.Loggable

object TaskController extends Controller with Loggable {

  def timeMillis = Calendar.getInstance().getTimeInMillis


  implicit val writeTask: OWrites[Task] = (
    (__ \ "id").write[Option[Int]] ~
      (__ \ "name").write[String] ~
      (__ \ "startDate").write[Time]
    )(unlift(Task.unapply))

  implicit val buildReadTask =
    (((__ \ "id").read[Option[Int]] ~
      (__ \ "name").read[String] ~
      (__ \ "startDate").read[Int] tupled)).map(v => Task(v._1 , v._2 ,new Time(v._3)))


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

  def delete(id: Int) = Action {
    dataBase withSession {
      (for (t <- Tasks if t.id === id) yield t).delete
      Ok("")
    }
  }

  def update = Action(parse.json) {
    implicit request =>
      request.body.validate[Seq[Task]].fold(
        valid = {
          tasks =>

            dataBase withSession {
              tasks.foreach {
                task =>
                  log.debug(s"task ${task.name} have time ${task.startDate}")
                  (for (t <- Tasks if t.id === task.id) yield t.startDate).update(task.startDate)
              }
              Ok("Tasks Updated")
            }

        },
        invalid = {
          e =>
            BadRequest
        }
      )
  }


}