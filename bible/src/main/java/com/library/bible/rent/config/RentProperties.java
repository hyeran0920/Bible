package com.library.bible.rent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

public enum RentProperties {
    RENT_DATE,				// 대여일
    RENEWAL_DATE,			// 연장일
    RENEWAL_COUNT,			// 연장 가능 횟수
    POSSIBLE_BOOK_COUNT,	// 대여 가능한 도서 개수
    REQUEST_DATE;			// 대여 신청 후 대여까지 기간

    private int value; 

    @Configuration
    public static class RentPropertiesConfig {
        @Value("${rent.date}")
        private int rentDate;
        
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
        	RENT_DATE.value = rentDate;
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
