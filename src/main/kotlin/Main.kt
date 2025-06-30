
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import converter.ParticipantMapper
import dto.ParticipantDto
import enums.SqsMessageType
import org.mapstruct.factory.Mappers
import org.ratifire.matcherservice.service.MatchingService
import repository.ParticipantRepository
import sender.ParticipantSender
import service.ParticipantFacade
import service.ParticipantService

data class Output(val message: String)

class Main : RequestHandler<Map<String, Any>, Output> {

    private val objectMapper = jacksonObjectMapper()
    private val mapper = Mappers.getMapper(ParticipantMapper::class.java)
    private val participantRepository = ParticipantRepository()
    private val participantService = ParticipantService(participantRepository, mapper)
    private val matchingService = MatchingService(participantRepository, participantService, ParticipantSender())
    private val participantFacade = ParticipantFacade(
        participantService,
        matchingService,
        mapper
    )

    override fun handleRequest(input: Map<String, Any>, context: Context): Output {

        val record = ((input["Records"] as? List<*>)?.firstOrNull() as Map<*, *>)
        val headers = record["messageAttributes"] as Map<*,*>
        val body = record["body"] as String

        val messageType = (headers["messageType"] as Map<*,*>).let {
            SqsMessageType.valueOf(it["stringValue"] as String) }

            when (messageType) {

                SqsMessageType.CREATE -> {
                   objectMapper.readValue<ParticipantDto>(body).let {
                       participantFacade.processNewParticipant(it)
                   }
                }
                SqsMessageType.UPDATE -> {
                    objectMapper.readValue<ParticipantDto>(body)
                        .let { participantService.update(it) }

                }
                SqsMessageType.DELETE ->{
                    objectMapper.readTree(body)["id"].asInt()
                        .let { participantService.delete(it) }
                }
            }

            return Output(message = "participant created")
    }
}