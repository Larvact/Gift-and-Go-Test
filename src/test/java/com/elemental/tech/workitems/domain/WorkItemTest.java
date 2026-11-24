package com.elemental.tech.workitems.domain;

import com.elemental.tech.workitems.exceptions.StatusTransitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkItemTest {

    @Test
    void currentStatusNull_transitionToStatus_statusTransitioned(){
        final WorkItem workItem = new WorkItem();

        workItem.transitionTo(WorkItem.Status.OPEN);

        assertEquals(WorkItem.Status.OPEN, workItem.getStatus());
    }

    private static Stream<Arguments> provideInvalidNewStatusesForDone() {
        return Stream.of(
                Arguments.of(WorkItem.Status.OPEN),
                Arguments.of(WorkItem.Status.IN_PROGRESS),
                Arguments.of(WorkItem.Status.CANCELLED),
                Arguments.of(WorkItem.Status.DONE)
        );
    }

    @MethodSource("provideInvalidNewStatusesForDone")
    @ParameterizedTest
    void currentStatusDone_transitionToInvalidStatus_StatusTransitionExceptionRaised(final WorkItem.Status newStatus){
        final WorkItem workItem = new WorkItem();

        workItem.transitionTo(WorkItem.Status.DONE);

        assertThrows(StatusTransitionException.class, () -> workItem.transitionTo(newStatus));
    }

    private static Stream<Arguments> provideInvalidNewStatusesForCancelled() {
        return Stream.of(
                Arguments.of(WorkItem.Status.OPEN),
                Arguments.of(WorkItem.Status.IN_PROGRESS),
                Arguments.of(WorkItem.Status.CANCELLED),
                Arguments.of(WorkItem.Status.DONE)
        );
    }

    @MethodSource("provideInvalidNewStatusesForCancelled")
    @ParameterizedTest
    void currentStatusCancelled_transitionToInvalidStatus_StatusTransitionExceptionRaised(final WorkItem.Status newStatus){
        final WorkItem workItem = new WorkItem();

        workItem.transitionTo(WorkItem.Status.CANCELLED);

        assertThrows(StatusTransitionException.class, () -> workItem.transitionTo(newStatus));
    }

    private static Stream<Arguments> provideInvalidNewStatusesForInProgress() {
        return Stream.of(
                Arguments.of(WorkItem.Status.OPEN),
                Arguments.of(WorkItem.Status.IN_PROGRESS)
        );
    }

    @MethodSource("provideInvalidNewStatusesForInProgress")
    @ParameterizedTest
    void currentStatusInProgress_transitionToInvalidStatus_StatusTransitionExceptionRaised(final WorkItem.Status newStatus){
        final WorkItem workItem = new WorkItem();

        workItem.transitionTo(WorkItem.Status.IN_PROGRESS);

        assertThrows(StatusTransitionException.class, () -> workItem.transitionTo(newStatus));
    }

    private static Stream<Arguments> provideInvalidNewStatusesForOpen() {
        return Stream.of(
                Arguments.of(WorkItem.Status.OPEN),
                Arguments.of(WorkItem.Status.DONE)
        );
    }

    @MethodSource("provideInvalidNewStatusesForOpen")
    @ParameterizedTest
    void currentStatusOpen_transitionToInvalidStatus_StatusTransitionExceptionRaised(final WorkItem.Status newStatus){
        final WorkItem workItem = new WorkItem();

        workItem.transitionTo(WorkItem.Status.OPEN);

        assertThrows(StatusTransitionException.class, () -> workItem.transitionTo(newStatus));
    }

}