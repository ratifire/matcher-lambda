package service

import converter.ParticipantMapper
import dto.ParticipantDto
import entity.ParticipantEntity
import exeption.ParticipantException
import repository.ParticipantRepository
import utils.validateParticipant

class ParticipantService(
    val participantRepository: ParticipantRepository,
    val participantMapper: ParticipantMapper
) {

    fun save(participant: ParticipantDto): ParticipantEntity {
        if (!validateParticipant(participant) || isParticipantRequestExist(participant)) {
            throw ParticipantException("Participant object with ID " + participant.id + " is already registered or " +
                    "have size of time slots")
        }

        val participantEntity = participantMapper.toEntity(participant)
        return participantRepository.save(participantEntity)
    }

    fun save(participant: ParticipantEntity): ParticipantEntity {
        return participantRepository.save(participant)
    }

    fun delete(id: Int) {
        participantRepository.deleteById(id)
    }

    fun update(participant: ParticipantDto) {
        participantRepository.findById(participant.id)
            .map {
                participantMapper.toEntity(participant).copy(
                    matchedInterview = it.matchedInterview,
                    blackList = it.blackList,
                    active = participant.desiredInterview > it.matchedInterview
                )
            }
            .ifPresentOrElse({participantRepository.save(it)},
                { throw NoSuchElementException("Participant with id: ${participant.id} not found") })
    }

    fun isParticipantRequestExist(participant: ParticipantDto) = participantRepository.exist(
        participant.participantId,
        participant.specialization,
        participant.masteryLevel,
        participant.type
    )
}