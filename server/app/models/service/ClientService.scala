package tech.vauldex.clock.me
package models.service

import java.util.UUID
import java.security.{ MessageDigest, SecureRandom }
import javax.inject.{ Inject, Singleton, Named }
import scala.concurrent.{ ExecutionContext, Future }
import akka.actor.ActorRef
import play.api.Configuration
import play.api.libs.json.{ Json, JsValue }
import cats.data.{ EitherT, OptionT }
import cats.implicits._
import jobs.DataKeepingLimit._
import models.domain._
import models.repo._
import services.S3Service
import utils._
import utils.Constant.{ CancelValue, ClientValue }

@Singleton
class ClientServiceV1 @Inject()(
    clientRepo: ClientRepo,
    workspacePermissionRepo: WorkspacePermissionRepo,
    conf: Configuration,
    s3Service: S3Service,
    @Named("data-keeping-actor") dataKeeping: ActorRef,
    protected implicit val ec: ExecutionContext)
  extends StatusErrors {

  def add(name: String, wsMember: WorkspaceMember): EitherT[Future, ServiceError, Client] = {
    val client = Client.from(name, wsMember.idWorkspace)
    ioActionResult(clientRepo.add(client), CLIENT_ADD_ERROR, client)
  }

  def updateName(
      id: UUID,
      newName: String,
      wsMember: WorkspaceMember): EitherT[Future, ServiceError, Unit] = {
    ioActionResult( clientRepo.updateName(id, newName), CLIENT_UPDATE_ERROR, ())
  }

  def updateAvatar(
      idWorkspace: UUID,
      id: UUID)(file: java.io.File, ext: String): EitherT[Future, ServiceError, String] = {
    val url = s"${idWorkspace}-${id}-${java.time.Instant.now}.$ext"
    val endpointUrl = conf.get[String]("aws.s3.endpoint-url")
    lazy val bucket = conf.get[String]("aws.s3.bucket")
    ioActionResult(
      s3Service.upload(url, file).flatMap { _ =>
        clientRepo.updateAvatar(id, url)
      },
      CLIENT_AVATAT_UPDATE_ERROR,
      s"$endpointUrl/$bucket/$url"
    )
  }

  def delete(id: UUID, wsMember: WorkspaceMember): EitherT[Future, ServiceError, Unit] = {
    ioActionResult(clientRepo.delete(id), CLIENT_DELETE_ERROR, ())
  }

  def pseudoDelete(id: UUID): EitherT[Future, ServiceError, Unit] = {
    ioActionResult(clientRepo.pseudoDelete(id), CLIENT_DELETE_ERROR, ())
      .map { _ =>
        dataKeeping ! Record(id, ClientValue)
      }
  }

  def restore(id: UUID): EitherT[Future, ServiceError, Client] = {
    for {
      client <- clientRepo.find(id).toRight(CLIENT_NOT_FOUND_ERROR)
      _ <- ioActionResult(clientRepo.restore(id), CLIENT_DELETE_ERROR, ())
    } yield {
      dataKeeping ! Record(id, ClientValue, CancelValue)
      client
    }
  }
}
