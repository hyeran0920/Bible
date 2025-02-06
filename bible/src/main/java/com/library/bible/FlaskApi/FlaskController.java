package com.library.bible.FlaskApi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flask")
public class FlaskController {
	
	private final FlaskClientService flaskClientService;
	
	public FlaskController(FlaskClientService flaskClientService) {
		this.flaskClientService = flaskClientService;
	}
	
	@GetMapping("/recommend/{input}")
	public String recommend(@PathVariable int input) {
		return flaskClientService.getRecommendation(input);
	}
}
