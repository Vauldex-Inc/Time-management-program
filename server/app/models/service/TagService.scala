package tech.vauldex.clock.me
package models.service

import java.util.UUID
import javax.inject.{ Inject, Singleton, Named }
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Try, Success, Failure }
import play.api.i18n._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import akka.actor.ActorRef
import actors.Event._
import actors.ElasticSearchActor
import cats.data.EitherT
import cats.implicits._
import jobs.DataKeepingLimit._
import models.domain.{ Tag => TimestampTag }
import models.dao._
import models.repo._
import utils._
import utils.Constant.{ CancelValue, TagValue }

@Singleton
class TagServiceV1 @Inject()(
    protected val workspacePermissionRepo: WorkspacePermissionRepo,
    protected val workspaceMemberRepo: WorkspaceMemberRepo,
    protected val tagRepo: TagRepo,
    @Named("clock-me-manager") manager: ActorRef,
    @Named("elastic-search-actor") elasticSearchActor: ActorRef,
    @Named("data-keeping-actor") dataKeeping: ActorRef,
    protected val dbConfigProvider: DatabaseConfigProvider,
    implicit val messagesApi: MessagesApi,
    protected implicit val ec: ExecutionContext)
  extends HasDatabaseConfigProvider[db.PostgresProfile]
  with I18nSupport
  with StatusErrors {
  import profile.api._

  def getWorkspaceTags(idWorkspace: UUID): Future[Seq[TimestampTag]] = {
    tagRepo.getWorkspaceTags(idWorkspace)
  }

  def createTag(
      idUser: UUID, 
      tag: models.domain.Tag): EitherT[Future, ServiceError, models.domain.Tag] =  {
    ioActionResult(tagRepo.add(tag), TAG_ADD_ERROR, tag)
      .map { tag =>
        manager ! TagCreate(tag)
        tag
      }
  }

  def updateTag(id: UUID, newName: String): EitherT[Future, ServiceError, Unit] = {
    ioActionResult(tagRepo.update(id, newName), TAG_UPDATE_ERROR, ())
      .map(_ => elasticSearchActor ! ElasticSearchActor.UpdateTag(id, newName))
  }

  def deleteTag(id: UUID, idWorkspace: UUID): EitherT[Future, ServiceError, Unit] = {
    ioActionResult(tagRepo.delete(id), TAG_DELETE_ERROR, ())
      .map(_ => manager ! TagDelete(id, idWorkspace))
  }

   def pseudoDelete(id: UUID, idWorkspace: UUID): EitherT[Future, ServiceError, Unit] = {
    for {
      tag <- ioActionResult(tagRepo.pseudoDelete(id), TAG_DELETE_ERROR, ())
      ttag <- EitherT.right[ServiceError](tagRepo.pseudoDeleteTimestampTags(id))
    } yield {
      dataKeeping ! Record(id, TagValue)
      manager ! TagDelete(id, idWorkspace)
    }
  }

  def restore(id: UUID, idWorkspace: UUID): EitherT[Future, ServiceError, models.domain.Tag] = {
    for {
      tag <- tagRepo.find(id).toRight(TAG_NOT_FOUND_ERROR)
      _ <- ioActionResult(tagRepo.restoreTimestampTag(id), TAG_RESTORE_ERROR, id)
      result <- ioActionResult(tagRepo.restore(id), TAG_RESTORE_ERROR, id)
    } yield {
      manager ! TagCreate(tag)
      dataKeeping ! Record(id, TagValue, CancelValue)
      tag
    }
  }
}
