package converter

import dto.ParticipantDto
import entity.ParticipantEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface ParticipantMapper {
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "matchedInterview", ignore = true)
    fun toEntity(dto: ParticipantDto): ParticipantEntity
}