package tech.vauldex.clock.me
package models.domain

import java.util.UUID

case class Tag(
    id: UUID,
    name: String,
    idWorkspace: java.util.UUID,
    isDeletable: Boolean,
    deletedAt: Option[java.time.Instant]) {

  def toInitData = InitData.Tag(id, name, isDeletable)
}

object Tag {
	val tupled = (apply _).tupled

  def from(name: String, idWorkspace: UUID, isDeletable: Boolean): Tag = {
    Tag(UUID.randomUUID, name, idWorkspace, isDeletable, None)
  }
}
