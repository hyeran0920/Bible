package com.library.bible.FlaskApi;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@GetMapping("/recommend")
    public ResponseEntity<String> getFlaskRecommendation(@RequestParam int user_id, @RequestParam(defaultValue = "5") int n) {
        String response = flaskClientService.getRecommendation(user_id, n);
        return ResponseEntity.ok(response);
    }
	
	@PostMapping("/recommend")
    public ResponseEntity<String> postFlaskRecommendation(@RequestBody Map<String, Object> request) {
        int userId = (int) request.get("user_id");
        int n = request.containsKey("n") ? (int) request.get("n") : 5;

        String response = flaskClientService.postRecommendation(userId, n);
        return ResponseEntity.ok(response);
    }

}