package utils

import dto.ParticipantDto

fun validateParticipant(participant: ParticipantDto) = participant.dates.size >= participant.desiredInterview