package sender

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dto.PairedParticipantDto
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

class ParticipantSender (
    private val sqsClient: SqsClient = SqsClient.create(),
    private val matchedParticipantQueueUrl: String = System.getenv("MATCHED_PARTICIPANT_QUEUE_URL")
) {

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    fun sendMatchedInterviewParticipants(pairedParticipantDto: PairedParticipantDto) {
        println(pairedParticipantDto)
        val payload = objectMapper.writeValueAsString(pairedParticipantDto)
        val request = SendMessageRequest.builder()
            .queueUrl(matchedParticipantQueueUrl)
            .messageBody(payload)
            .build()
        sqsClient.sendMessage(request)
    }
}