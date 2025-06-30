package service

import converter.ParticipantMapper
import dto.ParticipantDto
import org.ratifire.matcherservice.service.MatchingService
import utils.validateParticipant

class ParticipantFacade(
    val participantService: ParticipantService,
    val matchingService: MatchingService,
    val mapper: ParticipantMapper) {

    fun processNewParticipant(participant: ParticipantDto){
        participantService.save(participant)
        matchingService.matchParticipant(mapper.toEntity(participant))
    }
}