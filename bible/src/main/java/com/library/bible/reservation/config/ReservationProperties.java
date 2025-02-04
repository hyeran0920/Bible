package com.library.bible.reservation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class ReservationProperties {
    @Value("${reservation.possible-book-count}")
    private int POSSIBLE_BOOK_COUNT;
}
