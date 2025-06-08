package repository

import entity.ParticipantEntity
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class ParticipantRepository {

     private val dynamoDbClient = DynamoDbClient.create()
     private val enhancedClient = DynamoDbEnhancedClient.builder()
         .dynamoDbClient(dynamoDbClient)
         .build()

     private val table: DynamoDbTable<ParticipantEntity> = enhancedClient.table(
         "Participants", TableSchema.fromBean(ParticipantEntity::class.java)
     )

     fun save(participant: ParticipantEntity) {
         table.putItem(participant)
     }
 }