
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import entity.ParticipantEntity
import repository.ParticipantRepository

data class Output(val message: String)

class Main : RequestHandler<Map<String, Any>, Output> {
    private val participantRepository = ParticipantRepository()

    override fun handleRequest(input: Map<String, Any>, context: Context): Output {

         val participant = ParticipantEntity(
            name = "bob",
            role = "developer",
            participantId = "12"
        )

        participantRepository.save(participant)
        return Output(message = "participant created")
    }
}