package model

import java.sql.Date

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import java.sql.Time
import misc.Util._

/**
 * Created with IntelliJ IDEA.
 * User: luigi
 * Date: 22/04/13
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */
object Users extends Table[User]("users"){

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def email = column[String]("email")

  def password = column[String]("password")

  def registrationDate = column[Date]("registration_date")

  def * = id.? ~ email ~ password ~ registrationDate <> (User, User.unapply(_))

  def findByEmail(email: String): Option[User] = {
    dataBase withSession{
      (for (u <- Users if u.email === email) yield u).firstOption
    }
  }

  def checkEmailAndPassword(email: String, password: String): Boolean = {
    dataBase withSession{
      (for (u <- Users if u.email === email && u.password === password) yield u)
        .firstOption.isDefined
    }
  }

}

case class User(id: Option[Int],email: String, password: String , registrationDate: Date)
