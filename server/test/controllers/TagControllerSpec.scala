package tech.vauldex.clock.me
package controllers

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.DoNotDiscover
import org.scalatestplus.play.PlaySpec
import play.api.mvc.{ Action, AnyContent, AnyContentAsJson, Results }
import play.api.test.Helpers._
import play.api.libs.json.{ Json, JsValue }
import helpers.TestValues
import models.domain.TimestampTag
import models.domain.Implicits._

@DoNotDiscover
class TagControllerSpec(test: TestValues) extends PlaySpec with Results {
  private val controllerName = "Tag Controller"
  lazy val userID = test.user.id

  s"The $controllerName #createTag" should {
    "create a tag with proper session " in {
      status(
        test.createTag
      ) mustEqual 201
    }

    "fail to create a tag without a proper session " in {
      status(
        test.doRequest(
          test.tagController.createTag(),
          POST,
          test.tagController.createTag().toString,
          Json.obj("name" -> "Test"),
          ("id", "userID.toString"))
      ) mustEqual 401
    }
  }

  s"The $controllerName #updateTag" should {
    "update a tag with proper session " in {
      status(
        test.doRequest(
          test.tagController.updateTag(test.tag.id),
          PATCH,
          test.tagController.updateTag(test.tag.id).toString,
          Json.obj("name" -> "New Test"),
          ("id", userID.toString),
          ("id_workspace", test.workspaceID.toString))
      ) mustEqual 204
    }

    "fail to create a tag without a proper session " in {
      status(
        test.doRequest(
          test.tagController.updateTag(test.tag.id),
          PATCH,
          test.tagController.updateTag(test.tag.id).toString,
          Json.obj("name" -> "New Test"),
          ("id", "userID.toString"),
          ("id_workspace", test.workspaceID.toString))
      ) mustEqual 401
    }
  }

  s"The $controllerName #deleteTag" should {
    "delete a tag with proper session " in {
      status(
        test.doRequest(
          test.tagController.deleteTag(test.tag.id),
          DELETE,
          test.tagController.deleteTag(test.tag.id).toString,
          ("id", userID.toString),
          ("id_workspace", test.workspaceID.toString))
      ) mustEqual 204
    }

    "fail to delete a tag without a proper session " in {
      status(
        test.doRequest(
          test.tagController.createTag(),
          DELETE,
          test.tagController.createTag().toString,
          ("id", "userID.toString"),
          ("id_workspace", test.workspaceID.toString))
      ) mustEqual 401
    }
  }
}
