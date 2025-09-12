package br.gov.agu.nutec.solluxapp.exceptions;

public class FileInvalidException extends RuntimeException {

    private String message;

    public FileInvalidException(String message) {
        super(message);
        this.message = message;
    }
}
