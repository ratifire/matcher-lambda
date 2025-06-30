package entity

import enums.ParticipantType
import java.util.Date


data class ParticipantEntity(
    val id: Int,
    val participantId: Long,
    val specialization: String,
    val type: ParticipantType,
    val masteryLevel: Int,
    val desiredInterview: Int,
    val matchedInterview: Int,
    val active: Boolean,
    val hardSkills: Set<String>,
    val softSkills: Set<String>,
    val dates: Set<Date>,
    val averageMark: Double,
    val blackList: Set<Int>
)
