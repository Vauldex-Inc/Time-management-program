package tech.vauldex.clock.me
package controllers

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc._
import play.api.mvc.MultipartFormData._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import cats.implicits._
import auth.UserAction
import mock.ProjectMock
import models.domain._
import models.domain.Implicits._
import models.repo.ClientRepo
import models.service.ClientServiceV1

import forms.ClientForm._
import utils._
import utils.Constant._

@Singleton
class ClientControllerV1 @Inject()(
    userAction: UserAction,
    clientRepo: ClientRepo,
    clientService: ClientServiceV1,
    val controllerComponents: ControllerComponents,
    implicit val ec: ExecutionContext)
  extends BaseController with play.api.i18n.I18nSupport {

  /**
   * The methods below belongs to:
   * @route /...
   */
    
  def addClient = userAction.allows(CrudClients).async { implicit request =>
    nameForm.bindFromRequest().fold(
      formWithError =>Future.successful(BadRequest(formWithError.errorsAsJson)),
      name => clientService.add(name, request.member)
        .fold(_.toResult, client => Ok(Json.toJson(client)))
    )
  }

  /** Upload and store image for client.*/
  def updateAvatar(id: UUID) = userAction.allows(CrudClients)
      .async(parse.multipartFormData) { implicit request =>
    request.body.file("file").map {
      case FilePart(_, fileName, contentType, file, size, _) =>
        val extension: Option[String]= utils.extension(fileName)
        extension.map { ext =>
          clientService.updateAvatar(request.workspace.id, id)(file, ext)
            .fold(_.toResult, avatar => Ok(avatar))
        }
        .getOrElse(Future.successful(BadRequest("Missing extension")))
    }
    .getOrElse(Future.successful(BadRequest("Missing file")))
  }

  def updateClientName(id: UUID) = userAction.allows(CrudClients).async { implicit request =>
    nameForm.bindFromRequest().fold(
      formWithError => Future.successful(BadRequest(formWithError.errorsAsJson)),
      newName => clientService.updateName(id, newName, request.member)
        .fold(_.toResult, _ => Ok)
    )
  }

  def deleteClient(id: UUID) = userAction.allows(CrudClients).async { implicit request =>
    clientService.delete(id, request.member).fold(_.toResult, _ => Ok)
  }

  def pseudoDelete(id: UUID) = userAction.allows(CrudClients).async { implicit request =>
    clientService.pseudoDelete(id).fold(_.toResult, _ => Ok)
  }

  def restore(id: UUID) = userAction.allows(CrudClients).async { implicit request =>
    clientService.restore(id).fold(_.toResult, c => Ok(Json.toJson(c)))
  }
}