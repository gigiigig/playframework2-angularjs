package model

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import java.sql.{Time, Date}
import java.util.Calendar

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

  def * = id.? ~ name ~ startDate <> (Task , Task.unapply(_))

}

case class Task(id: Option[Int], name: String, startDate: Time)

