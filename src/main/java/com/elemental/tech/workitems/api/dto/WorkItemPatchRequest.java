package com.elemental.tech.workitems.api.dto;

import com.elemental.tech.workitems.domain.WorkItem;
import jakarta.validation.constraints.NotNull;

public record WorkItemPatchRequest(
    @NotNull
    Long version,
    @NotNull
    WorkItem.Status status
){}
