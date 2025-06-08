package dto

import enums.ParticipantType
import java.util.*


data class ParticipantDto(
    val id: Int,
    val participantId: Long,
    val desiredInterview: Int,
    val type: ParticipantType,
    val specialization: String,
    val masteryLevel: Int,
    val hardSkills: Set<String>,
    val softSkills: Set<String>,
    val dates: Set<Date>,
    val averageMark: Double,
    val blackList: Set<Int>,
)

data class PairedParticipantDto(
    val interviewerId: Long,
    val candidateId: Long,
    val interviewerParticipantId: Int,
    val candidateParticipantId: Int,
    val date: Date
)

data class UpdateRequestDto(
    val desiredInterview: Int,
    val matchedInterview: Int,
    val dates: Set<Date>
)