package com.ratelimiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiController {

    // Redirect root "/" to "/api/health"
    @GetMapping("/")
    public void root(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/health");
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "rate-limiter-service");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/data")
    public ResponseEntity<Map<String, Object>> getData() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toEpochMilli());
        response.put("message", "Request processed successfully");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}
