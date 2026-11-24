package com.elemental.tech.workitems.api;

import com.elemental.tech.workitems.exceptions.OptimisticLockingException;
import com.elemental.tech.workitems.exceptions.ResourceNotFoundException;
import com.elemental.tech.workitems.exceptions.StatusTransitionException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionController {

    private static final String ERRORS_KEY = "errors";
    private static final String TIMESTAMP_KEY = "timestamp";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        final ProblemDetail problemDetail = initialiseProblemDetail(HttpStatus.BAD_REQUEST, exception);
        problemDetail.setTitle("Validation Failed");
        final List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        final String errorMessage = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .distinct()
                .collect(Collectors.joining(", "));
        problemDetail.setDetail(errorMessage);
        return problemDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(final ResourceNotFoundException exception) {
        final ProblemDetail problemDetail = initialiseProblemDetail(HttpStatus.NOT_FOUND, exception);
        problemDetail.setTitle("Resource not found");
        return problemDetail;
    }

    @ExceptionHandler(StatusTransitionException.class)
    public ProblemDetail handleStatusTransitionException(final StatusTransitionException exception) {
        final ProblemDetail problemDetail = initialiseProblemDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception);
        problemDetail.setTitle("Status could not be changed");
        return problemDetail;
    }

    @ExceptionHandler(OptimisticLockingException.class)
    public ProblemDetail handleOptimisticLockingException(final OptimisticLockingException exception) {
        final ProblemDetail problemDetail = initialiseProblemDetail(HttpStatus.CONFLICT, exception);
        problemDetail.setTitle("Version provided in the request is out of date");
        return problemDetail;
    }

    private ProblemDetail initialiseProblemDetail(final HttpStatusCode httpStatusCode, final Exception exception){
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatusCode, exception.getMessage());
        problemDetail.setProperty(TIMESTAMP_KEY, OffsetDateTime.now());
        return problemDetail;
    }
}
