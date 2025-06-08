package entity

import enums.ParticipantType
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.util.*

@DynamoDbBean
data class ParticipantEntity(
    @get:DynamoDbPartitionKey
    val id: Int,
    val participantId: Long,
    val specialization: String,
    val type: ParticipantType,
    val masteryLevel: Int,
    val desiredInterview: Int,
    val matchedInterview: Int = 0,
    val active: Boolean,
    val hardSkills: Set<String>,
    val softSkills: Set<String>,
    val dates: Set<Date>,
    val averageMark: Double,
    val blackList: Set<Int>
)
