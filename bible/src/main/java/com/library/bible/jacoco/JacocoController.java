package com.library.bible.jacoco;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JacocoController {
	
	@GetMapping("/test")
	public Integer test(@RequestParam int n) {
		return Integer.valueOf(n*2);
	}

}
