package com.elemental.tech.workitems.api;

import com.elemental.tech.workitems.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionController {

    private static final String TIMESTAMP_KEY = "timestamp";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(final ResourceNotFoundException exception) {
        final ProblemDetail problemDetail = initialiseProblemDetail(HttpStatus.NOT_FOUND, exception);
        problemDetail.setTitle("Resource not found");
        return problemDetail;
    }

    private ProblemDetail initialiseProblemDetail(final HttpStatusCode httpStatusCode, final Exception exception){
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatusCode, exception.getMessage());
        problemDetail.setProperty(TIMESTAMP_KEY, OffsetDateTime.now());
        return problemDetail;
    }
}
