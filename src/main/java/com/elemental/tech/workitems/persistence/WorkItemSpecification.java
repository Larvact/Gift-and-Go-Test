package com.elemental.tech.workitems.persistence;

import com.elemental.tech.workitems.domain.WorkItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkItemSpecification {

    public static Specification<WorkItem> hasTitle(final String title) {
        return (root, query, cb) ->
                title == null ? null : cb.equal(root.get("title"), title);
    }

    public static Specification<WorkItem> hasDescription(final String description) {
        return (root, query, cb) ->
                description == null ? null : cb.equal(root.get("description"), description);
    }

    public static Specification<WorkItem> hasStatus(final WorkItem.Status status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<WorkItem> hasPriority(final WorkItem.Priority priority) {
        return (root, query, cb) ->
                priority == null ? null : cb.equal(root.get("priority"), priority);
    }
}

