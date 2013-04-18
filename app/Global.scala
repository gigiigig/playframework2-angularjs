import java.sql.Date
import java.util.Calendar
import model.{Tasks, Task}
import play.api.db.DB
import play.api._
import util.{Failure, Try, Success}

// Use H2Driver to connect to an H2 database

import scala.slick.driver.H2Driver.simple._
import play.api.Play.current

// Use the implicit threadLocalSession

import Database.threadLocalSession

/**
 * Created with IntelliJ IDEA.
 * User: luigi
 * Date: 18/04/13
 * Time: 00:19
 * To change this template use File | Settings | File Templates.
 */
object Global extends GlobalSettings {
  override def onStart(app: Application) {

    // Connect to the database and execute the following block within a session
    Database.forDataSource(DB.getDataSource()) withSession {
      // The session is never named explicitly. It is bound to the current
      // thread as the threadLocalSession that we imported

      // Create the tables, including primary and foreign keys
      Try(Tasks.ddl) match {
        case Success(tab) => Logger.logger.debug(s"database created ${tab}")
        case Failure(ex) => Logger.logger.warn(s"database creation error ${ex}")
      }

      def timeMillis = Calendar.getInstance().getTimeInMillis

      Tasks.insertAll(
        (Task(None, "task 1", new Date(timeMillis))),
        (Task(None, "task 2", new Date(timeMillis))),
        (Task(None, "task 3", new Date(timeMillis)))
      )

    }
  }

}
