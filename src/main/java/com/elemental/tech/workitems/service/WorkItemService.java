package com.elemental.tech.workitems.service;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.elemental.tech.workitems.api.dto.PatchWorkItemModel;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.api.dto.WorkItemSearchModel;
import com.elemental.tech.workitems.domain.WorkItem;
import com.elemental.tech.workitems.exceptions.OptimisticLockingException;
import com.elemental.tech.workitems.exceptions.ResourceNotFoundException;
import com.elemental.tech.workitems.persistence.WorkItemRepository;
import com.elemental.tech.workitems.persistence.WorkItemSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkItemService {

    private final WorkItemRepository repository;

    public WorkItemService(WorkItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WorkItemResponse create(CreateWorkItemRequest request) {
        WorkItem workItem = new WorkItem(
                request.title(),
                request.description(),
                WorkItem.Priority.valueOf(request.priority().name())
        );

        WorkItem saved = repository.save(workItem);
        return WorkItemMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkItemResponse> list() {
        return repository.findAll()
                .stream()
                .map(WorkItemMapper::toResponse)
                .toList();
    }

    public Page<WorkItemResponse> paginationList(final WorkItemSearchModel searchModel, final Pageable pageable) {
        final Specification<WorkItem> spec = Specification
                .where(WorkItemSpecification.hasTitle(searchModel.title()))
                .and(WorkItemSpecification.hasDescription(searchModel.description()))
                .and(WorkItemSpecification.hasStatus(searchModel.status()))
                .and(WorkItemSpecification.hasPriority(searchModel.priority()));
        final Page<WorkItem> results = repository.findAll(spec, pageable);
        return results.map(WorkItemMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public WorkItemResponse get(long id) {
        WorkItem workItem = findById(id);
        return WorkItemMapper.toResponse(workItem);
    }

    @Transactional
    public WorkItemResponse patch(final PatchWorkItemModel patchWorkItemModel) {
        final WorkItem workItem = findById(patchWorkItemModel.id());
        final long upToDateVersion = workItem.getVersion();
        final long requestVersion = patchWorkItemModel.version();
        if(requestVersion != upToDateVersion){
            throw new OptimisticLockingException(String.format("Failed to patch the WorkItem with id: %s as the requested version is out of date", patchWorkItemModel.id()));
        }
        workItem.transitionTo(patchWorkItemModel.status());
        WorkItem saved = repository.save(workItem);
        return WorkItemMapper.toResponse(saved);
    }

    private WorkItem findById(final long id){
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("work item not found: " + id));
    }
}
