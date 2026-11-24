package com.elemental.tech.workitems.api;

import com.elemental.tech.App;
import com.elemental.tech.workitems.api.dto.PatchWorkItemRequest;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.domain.WorkItem;
import com.elemental.tech.workitems.persistence.WorkItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.Objects;

@AutoConfigureMockMvc
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkItemControllerIT {

    private static final String WORK_ITEMS_PATH = "/api/work-items";
    private static final String WORK_ITEMS_BY_ID_PATH = WORK_ITEMS_PATH + "/{work_item_id}";

    private static final String TITLE = "Hello";
    private static final String DESCRIPTION = "World";
    private static final long VERSION = 0;
    private static final CreateWorkItemRequest.Priority PRIORITY = CreateWorkItemRequest.Priority.MEDIUM;
    private static final CreateWorkItemRequest STANDARD_WORK_ITEM_REQUEST = new CreateWorkItemRequest(TITLE, DESCRIPTION, PRIORITY);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WorkItemRepository workItemRepository;

    @AfterEach
    void tearDown() {
        workItemRepository.deleteAll();
    }

    @Nested
    class NewWorkItemEndpointTest{

        @Test
        void createNewWorkItem() throws Exception {
            final MvcResult result = createWorkItem(STANDARD_WORK_ITEM_REQUEST, status().isCreated());
            final String workItemResponseJson = result.getResponse().getContentAsString();
            final WorkItemResponse workItemResponse = objectMapper.readValue(workItemResponseJson, WorkItemResponse.class);

            assertThat(workItemResponse)
                    .has(new Condition<>(res -> res.id() > 0, "Id has been generated"))
                    .has(new Condition<>(res -> Objects.nonNull(res.createdAt()), "Created at has been generated"))
                    .extracting(
                            WorkItemResponse::title,
                            WorkItemResponse::description,
                            WorkItemResponse::priority,
                            WorkItemResponse::status)
                    .containsExactly(
                            TITLE,
                            DESCRIPTION,
                            PRIORITY.name(),
                            WorkItem.Status.OPEN.name()
                    );
        }

        @NullAndEmptySource
        @ParameterizedTest
        void emptyTitle_callCreateNewWorkItemEndpoint_400ResponseWithDetail(final String title) throws Exception {
            final CreateWorkItemRequest emptyTitleRequest = new CreateWorkItemRequest(title, DESCRIPTION, PRIORITY);

            final MvcResult result = createWorkItem(emptyTitleRequest, status().isBadRequest());
            final String problemDetailJson = result.getResponse().getContentAsString();
            final ProblemDetail problemDetail = objectMapper.readValue(problemDetailJson, ProblemDetail.class);

            assertThat(problemDetail)
                    .extracting(
                            ProblemDetail::getTitle,
                            ProblemDetail::getStatus,
                            ProblemDetail::getDetail)
                    .containsExactly(
                            "Validation Failed",
                            400,
                            "Title must be provided");
        }

        @Test
        void titleSizeOver120_callCreateNewWorkItemEndpoint_400Response() throws Exception {
            final String title = RandomStringUtils.secure().nextAlphabetic(121);
            final CreateWorkItemRequest emptyTitleRequest = new CreateWorkItemRequest(title, DESCRIPTION, PRIORITY);

            createWorkItem(emptyTitleRequest, status().isBadRequest());
        }

        @Test
        void descriptionSizeOver2000_callCreateNewWorkItemEndpoint_400Response() throws Exception {
            final String description = RandomStringUtils.secure().nextAlphabetic(2001);
            final CreateWorkItemRequest emptyTitleRequest = new CreateWorkItemRequest(TITLE, description, PRIORITY);

            createWorkItem(emptyTitleRequest, status().isBadRequest());
        }

        @Test
        void priorityNull_callCreateNewWorkItemEndpoint_400Response() throws Exception {
            final CreateWorkItemRequest emptyTitleRequest = new CreateWorkItemRequest(TITLE, DESCRIPTION, null);

            createWorkItem(emptyTitleRequest, status().isBadRequest());
        }
    }

    @Nested
    class GetWorkItemsEndpointTest{

        @Test
        void reqShouldReturnOK() throws Exception {
            createWorkItem(STANDARD_WORK_ITEM_REQUEST, status().isCreated());

            final MvcResult result = mockMvc.perform(get(WORK_ITEMS_PATH))
                    .andExpect(status().isOk())
                    .andReturn();
            final String workItemsResponseJson = result.getResponse().getContentAsString();
            final List<WorkItemResponse> workItemsResponse = objectMapper.readValue(workItemsResponseJson, new TypeReference<>() {});

            assertEquals(1, workItemsResponse.size());
        }

        @Test
        void createWorkItem_callGetWorkItemsEndpoint_workItemReturned() throws Exception {
            mockMvc.perform(get(WORK_ITEMS_PATH))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetWorkItemByIdEndpointTest{

        @Test
        void createWorkItem_getByIdEndpoint_okResponse_bodyValidated() throws Exception {
            final long id = createValidWorkItemReturnId();

            final MvcResult result = mockMvc.perform(get(WORK_ITEMS_BY_ID_PATH, id))
                    .andExpect(status().isOk())
                    .andReturn();
            final String workItemResponseJson = result.getResponse().getContentAsString();
            final WorkItemResponse workItemResponse = objectMapper.readValue(workItemResponseJson, WorkItemResponse.class);

            assertThat(workItemResponse)
                    .has(new Condition<>(res -> res.id() > 0, "Id has been generated"))
                    .has(new Condition<>(res -> Objects.nonNull(res.createdAt()), "Created at has been generated"))
                    .extracting(
                            WorkItemResponse::title,
                            WorkItemResponse::description,
                            WorkItemResponse::priority,
                            WorkItemResponse::status)
                    .containsExactly(
                            TITLE,
                            DESCRIPTION,
                            PRIORITY.name(),
                            WorkItem.Status.OPEN.name()
                    );
        }
    }

    @Nested
    class PatchWorkItemByIdEndpointTest{

        @Test
        void createWorkItem_validVersionStatus_patchEndpoint_okResponse_bodyValidated() throws Exception {
            final PatchWorkItemRequest validVersionRequest = new PatchWorkItemRequest(VERSION, WorkItem.Status.IN_PROGRESS);
            final long id = createValidWorkItemReturnId();

            final MvcResult result = mockMvc.perform(patch(WORK_ITEMS_BY_ID_PATH, id)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validVersionRequest)))
                    .andExpect(status().isOk())
                    .andReturn();
            final String workItemResponseJson = result.getResponse().getContentAsString();
            final WorkItemResponse workItemResponse = objectMapper.readValue(workItemResponseJson, WorkItemResponse.class);

            assertThat(workItemResponse)
                    .has(new Condition<>(res -> res.id() > 0, "Id has been generated"))
                    .has(new Condition<>(res -> Objects.nonNull(res.createdAt()), "Created at has been generated"))
                    .extracting(
                            WorkItemResponse::title,
                            WorkItemResponse::description,
                            WorkItemResponse::priority,
                            WorkItemResponse::status)
                    .containsExactly(
                            TITLE,
                            DESCRIPTION,
                            PRIORITY.name(),
                            WorkItem.Status.IN_PROGRESS.name()
                    );
        }

        @Test
        void nullVersion_callPatchWorkItemEndpoint_400ResponseWithDetail() throws Exception {
            final PatchWorkItemRequest emptyVersionRequest = new PatchWorkItemRequest(null, WorkItem.Status.IN_PROGRESS);

            final MvcResult result = mockMvc.perform(patch(WORK_ITEMS_BY_ID_PATH, 1L)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyVersionRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            final String problemDetailJson = result.getResponse().getContentAsString();
            final ProblemDetail problemDetail = objectMapper.readValue(problemDetailJson, ProblemDetail.class);

            assertEquals(400, result.getResponse().getStatus());
            assertThat(problemDetail)
                    .extracting(
                            ProblemDetail::getTitle,
                            ProblemDetail::getStatus,
                            ProblemDetail::getDetail)
                    .containsExactly(
                            "Validation Failed",
                            400,
                            "Version must be provided");
        }

        @Test
        void nullStatus_callPatchWorkItemEndpoint_400Response() throws Exception {
            final PatchWorkItemRequest emptyVersionRequest = new PatchWorkItemRequest(VERSION, null);

            mockMvc.perform(patch(WORK_ITEMS_BY_ID_PATH, 1L)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyVersionRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createWorkItem_doublePatchCallWithoutAmendingVersion_conflictResponseOnSecondCall() throws Exception {
            final PatchWorkItemRequest validVersionRequest = new PatchWorkItemRequest(VERSION, WorkItem.Status.IN_PROGRESS);
            final long id = createValidWorkItemReturnId();

            mockMvc.perform(patch(WORK_ITEMS_BY_ID_PATH, id)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validVersionRequest)))
                    .andExpect(status().isOk());

            mockMvc.perform(patch(WORK_ITEMS_BY_ID_PATH, id)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validVersionRequest)))
                    .andExpect(status().isConflict());
        }
    }

    private MvcResult createWorkItem(final CreateWorkItemRequest createWorkItemRequest, ResultMatcher responseStatusMatcher) throws Exception {
        return mockMvc.perform(post(WORK_ITEMS_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWorkItemRequest)))
                .andExpect(responseStatusMatcher)
                .andReturn();
    }

    private long createValidWorkItemReturnId() throws Exception {
        final MvcResult createWorkItemResult = createWorkItem(STANDARD_WORK_ITEM_REQUEST, status().isCreated());
        final String createWorkItemJson = createWorkItemResult.getResponse().getContentAsString();
        final WorkItemResponse createWorkItemResponse = objectMapper.readValue(createWorkItemJson, WorkItemResponse.class);
        return createWorkItemResponse.id();
    }
}
