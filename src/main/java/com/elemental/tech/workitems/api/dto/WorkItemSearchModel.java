package com.elemental.tech.workitems.api.dto;

import com.elemental.tech.workitems.domain.WorkItem;

public record WorkItemSearchModel(
        String title,
        String description,
        WorkItem.Status status,
        WorkItem.Priority priority) {}
