package com.elemental.tech.workitems.api.dto;

import com.elemental.tech.workitems.domain.WorkItem;

public record PatchWorkItemModel (
    Long id,
    Long version,
    WorkItem.Status status
){}
