package repository

import dto.ParticipantDto
import entity.ParticipantEntity
import enums.ParticipantType
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class ParticipantRepository {

    private val tableName = "Participants"
    private val dynamoDbClient = DynamoDbClient.create()
    private val enhancedClient = DynamoDbEnhancedClient.builder()
        .dynamoDbClient(dynamoDbClient)
        .build()

    val participantTable = enhancedClient.table(
        tableName,
        TableSchema.fromBean(ParticipantEntity::class.java)
    )

    fun findMatch(participant: ParticipantDto): List<ParticipantEntity> {
        val query = StringBuilder(
            """
        SELECT * FROM "ParticipantTable"
        WHERE "type" <> ? 
          AND "specialization" = ?
          AND "active" = true
          AND "participantId" <> ? 
    """
        )

        val parameters = mutableListOf<AttributeValue>(
            AttributeValue.fromS(participant.type.name),
            AttributeValue.fromS(participant.specialization),
            AttributeValue.fromN(participant.participantId.toString()),
        )

        when (participant.type) {
            ParticipantType.CANDIDATE -> {
                query.append(
                    """
              AND (
                    "masteryLevel" > ? OR 
                   ("masteryLevel" = ? AND "averageMark" >= ?)
              )
            """
                )
                parameters.addAll(
                    listOf(
                        AttributeValue.fromN(participant.masteryLevel.toString()),
                        AttributeValue.fromN(participant.masteryLevel.toString()),
                        AttributeValue.fromN(participant.averageMark.toString())
                    )
                )
            }

            ParticipantType.INTERVIEWER -> {
                query.append(
                    """
              AND (
                    "masteryLevel" < ? OR 
                   ("masteryLevel" = ? AND "averageMark" <= ?)
              )
            """
                )
                parameters.addAll(
                    listOf(
                        AttributeValue.fromN(participant.masteryLevel.toString()),
                        AttributeValue.fromN(participant.masteryLevel.toString()),
                        AttributeValue.fromN(participant.averageMark.toString())
                    )
                )
            }
        }

        val tableSchema = TableSchema.fromBean(ParticipantEntity::class.java)
        val result = dynamoDbClient.executeStatement {
            it.statement(query.toString())
                .parameters(parameters)
        }

        return result.items()
            .map { tableSchema.mapToItem(it) }
    }

    fun save(participant: ParticipantEntity){
        participantTable.putItem(participant)
    }
    fun delete(participant: ParticipantEntity) {
        val key = Key.builder()
            .partitionValue(participant.participantId)
            .build()
        participantTable.deleteItem { it.key(key) }
    }
}