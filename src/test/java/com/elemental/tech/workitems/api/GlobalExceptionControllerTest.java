package com.elemental.tech.workitems.api;

import com.elemental.tech.workitems.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionControllerTest {

    private static final String EXCEPTION_MESSAGE = "Exception Raised";

    private GlobalExceptionController globalExceptionController;

    @BeforeEach
    void setup(){
        globalExceptionController = new GlobalExceptionController();
    }

    @Test
    void handleResourceNotFoundException_problemDetailGenerated(){
        final ResourceNotFoundException exception = new ResourceNotFoundException(EXCEPTION_MESSAGE);

        final ProblemDetail problemDetail = globalExceptionController.handleResourceNotFoundException(exception);

        assertThat(problemDetail)
                .extracting(
                        ProblemDetail::getTitle,
                        ProblemDetail::getStatus,
                        ProblemDetail::getDetail)
                .containsExactly("Resource not found", 404, EXCEPTION_MESSAGE);
        assertThat(problemDetail.getProperties())
                .extracting(p -> p.get("timestamp"))
                .isNotNull();
    }

}