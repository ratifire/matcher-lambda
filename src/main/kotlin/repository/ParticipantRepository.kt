package repository

import entity.ParticipantEntity
import enums.ParticipantType
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import java.text.SimpleDateFormat

class ParticipantRepository {

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val tableName = System.getenv("DB_TABLE_NAME")
    private val dynamoDbClient = DynamoDbClient.create()

    fun findMatch(participant: ParticipantEntity): List<ParticipantEntity> {
        val query = StringBuilder(
            """
        SELECT * FROM "$tableName"
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

        return dynamoDbClient.executeStatement {
            it.statement(query.toString())
                .parameters(parameters)
        }
            .items()
            .map { convertTo(it) }
    }

    fun save(participant: ParticipantEntity) {

        val item = convertFrom(participant)
        val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()

        dynamoDbClient.putItem(request)
    }

    fun update(participant: ParticipantEntity) {
        val item = convertFrom(participant)

        val key = mapOf("id" to item["id"]!!)

        // Remove the key from the update fields
        val nonKeyAttributes = item.filterKeys { it != "id" }

        val updateExpression = nonKeyAttributes.keys.joinToString(", ") { "$it = :$it" }.let { "SET $it" }

        val attributeValues = nonKeyAttributes.mapKeys { ":${it.key}" }

        val request = UpdateItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .updateExpression(updateExpression)
            .expressionAttributeValues(attributeValues)
            .build()

        dynamoDbClient.updateItem(request)
    }

    fun deleteById(id: Int) {
        val key = mutableMapOf<String, AttributeValue>("id" to AttributeValue.fromN(id.toString()))

        val request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build()

        dynamoDbClient.deleteItem(request)
    }

    fun findById(id: Int) : ParticipantEntity {
        val key = mapOf("id" to AttributeValue.fromN(id.toString()))
        val request = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build()
        val response = dynamoDbClient.getItem(request)
        return response.item().let { convertTo(it)}
    }

    fun exist(participantId: Long, specialization: String, mastery: Int, type: ParticipantType): Boolean {
        val request = ScanRequest.builder()
            .tableName(tableName)
            .filterExpression("participantId = :pid and specialization = :spec and masteryLevel = :ml and #t = :type")
            .expressionAttributeValues(
                mapOf(
                    ":pid" to AttributeValue.fromN(participantId.toString()),
                    ":spec" to AttributeValue.fromS(specialization),
                    ":ml" to AttributeValue.fromN(mastery.toString()),
                    ":type" to AttributeValue.fromS(type.name)
                )
            )
            .expressionAttributeNames(mapOf("#t" to "type"))
            .limit(1) // early exit
            .build()

        val result = dynamoDbClient.scan(request)
        return result.count() > 0

    }

    private fun convertTo(item: Map<String, AttributeValue>) =  ParticipantEntity(
        id = item["id"]?.n()?.toIntOrNull()?: error("Participant ID must not be null"),
        participantId = item["participantId"]?.n()?.toLongOrNull()?: error("Participant ID must not be null"),
        specialization = item["specialization"]?.s() ?: error("Specialization must not be null"),
        type = ParticipantType.valueOf(item["type"]?.s()?.toString()?: error("Type must not be null")),
        masteryLevel = item["masteryLevel"]?.n()?.toIntOrNull() ?: error("Mastery level must not be null"),
        desiredInterview = item["desiredInterview"]?.n()?.toIntOrNull() ?: error("Desired interview must not be null"),
        matchedInterview = item["matchedInterview"]?.n()?.toIntOrNull() ?: error("Matched interview must not be null"),
        active = item["active"]?.bool() ?: error("Active must not be null"),
        hardSkills = item["hardSkills"]?.ss()?.map{it.toString()}?.toSet() ?: error("hardSkills cannot be null"),
        softSkills = item["softSkills"]?.ss()?.map{ it.toString()}?.toSet() ?: error("softSkills cannot be null"),
        dates = item["dates"]?.l()?.map{formatter.parse(it.s().toString())}?.toSet() ?: error("Dates cannot be null"),
        averageMark =  item["averageMark"]?.n()?.toDoubleOrNull() ?: error("averageMark cannot be null"),
        blackList = item["blackList"]?.l()?.map{ it.n().toInt()}?.toSet() ?: error("blackList cannot be null")
    )

    private fun convertFrom(participant: ParticipantEntity) =  mapOf(
            "id" to AttributeValue.fromN(participant.id.toString()),
            "participantId" to AttributeValue.fromN(participant.participantId.toString()),
            "specialization" to AttributeValue.fromS(participant.specialization),
            "type" to AttributeValue.fromS(participant.type.name),
            "masteryLevel" to AttributeValue.fromN(participant.masteryLevel.toString()),
            "desiredInterview" to AttributeValue.fromN(participant.desiredInterview.toString()),
            "matchedInterview" to AttributeValue.fromN(participant.matchedInterview.toString()),
            "active" to AttributeValue.fromBool(participant.active),
            "hardSkills" to AttributeValue.fromSs(participant.hardSkills.toList()),
            "softSkills" to AttributeValue.fromSs(participant.softSkills.toList()),
            "dates" to AttributeValue.fromL(participant.dates
                .map { AttributeValue.fromS(formatter.format(it)) }),
            "averageMark" to AttributeValue.fromN(participant.averageMark.toString()),
            "blackList" to AttributeValue.fromL(participant.blackList.map { AttributeValue.fromN(it.toString())})
        )
}
