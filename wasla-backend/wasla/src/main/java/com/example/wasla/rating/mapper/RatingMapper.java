package com.example.wasla.rating.mapper;

import com.example.wasla.rating.dto.RatingResponseDto;
import com.example.wasla.rating.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for Rating → RatingResponseDto.
 * Handles UUID to String conversion.
 */
@Mapper(componentModel = "spring", imports = {java.util.UUID.class})
public interface RatingMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    @Mapping(source = "job.id", target = "jobId", qualifiedByName = "uuidToString")
    RatingResponseDto toResponseDto(Rating rating);

    @Mapping(target = "job", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "driver", ignore = true)
    Rating toRating(com.example.wasla.rating.dto.CreateRatingRequest request);

    @Named("uuidToString")
    default String uuidToString(java.util.UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
}
