package com.example.demo.asset_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ HANDLE BUSINESS ERRORS
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 🔥 400 instead of 500
                .body(Map.of(
                        "error", ex.getMessage()
                ));
    }
}