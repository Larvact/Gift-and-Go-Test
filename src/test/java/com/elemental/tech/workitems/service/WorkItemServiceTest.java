package com.elemental.tech.workitems.service;

import com.elemental.tech.workitems.api.dto.PatchWorkItemModel;
import com.elemental.tech.workitems.domain.WorkItem;
import com.elemental.tech.workitems.exceptions.OptimisticLockingException;
import com.elemental.tech.workitems.exceptions.ResourceNotFoundException;
import com.elemental.tech.workitems.persistence.WorkItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = WorkItemService.class)
@ExtendWith(SpringExtension.class)
class WorkItemServiceTest {

    private static final long ID = 1;

    @MockitoBean
    private WorkItemRepository workItemRepository;
    @Autowired
    private WorkItemService workItemService;

    @Test
    void noWorkItemInDb_get_throwsResourceNotFoundException(){
        when(workItemRepository.findById(ID)).thenReturn(Optional.empty());

        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> workItemService.get(ID));

        assertEquals(format("work item not found: %d", ID), exception.getMessage());
    }

    @Test
    void noWorkItemInDb_patch_throwsResourceNotFoundException(){
        final PatchWorkItemModel patchWorkItemModel = new PatchWorkItemModel(ID, 2L, WorkItem.Status.IN_PROGRESS);
        when(workItemRepository.findById(ID)).thenReturn(Optional.empty());

        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> workItemService.patch(patchWorkItemModel));

        assertEquals(format("work item not found: %d", ID), exception.getMessage());
    }

    @Test
    void workItemInDb_patchWithOutOfDateVersion_throwsOptimisticLockingException(){
        final WorkItem mockWorkItem = Mockito.mock(WorkItem.class);
        Mockito.when(mockWorkItem.getVersion()).thenReturn(2L);
        final PatchWorkItemModel patchWorkItemModel = new PatchWorkItemModel(ID, 1L, WorkItem.Status.IN_PROGRESS);
        when(workItemRepository.findById(ID)).thenReturn(Optional.of(mockWorkItem));

        assertThrows(OptimisticLockingException.class, () -> workItemService.patch(patchWorkItemModel));
    }

}