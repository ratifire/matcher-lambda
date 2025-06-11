package sender

import dto.PairedParticipantDto

class ParticipantSender {

    fun sendMatchedInterviewParticipants(pairedParticipantDto: PairedParticipantDto) {
        println("sending matched interview participants")
        println(pairedParticipantDto)
    }
}