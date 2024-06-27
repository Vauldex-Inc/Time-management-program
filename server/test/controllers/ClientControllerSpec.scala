package tech.vauldex.clock.me
package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.DoNotDiscover
import org.scalatestplus.play.PlaySpec
import play.api.mvc.{ Action, AnyContent, AnyContentAsJson, Results }
import play.api.test.Helpers._
import play.api.libs.json.{ Json, JsValue }
import helpers.TestValues

@DoNotDiscover
class ClientControllerSpec(test: TestValues) extends PlaySpec with Results {
  private val controllerName = "Client Controller"
  lazy val workspaceMemberID = test.workspaceMember.idUser
  lazy val userID = test.user.id
  lazy val projectID = test.project.id


  s"The $controllerName #addClient" should {
     "add client to workspace " in {
      status(
        test.doRequest(
          test.clientController.addClient(),
          POST,
          test.clientController.addClient().toString,
          Json.obj("name" -> "Test Client"),
          ("id", userID.toString),
          ("id_workspace", test.workspaceID.toString))
      ) mustEqual 200
    }
    "fail to add client without proper session " in {
      status(
        test.doRequest(
          test.clientController.addClient(),
          POST,
          test.clientController.addClient().toString,
          Json.obj("name" -> "Test Client"),
          ("id", "userID.toString"))
      ) mustEqual 401
    }
  }

  s"The $controllerName #updateClientName" should {
     "update client name in workspace " in {
      status(
        test.doRequest(
          test.clientController.updateClientName(test.client.id),
          PATCH,
          test.clientController.updateClientName(test.client.id).toString,
          Json.obj("name" -> "Test Client 2"),
          ("id", userID.toString),
          ("id_workspace", test.workspaceID.toString))
      ) mustEqual 200
    }
    "fail to update client name without proper session " in {
      status(
        test.doRequest(
          test.clientController.updateClientName(test.client.id),
          PATCH,
          test.clientController.updateClientName(test.client.id).toString,
          Json.obj("name" -> "Test Client 2"),
          ("id", "userID.toString"))
      ) mustEqual 401
    }
  }

  s"The $controllerName #deleteClient" should {
     "delete client in workspace " in {
      status(
        test.doRequest(
          test.clientController.deleteClient(test.client.id),
          DELETE,
          test.clientController.deleteClient(test.client.id).toString,
          ("id", userID.toString),
          ("id_workspace", test.workspaceID.toString))
      ) mustEqual 200
    }
    "fail to delete client without proper session " in {
      status(
        test.doRequest(
          test.clientController.deleteClient(test.client.id),
          DELETE,
          test.clientController.deleteClient(test.client.id).toString,
          ("id", "userID.toString"))
      ) mustEqual 401
    }
  }
}
