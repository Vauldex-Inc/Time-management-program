package tech.vauldex.clock.me
package controllers

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import cats.implicits._
import auth.UserAction
import mock.ProjectMock
import models.domain.Tag
import models.domain.Implicits._
import models.repo.TagRepo
import models.service.TagService
import utils._
import utils.Constant._

@Singleton
class TagController @Inject()(
    tagService: TagService,
    userAction: UserAction,
    val controllerComponents: ControllerComponents,
    implicit val ec: ExecutionContext)
  extends BaseController with play.api.i18n.I18nSupport {

  private val createTagForm = Form(single("name" -> nonEmptyText))

  def createTag = userAction.allows(CrudTags).async { implicit request =>
    createTagForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      name => tagService
        .createTag(request.user.id, Tag.from(name, request.workspace.id, true))
        .fold(_.toResult, tag => Created(Json.toJson(tag)))
    )
  }

  def deleteTag(id: UUID) = userAction.allows(CrudTags).async { implicit request =>
    tagService.deleteTag(id, request.workspace.id).fold(_.toResult, _ => NoContent)
  }

  def getTags = userAction.async { implicit request =>
    tagService.getWorkspaceTags(request.workspace.id).map(tags => Ok(Json.toJson(tags)))
  }

  def updateTag(id: UUID) = userAction.allows(CrudTags).async { implicit request =>
    createTagForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      newName => tagService.updateTag(id, newName)
        .fold(_.toResult, _ => NoContent)
    )
  }

  private val groupEditForm = Form(single(
    "tags" -> list(
      tuple(
        "id" -> uuid,
        "name" -> nonEmptyText
      )
    )
  ))

  def groupUpdateTag = userAction.async { implicit request =>
    groupEditForm.bindFromRequest().fold(
      formWithError => Future.successful(BadRequest(formWithError.errorsAsJson)),
      {
        case (tags) => {
          traverseSequentially(tags.map(_._1)) { id =>
            tagService.updateTag(id, tags.filter(_._1 === id).head._2)
              .leftMap(error => (error, id))
              .swap
              .toOption
              .value
          }
          .map(id => Ok(Json.toJson(id.collect {
            case Some((error, id)) =>
              Json.obj("error" -> Json.obj("message" -> error.message, "status" -> error.status), "id" -> id)
          })))
        }
      }
    )
  }
}
