package com.library.bible.i18n;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@SpringBootApplication
public class I18nApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(I18nApplication.class, args);
    }
	
}