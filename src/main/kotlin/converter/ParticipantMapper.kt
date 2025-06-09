package converter

import dto.ParticipantDto
import entity.ParticipantEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import java.text.SimpleDateFormat
import java.util.Date

@Mapper

interface ParticipantMapper {

    @Mapping(target = "active", constant = "true")
    @Mapping(target = "matchedInterview", ignore = true)
    @Mapping(target = "dates", source = "dates", qualifiedByName = ["dateSetToStringSet"])
    fun toEntity(dto: ParticipantDto): ParticipantEntity

    companion object {
        @JvmStatic
        @Named("dateSetToStringSet")
        fun dateSetToStringSet(dates: Set<Date>?): Set<String> {
            if (dates == null) return emptySet()
            val formatter = SimpleDateFormat("yyyy-MM-dd") // or ISO 8601
            return dates.map { formatter.format(it) }.toSet()
        }
    }
}