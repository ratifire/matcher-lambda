package entity

import enums.ParticipantType
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
data class ParticipantEntity(
    @get:DynamoDbPartitionKey
    val participantId: Long,
    val specialization: String,
    val type: ParticipantType,
    val masteryLevel: Int,
    val desiredInterview: Int,
    val matchedInterview: Int,
    val active: Boolean,
    val hardSkills: Set<String>,
    val softSkills: Set<String>,
    val dates: Set<String>,
    val averageMark: Double,
    val blackList: Set<Int>
)
