package com.library.bible.rent.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.library.bible.rent.dto.RentResponse;
import com.library.bible.rent.model.Rent;

class RentMapperTest {

    private RentMapper rentMapper;

    @BeforeEach
    void setUp() {
        rentMapper = Mappers.getMapper(RentMapper.class);
    }

    @Test
    @DisplayName("Rent 리스트를 RentResponse 리스트로 변환하는지 테스트")
    void testRentsToRentResponses() {
        // Given
        List<Rent> rents = Arrays.asList(
            new Rent(1L, 101L, 1L, new Timestamp(System.currentTimeMillis()), null, null, null),
            new Rent(2L, 102L, 1L, new Timestamp(System.currentTimeMillis()), null, null, null)
        );

        // When
        List<RentResponse> rentResponses = rentMapper.rentsToRentResponses(rents);

        // Then
        assertNotNull(rentResponses);
        assertEquals(2, rentResponses.size());
        assertEquals(1L, rentResponses.get(0).getRentId());
        assertEquals(101L, rentResponses.get(0).getBookId());
    }
}
