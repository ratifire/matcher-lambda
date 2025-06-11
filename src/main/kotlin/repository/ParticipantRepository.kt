package repository

import entity.ParticipantEntity
import enums.ParticipantType
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
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

    fun findMatch(participant: ParticipantEntity): List<ParticipantEntity> {
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

        val tableSchema = TableSchema.fromBean(ParticipantEntityDynamo::class.java)
        val result = dynamoDbClient.executeStatement {
            it.statement(query.toString())
                .parameters(parameters)
        }

        return result.items()
            .map { tableSchema.mapToItem(it) }
            .map {
                ParticipantEntity(
                    participantId = it.participantId,
                    specialization = it.specialization,
                    type = it.type,
                    masteryLevel = it.masteryLevel,
                    desiredInterview = it.desiredInterview,
                    matchedInterview = it.matchedInterview,
                    active = it.active,
                    hardSkills = it.hardSkills,
                    softSkills = it.softSkills,
                    dates = it.dates,
                    averageMark = it.averageMark,
                    blackList = it.blackList,
                )
            }
    }

    fun save(participant: ParticipantEntity) {
        participantTable.putItem(participant)
    }

    fun delete(participant: ParticipantEntity) {
        val key = Key.builder()
            .partitionValue(participant.participantId)
            .build()
        participantTable.deleteItem { it.key(key) }
    }
}


private fun convert(item: Map<String, AttributeValue>) =  ParticipantEntity(
        participantId = item["participantId"]?.n()?.toLongOrNull()?: error("Participant ID must not be null"),
        specialization = item["specialization"]?.n() ?: error("Specialization must not be null"),
        type = ParticipantType.valueOf(item["type"]?.s()?.toString()?: error("Type must not be null")),
        masteryLevel = item["masteryLevel"]?.n()?.toIntOrNull() ?: error("Mastery level must not be null"),
        desiredInterview = item["desiredInterview"]?.n()?.toIntOrNull() ?: error("Desired interview must not be null"),
        matchedInterview = item["matchedInterview"]?.n()?.toIntOrNull() ?: error("Matched interview must not be null"),
        active = item["active"]?.bool() ?: error("Active must not be null"),
        hardSkills = item["hardSkills"]?.l()?.map{it.toString()}?.toSet() ?: error("hardSkills cannot be null"),
        softSkills = item["softSkills"]?.l()?.map{ it.toString()}?.toSet() ?: error("softSkills cannot be null"),
        dates = item["dates"]?.l()?.map{it.toString()}?.toSet() ?: error("Dates cannot be null"),
        averageMark =  item["averageMark"]?.n()?.toDoubleOrNull() ?: error("averageMark cannot be null"),
        blackList = item["balckList"]?.l()?.map{ it.n().toInt()}?.toSet() ?: error("blackList cannot be null")
    )


@DynamoDbBean
data class ParticipantEntityDynamo(
    @get:DynamoDbPartitionKey
    var participantId: Long = 0,
    var specialization: String = "",
    var type: ParticipantType = ParticipantType.CANDIDATE,
    var masteryLevel: Int = 0,
    var desiredInterview: Int = 0,
    var matchedInterview: Int = 0,
    var active: Boolean = false,
    var hardSkills: Set<String> = emptySet(),
    var softSkills: Set<String> = emptySet(),
    var dates: Set<String> = emptySet(),
    var averageMark: Double = 0.0,
    var blackList: Set<Int> = emptySet()
)