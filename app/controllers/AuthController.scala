package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import model.{Users, User}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import misc.Util._
import java.util.Calendar
import java.sql.Date
import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession


/**
 * Created with IntelliJ IDEA.
 * User: luigi
 * Date: 22/04/13
 * Time: 10:13
 * To change this template use File | Settings | File Templates.
 */
object AuthController extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying("Invalid email or password", result => result match {
      case (email, password) => check(email, password)
    })
  )

  val registerForm = Form(
    tuple(
      "email" -> text,
      "password" -> text,
      "password2" -> text
    ) verifying("Invalid email or password", result => result match {
      case (email, password, password2) => true
    })
  )


  def check(email: String, password: String) = {
    Users.checkEmailAndPassword(email, password)
  }

  def login = Action {
    implicit request =>
      Ok(views.html.login(loginForm, registerForm))
  }

  def authenticate = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        loginFormWithErrors => BadRequest(views.html.login(loginFormWithErrors, registerForm)),
        user => Redirect(routes.TaskController.index).withSession(Security.username -> user._1)
      )
  }

  def register = Action {
    implicit request =>
      registerForm.bindFromRequest.fold(
        registerFormWithErrors => BadRequest(views.html.login(loginForm, registerFormWithErrors)),
        formData =>
          dataBase withSession {
            Users.insert(User(None , formData._1 , formData._2 ,
              new Date(Calendar.getInstance.getTimeInMillis)) )
            Redirect(routes.TaskController.index).withSession(Security.username -> formData._1)
          }
      )
  }

  def logout = Action {
    Redirect(routes.AuthController.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }


}

trait Secured extends BodyParsers {

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.AuthController.login)

  def withAuth(f: => String => Request[AnyContent] => Result): EssentialAction = {
    Security.Authenticated(username, onUnauthorized) {
      user =>
        Action(request => f(user)(request))
    }
  }

  /**
   * This method shows how you could wrap the withAuth method to also fetch your user
   * You will need to implement UserDAO.findOneByUsername
   */
  def withUser(f: User => Request[AnyContent] => Result): EssentialAction = withAuth {
    username => implicit request =>
      Users.findByEmail(username).map {
        user =>
          f(user)(request)
      }.getOrElse(onUnauthorized(request))
  }

  def withJsonAuth(f: String => Request[JsValue] => Result) = {
    Security.Authenticated(username, onUnauthorized) {
      user =>
        Action(parse.json)(request => f(user)(request))
    }
  }

  def withJsonUser(f: User => Request[JsValue] => Result) = withJsonAuth {
    username => implicit request =>
      Users.findByEmail(username).map {
        user =>
          f(user)(request)
      }.getOrElse(onUnauthorized(request))
  }
}