package com.elemental.tech.workitems.domain;

import java.time.Instant;
import java.util.function.Predicate;

import com.elemental.tech.workitems.exceptions.StatusTransitionException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "work_item")
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Priority priority;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public WorkItem(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = Status.OPEN;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
        touch();
    }

    public void setDescription(String description) {
        this.description = description;
        touch();
    }

    public void transitionTo(Status newStatus) {
        if(this.status != null && !this.status.isNewStatusValid().test(newStatus)){
            throw new StatusTransitionException(String.format("Cannot transition from status: %s to new status %s", this.status.name(), newStatus.name()));
        }
        this.status = newStatus;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    @RequiredArgsConstructor
    public enum Status {
        DONE {
            @Override
            Predicate<Status> isNewStatusValid() {
                return (newStatus) -> false;
            }
        },
        CANCELLED {
            @Override
            Predicate<Status> isNewStatusValid() {
                return (newStatus) -> false;
            }
        },
        IN_PROGRESS {
            @Override
            Predicate<Status> isNewStatusValid() {
                return (newStatus) -> newStatus == DONE || newStatus == CANCELLED;
            }
        },
        OPEN {
            @Override
            Predicate<Status> isNewStatusValid() {
                return (newStatus) -> newStatus == IN_PROGRESS || newStatus == CANCELLED;
            }
        };

        abstract Predicate<Status> isNewStatusValid();
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
}
