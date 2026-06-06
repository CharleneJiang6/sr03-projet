package fr.utc.sr03.model;

import java.time.Instant;
import java.util.Map;

/**
 * Generic API response wrapper for success and error messages.
 */
public class ApiResponse {
    private String message;
    private int status;
    private Instant timestamp;
    private Map<String, Object> details;

    public ApiResponse(String message, int status) {
        this(message, status, null);
    }

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(String message, int status, Map<String, Object> details) {
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
