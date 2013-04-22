package model

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import java.sql.Time
import misc.Util._

/**
 * Created with IntelliJ IDEA.
 * User: luigi
 * Date: 17/04/13
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
object Tasks extends Table[Task]("tasks") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Int]("user_id")

  def name = column[String]("name")

  def startDate = column[Time]("start_date")

  def running = column[Boolean]("running")

  def * = id.? ~ userId.? ~ name ~ startDate ~ running <>(Task, Task.unapply(_))

  def count: Int = {
    dataBase withSession {
      (for (t <- Tasks) yield t.length).first
    }
  }

  def findByUser(userId: Int): List[Task] = {
    dataBase withSession {
      (for (t <- Tasks if t.userId === userId) yield t) list
    }
  }

}

  case class Task(id: Option[Int], userId: Option[Int], name: String, startDate: Time, running: Boolean)

