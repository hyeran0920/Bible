package com.library.bible.rent.mapper;

import java.util.List;
import org.mapstruct.Mapper;

import com.library.bible.rent.dto.RentHistoryResponse;
import com.library.bible.rent.dto.RentResponse;
import com.library.bible.rent.model.Rent;
import com.library.bible.rent.model.RentHistory;

@Mapper(componentModel = "spring")
public interface RentHistoryMapper {
//	RentHistoryResponse rentAndRentHistoryToRentHistoryResponse(List<Rent> rents, RentHistory rentHistory);
//	List<RentResponse> rentsToRentResponses(List<Rent> rents);
}
