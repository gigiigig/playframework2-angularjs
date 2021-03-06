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
import slick.lifted

object TaskController extends Controller with Secured with Loggable {

  def timeMillis = Calendar.getInstance().getTimeInMillis


  implicit val writeTask: OWrites[Task] = (
    (__ \ "id").write[Option[Int]] ~
      (__ \ "name").write[String] ~
      (__ \ "startDate").write[Time] ~
      (__ \ "running").write[Boolean]
    )(t => (t.id, t.name, t.startDate, t.running))

  def index = withUser {
    user => implicit request =>
      Ok(views.html.index(Some(user)))
  }

  def list = withUser {
    user => implicit request =>
      dataBase withSession {
        val result = for {
          t <- Tasks
          if (t.userId === user.id)
        } yield (t)
        Ok(Json.toJson(result.sortBy(t => t.id.desc).list()))
      }
  }

  def add = withJsonUser {
    user => implicit request =>

      implicit val readNewTask =
        ((__ \ "name").read[String]).
          map(v => Task(None, user.id, v, new Time(0), false))

      request.body.validate[Task].fold(
        valid = {
          t =>
            dataBase withSession {
              Try(Tasks.insert(t)) match {
                case Success(r) => Ok(s"Created new task ${t.name}")
                case Failure(e) => BadRequest("Detected error:" + (e))
              }
            }

        },
        invalid = (e => BadRequest("Detected error:" + JsError.toFlatJson(e)))
      )
  }

  def delete(id: Int) = Action {
    dataBase withSession {
      val query: lifted.Query[Tasks.type, Task] = for (t <- Tasks if t.id === id) yield t
      val name = query.first.name
      query.delete
      Ok(s"Deleted task ${name}")
    }
  }

  def update = withJsonUser {
    user => implicit request =>

      implicit val readTask =
        (((__ \ "id").read[Option[Int]] ~
          (__ \ "name").read[String] ~
          (__ \ "startDate").read[Int] ~
          (__ \ "running").read[Boolean]
          tupled)).map(v => Task(v._1, None, v._2, new Time(v._3), v._4))

      request.body.validate[Seq[Task]].fold(
        valid = {
          tasks =>

            dataBase withSession {
              tasks.foreach {
                task =>
                  log.debug(s"task ${task.name} have time ${task.startDate}")
                  (for (t <- Tasks if t.id === task.id && t.userId === user.id)
                  yield t.startDate).update(task.startDate)
                  (for (t <- Tasks if t.id === task.id && t.userId === user.id)
                  yield t.running).update(task.running)
                  (for (t <- Tasks if t.id === task.id && t.userId === user.id)
                  yield t.name).update(task.name)
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