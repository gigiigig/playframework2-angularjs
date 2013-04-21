package misc

import java.sql.Time
import java.util.Calendar
import misc.Util
import model.{Tasks, Task}
import play.api._
import util.{Failure, Try, Success}

// Use H2Driver to connect to an H2 database

import scala.slick.driver.H2Driver.simple._
import Util._

// Use the implicit threadLocalSession

import Database.threadLocalSession

/**
 * Created with IntelliJ IDEA.
 * User: luigi
 * Date: 18/04/13
 * Time: 00:19
 * To change this template use File | Settings | File Templates.
 */
object TestGlobal extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.logger.debug("test global")
  }

  def timeMillis = Calendar.getInstance().getTimeInMillis

}
