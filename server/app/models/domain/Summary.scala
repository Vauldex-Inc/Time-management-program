package tech.vauldex.clock.me
package models.domain

import java.time._
import java.util.UUID

case class Summary(
    totalWorkspaceTime: Long,
    totalBreakTime: Long,
    previousTotalWork: Long,
    previousTotalBreak: Long,
    averageEarlyIn: Long,
    averageLateOut: Long,
    averageBreak: Long,
    numberOfBreaks: Int,
    numberOfDrafts: Int,
    trackedDuration: Long,
    topCategories: Seq[Summary.Category],
    topProjects: Seq[Summary.Project],
    numberOfActivities: Int,
    averageActivities: Long
)

object Summary {

  case class Category(
      categoryId: String,
      duration: Long)

  case class Project(
      projectId: String,
      duration: Long)

}
