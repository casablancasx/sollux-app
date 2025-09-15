package br.gov.agu.nutec.solluxapp.exceptions;

public class UserUnauthorizedException extends RuntimeException {

    public UserUnauthorizedException(String message) {
        super(message);
    }
}
