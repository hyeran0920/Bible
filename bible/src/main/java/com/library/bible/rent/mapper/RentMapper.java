package com.library.bible.rent.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.library.bible.rent.dto.RentResponse;
import com.library.bible.rent.model.Rent;

@Mapper(componentModel = "spring")
public interface RentMapper {
    List<RentResponse> toRentResponseList(List<Rent> rentList);
}