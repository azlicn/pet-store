package com.petstore.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Response object for API errors (includes status, message, and timestamp)
 */
public class ErrorResponse {
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    
    /**
     * Creates an empty error response with current timestamp
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Creates a complete error response with all fields
     * 
     * @param status HTTP status code (e.g., 400, 404, 500)
     * @param error error type description (e.g., "Not Found", "Bad Request")
     * @param message detailed error message
     * @param path request URI that caused the error
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
    
    /**
     * @return time when the error occurred
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * @param timestamp time when the error occurred
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * @return HTTP status code
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * @param status HTTP status code
     */
    public void setStatus(int status) {
        this.status = status;
    }
    
    /**
     * @return error type description
     */
    public String getError() {
        return error;
    }
    
    /**
     * @param error error type description
     */
    public void setError(String error) {
        this.error = error;
    }
    
    /**
     * @return detailed error message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @param message detailed error message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * @return request URI that caused the error
     */
    public String getPath() {
        return path;
    }
    
    /**
     * @param path request URI that caused the error
     */
    public void setPath(String path) {
        this.path = path;
    }
}