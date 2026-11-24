package com.elemental.tech.workitems.service;

import com.elemental.tech.workitems.api.dto.PatchWorkItemModel;
import com.elemental.tech.workitems.api.dto.PatchWorkItemRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkitemPatchMapper {

    public static PatchWorkItemModel toModel(final long id, final PatchWorkItemRequest patchWorkItemRequest){
        return new PatchWorkItemModel(id, patchWorkItemRequest.version(), patchWorkItemRequest.status());
    }
}
