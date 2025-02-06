package com.library.bible.FlaskApi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flask")
public class FlaskController {
	
	private final FlaskClientService flaskClientService;
	
	public FlaskController(FlaskClientService flaskClientService) {
		this.flaskClientService = flaskClientService;
	}
	
	@GetMapping("/recommend/{input}")
	public ResponseEntity<String> getFlaskRecommendation(@PathVariable int userId, @RequestParam(defaultValue = "5") int n) {
        String response = flaskClientService.getRecommendation(userId, n);
        return ResponseEntity.ok(response);
    }
}
