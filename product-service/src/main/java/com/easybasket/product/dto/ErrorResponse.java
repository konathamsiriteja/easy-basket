package com.easybasket.product.dto;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private List<String> fieldErrors;
    private Instant timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String error, String message, List<String> fieldErrors, Instant timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldErrors = fieldErrors;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
