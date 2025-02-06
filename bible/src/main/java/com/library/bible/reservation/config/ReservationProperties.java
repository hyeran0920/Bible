package com.library.bible.reservation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

public enum ReservationProperties {
    POSSIBLE_BOOK_COUNT;

    private int value; 

    @Configuration
    public static class ReservationPropertiesConfig {
        @Value("${reservation.possible-book-count}")
        private int possibleBookCount;

        @PostConstruct
        public void init() {
        	POSSIBLE_BOOK_COUNT.value = possibleBookCount;
        }
    }

    public int getValue() {
        return value;
    }
}
