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
                    "have size of time slots") //todo add proper logging
        }

        val participantEntity = participantMapper.toEntity(participant)
        participantRepository.save(participantEntity)
        return participantEntity;
    }

    fun save(participant: ParticipantEntity): ParticipantEntity {
        participantRepository.save(participant)
        return participant;
    }

    fun delete(id: Int) {
        participantRepository.deleteById(id)
    }

    fun update(participant: ParticipantDto) {
        val participantOld = participantRepository
            .findById(participant.id)
            .let {
            participantMapper.toEntity(participant).copy(
                matchedInterview = it.matchedInterview,
                blackList = it.blackList,
                active = participant.desiredInterview > it.matchedInterview
            )
        }
        participantRepository.save(participantOld)
    }

    fun update(participant: ParticipantEntity){
        participantRepository.update(participant)
    }

    fun isParticipantRequestExist(participant: ParticipantDto) = participantRepository.exist(
        participant.participantId,
        participant.specialization,
        participant.masteryLevel,
        participant.type
    )
}