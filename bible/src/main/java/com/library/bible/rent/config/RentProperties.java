package com.library.bible.rent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

public enum RentProperties {
    DATE,
    RENEWAL_DATE,
    RENEWAL_COUNT,
    POSSIBLE_BOOK_COUNT,
    REQUEST_DATE;

    private int value;

    @Configuration
    public static class RentPropertiesConfig {
        @Value("${rent.date}")
        private int date;
        
        @Value("${rent.renewal-date}")
        private int renewalDate;
        
        @Value("${rent.renewal-count}")
        private int renewalCount;
        
        @Value("${rent.possible-book-count}")
        private int possibleBookCount;
        
        @Value("${rent.request-date}")
        private int requestDate;

        @PostConstruct
        public void init() {
            DATE.value = date;
            RENEWAL_DATE.value = renewalDate;
            RENEWAL_COUNT.value = renewalCount;
            POSSIBLE_BOOK_COUNT.value = possibleBookCount;
            REQUEST_DATE.value = requestDate;
        }
    }

    public int getValue() {
        return value;
    }
}
