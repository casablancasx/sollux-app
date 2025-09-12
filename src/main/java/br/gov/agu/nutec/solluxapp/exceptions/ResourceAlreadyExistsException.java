package br.gov.agu.nutec.solluxapp.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {

    private String message;

    public ResourceAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }
}
