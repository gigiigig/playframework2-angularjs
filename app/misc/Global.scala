package misc

import java.sql.{Time, Date}
import java.util.Calendar
import model.{User, Users, Tasks, Task}
import play.api._
import util.{Failure, Try, Success}

// Use H2Driver to connect to an H2 database

import scala.slick.driver.H2Driver.simple._
import play.api.Play.current
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
object Global extends GlobalSettings with Loggable {
  override def onStart(app: Application) {

    // Connect to the database and execute the following block within a session
    dataBase withSession {
      // The session is never named explicitly. It is bound to the current
      // thread as the threadLocalSession that we imported

      // Create the tables, including primary and foreign keys
      Try((Tasks.ddl ++ Users.ddl) create) match {
        case Success(tab) => log.debug(s"database created ${tab}")
        case Failure(ex) => log.warn(s"database creation error ${ex}")
      }


      Query(Query(Tasks).length).first match {

        case 0 =>

          Users.insert(User(Some(1), "l@i.it", "123", new Date(Calendar.getInstance.getTimeInMillis)))

          Tasks.insertAll(
            (Task(None, Some(1), "task 1", new Time(0), false)),
            (Task(None, Some(1), "task 2", new Time(0), false)),
            (Task(None, Some(1), "task 3", new Time(0), false))
          )

        case _ => Logger.logger.debug("tasks already exist")

      }
    }
  }

  def timeMillis = Calendar.getInstance().getTimeInMillis


}
