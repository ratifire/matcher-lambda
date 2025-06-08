package entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
data class ParticipantEntity(
    @get:DynamoDbPartitionKey
    var participantId: String? = null,
    var name: String? = null,
    var role: String? = null
)
