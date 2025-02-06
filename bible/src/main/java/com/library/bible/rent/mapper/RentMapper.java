package com.library.bible.rent.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.library.bible.rent.dto.RentResponse;
import com.library.bible.rent.model.Rent;

@Mapper(componentModel = "spring")
public interface RentMapper {
    RentMapper INSTANCE = Mappers.getMapper(RentMapper.class);

    RentResponse toRentResponse(Rent rent);
    List<RentResponse> toRentResponseList(List<Rent> rentList);
    default List<RentResponse> rentsToRentResponses(List<Rent> rentList) {
        return toRentResponseList(rentList);
    }
}