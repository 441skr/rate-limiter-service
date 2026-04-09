package com.ratelimiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getData() {
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Request processed successfully",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
