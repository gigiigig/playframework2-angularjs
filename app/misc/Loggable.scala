package misc

import play.api.Logger

/**
 * Created with IntelliJ IDEA.
 * User: luigi
 * Date: 20/04/13
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
trait Loggable {
  lazy val log = Logger("app." + this.getClass)
}
