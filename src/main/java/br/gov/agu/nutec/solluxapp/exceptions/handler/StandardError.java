package br.gov.agu.nutec.solluxapp.exceptions.handler;

import java.time.Instant;

public record StandardError(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
}
