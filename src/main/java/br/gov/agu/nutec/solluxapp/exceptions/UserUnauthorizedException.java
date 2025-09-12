package br.gov.agu.nutec.solluxapp.exceptions;

public class UserUnauthorizedException extends RuntimeException {

    private String message;

    public UserUnauthorizedException(String message) {
        super(message);
        this.message = message;
    }
}
