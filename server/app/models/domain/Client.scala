package tech.vauldex.clock.me
package models.domain

import java.util.UUID

case class Client(
    id: UUID,
    name: String,
    idWorkspace: UUID,
    avatar: Option[String],
    deletedAt: Option[java.time.Instant]) {

  def toInitData = InitData.Client(id, name, avatar)
}

object Client {

  def from(name: String, idWorkspace: UUID, avatar: Option[String] = None): Client = {
    Client(UUID.randomUUID, name, idWorkspace, avatar, None)
  }
}
