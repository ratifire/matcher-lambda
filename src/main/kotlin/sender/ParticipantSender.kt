package sender

import dto.PairedParticipantDto

class ParticipantSender {

    fun sendMatchedInterviewParticipants(pairedParticipantDto: PairedParticipantDto) {
        println("sending matched interview participants") // todo need to be changed (should send data to sqs paired queue)
        println(pairedParticipantDto)
    }
}