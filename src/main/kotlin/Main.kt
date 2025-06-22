
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import converter.ParticipantMapper
import dto.ParticipantDto
import org.mapstruct.factory.Mappers
import repository.ParticipantRepository

data class Output(val message: String)

class Main : RequestHandler<Map<String, Any>, Output> {

    private val participantRepository = ParticipantRepository()
    private val objectMapper = jacksonObjectMapper()
    private val mapper = Mappers.getMapper(ParticipantMapper::class.java)

    override fun handleRequest(input: Map<String, Any>, context: Context): Output {
        val dto: ParticipantDto = objectMapper.convertValue(input, ParticipantDto::class.java)
        val entity = mapper.toEntity(dto)

      //  participantRepository.save(entity)
      //  participantRepository.findById(entity.id).let { println(it) }
        participantRepository.findMatch(entity).let { println(it)}
        return Output(message = "participant created")
    }
}