package entity

import enums.ParticipantType
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
data class ParticipantEntity(
    @get:DynamoDbPartitionKey
    var participantId: Long? = null,
    var specialization: String? = null,
    var type: ParticipantType? = null,
    var masteryLevel: Int? = null,
    var desiredInterview: Int? = null,
    var matchedInterview: Int? = null,
    var active: Boolean? = null,
    var hardSkills: Set<String>? = null,
    var softSkills: Set<String>? = null,
    var dates: Set<String>? = null,
    var averageMark: Double? = null,
    var blackList: Set<Int>? = null
)
