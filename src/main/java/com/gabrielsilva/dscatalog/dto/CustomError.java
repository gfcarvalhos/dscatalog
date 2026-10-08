package com.gabrielsilva.dscatalog.dto;

import java.time.Instant;

public class CustomError {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;

    public CustomError(Instant timestamp, String path, String error, Integer status) {
        this.timestamp = timestamp;
        this.path = path;
        this.error = error;
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}
