package br.gov.agu.nutec.solluxapp.exceptions.handler;

import br.gov.agu.nutec.solluxapp.exceptions.FileInvalidException;
import br.gov.agu.nutec.solluxapp.exceptions.ResourceAlreadyExistsException;
import br.gov.agu.nutec.solluxapp.exceptions.ResourceNotFoundException;
import br.gov.agu.nutec.solluxapp.exceptions.UserUnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class RestExceptionHandler {



    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        StandardError err = new StandardError(
                java.time.Instant.now(),
                404,
                "Resource Not Found",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(404).body(err);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<StandardError> resourceAlreadyExists(ResourceAlreadyExistsException e, HttpServletRequest request) {
        StandardError err = new StandardError(
                java.time.Instant.now(),
                409,
                "Resource Already Exists",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(409).body(err);
    }


    @ExceptionHandler(FileInvalidException.class)
    public ResponseEntity<StandardError> fileInvalid(FileInvalidException e, HttpServletRequest request) {
        StandardError err = new StandardError(
                java.time.Instant.now(),
                400,
                "File Invalid",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(400).body(err);
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<StandardError> userUnauthorized(UserUnauthorizedException e, HttpServletRequest request) {
        StandardError err = new StandardError(
                java.time.Instant.now(),
                401,
                "User Unauthorized",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(401).body(err);
    }

}
