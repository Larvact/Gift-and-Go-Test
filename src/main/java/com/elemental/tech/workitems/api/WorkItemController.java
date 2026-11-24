package com.elemental.tech.workitems.api;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.service.WorkItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/work-items")
public class WorkItemController {

    private final WorkItemService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public WorkItemResponse create (@Valid @RequestBody CreateWorkItemRequest request) {
        return service.create(request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkItemResponse> list() {
        return service.list();
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WorkItemResponse get(@PathVariable long id) {
        return service.get(id);
    }

    // TODO: implement PATCH endpoint for status transition with validation & concur strategy.
}
