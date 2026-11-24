package com.elemental.tech.workitems.api.dto;

import com.elemental.tech.workitems.domain.WorkItem;
import jakarta.validation.constraints.NotNull;

public record PatchWorkItemRequest(
    @NotNull(message = "Version must be provided")
    Long version,
    @NotNull(message = "Status must be provided")
    WorkItem.Status status
){}
