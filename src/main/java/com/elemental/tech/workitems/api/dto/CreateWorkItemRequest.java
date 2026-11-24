package com.elemental.tech.workitems.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
    TODO: please enhance with validation
 */
public record CreateWorkItemRequest(
        @NotNull(message = "Title must be provided") @NotBlank(message = "Title must be provided") @Size(max = 120)
        String title,
        @Size(max = 2000)
        String description,
        @NotNull(message = "Priority must be provided")
        Priority priority
) {

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
}
