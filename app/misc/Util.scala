package misc

import play.api.db.DB
import slick.session.Database
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: luigi
 * Date: 19/04/13
 * Time: 19:16
 * To change this template use File | Settings | File Templates.
 */
object Util {
  lazy val dataBase = Database.forDataSource(DB.getDataSource())
}
