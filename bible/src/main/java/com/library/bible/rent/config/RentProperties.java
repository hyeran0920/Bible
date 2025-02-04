package com.library.bible.rent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class RentProperties {
    @Value("${rent.date}")
    private int DATE;
    
    @Value("${rent.renewal-date}")
    private int RENEWAL_DATE;
    
    @Value("${rent.renewal-count}")
    private int RENEWAL_COUNT;
    
    @Value("${rent.possible-book-count}")
    private int POSSIBLE_BOOK_COUNT;
    
    @Value("${rent.request-date}")
    private int REQUEST_DATE;
}
