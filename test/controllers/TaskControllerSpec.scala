package controllers

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

import model._
import scala.slick.driver.H2Driver.simple._
import java.sql.Time
import misc.TestUtil._
import Database.threadLocalSession
import misc._
import play.api.libs.json.Json

/**
 * Created with IntelliJ IDEA.
 * User: luigi
 * Date: 20/04/13
 * Time: 13:03
 * To change this template use File | Settings | File Templates.
 */
class TaskControllerSpec extends Specification with Loggable {

  lazy val config = (Map("global" -> "misc.TestGlobal") ++ inMemoryDatabase())


  "TaskController" should {

    "#add should create a new Task from posted json body" in new WithApplication(
      FakeApplication(additionalConfiguration = config)) {

      dataBase withSession {
        val body =
          """{"name":"test 3"}"""

        val count = Tasks.count

        val response = route(FakeRequest(POST, "/task/").withJsonBody(Json.parse(body))).get
        status(response) mustEqual (OK)

        Tasks.count must beEqualTo(count + 1)
      }

    }

    "#delete a task with passed id" in new WithApplication(
      FakeApplication(additionalConfiguration = config)) {

      dataBase withSession {

        Tasks.insert(Task(None, "test", new Time(434), false))
        val count = Tasks.count
        val home = route(FakeRequest(DELETE, "/task/1")).get

        status(home) must equalTo(OK)

        Tasks.count mustEqual (count - 1)

      }
    }

    "#update should update startTime for all tasks" in new WithApplication(
      FakeApplication(additionalConfiguration = config)) {

      dataBase withSession {

        val body =
          """[
              {"id":3,"name":"test 3","startDate":1000,"running":false},
              {"id":2,"name":"task 2","startDate":12000,"running":false},
              {"id":1,"name":"task 1","startDate":0,"running":true}
             ]"""

        val response = route(FakeRequest(PUT, "/task/").withJsonBody(Json.parse(body))).get

        log debug contentAsString(response)
        status(response) shouldEqual (OK)

        val q3 = for (t <- Tasks if t.id === 3) yield t
        val q2 = for (t <- Tasks if t.id === 2) yield t
        val q1 = for (t <- Tasks if t.id === 1) yield t

        q3.first.startDate shouldEqual (new Time(1000))
        q2.first.startDate shouldEqual (new Time(12000))
        q1.first.running should beEqualTo(true)

      }

    }
  }
}
