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

  def name = column[String]("name")

  def startDate = column[Time]("start_date")

  def running = column[Boolean]("running")

  def * = id.? ~ name ~ startDate ~ running  <>(Task, Task.unapply(_))

  def count = {
    dataBase withSession {
      (for (t <- Tasks) yield t.length).first
    }
  }

}

case class Task(id: Option[Int], name: String, startDate: Time , running: Boolean)

