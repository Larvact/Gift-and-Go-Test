package com.elemental.tech.workitems.api;

// TODO: 2. Left imports as a hint.
import com.elemental.tech.App;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.domain.WorkItem;
import com.elemental.tech.workitems.persistence.WorkItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;

@AutoConfigureMockMvc
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkItemControllerIT {

    private static final String WORK_ITEMS_PATH = "/api/work-items";
    private static final String WORK_ITEMS_BY_ID_PATH = WORK_ITEMS_PATH + "/{work_item_id}";

    private static final String TITLE = "Hello";
    private static final String DESCRIPTION = "World";
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
            final MvcResult result = createValidWorkItem(STANDARD_WORK_ITEM_REQUEST);
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

            final MvcResult result = createWorkItem(emptyTitleRequest);

            assertEquals(400, result.getResponse().getStatus());
        }

        @Test
        void titleSizeOver120_callCreateNewWorkItemEndpoint_400ResponseWithDetail() throws Exception {
            final String title = RandomStringUtils.secure().nextAlphabetic(121);
            final CreateWorkItemRequest emptyTitleRequest = new CreateWorkItemRequest(title, DESCRIPTION, PRIORITY);

            final MvcResult result = createWorkItem(emptyTitleRequest);

            assertEquals(400, result.getResponse().getStatus());
        }

        @Test
        void descriptionSizeOver2000_callCreateNewWorkItemEndpoint_400ResponseWithDetail() throws Exception {
            final String description = RandomStringUtils.secure().nextAlphabetic(2001);
            final CreateWorkItemRequest emptyTitleRequest = new CreateWorkItemRequest(TITLE, description, PRIORITY);

            final MvcResult result = createWorkItem(emptyTitleRequest);

            assertEquals(400, result.getResponse().getStatus());
        }

        @Test
        void priorityNull_callCreateNewWorkItemEndpoint_400ResponseWithDetail() throws Exception {
            final CreateWorkItemRequest emptyTitleRequest = new CreateWorkItemRequest(TITLE, DESCRIPTION, null);

            final MvcResult result = createWorkItem(emptyTitleRequest);

            assertEquals(400, result.getResponse().getStatus());
        }
    }

    @Nested
    class GetWorkItemsEndpointTest{

        @Test
        void reqShouldReturnOK() throws Exception {
            createValidWorkItem(STANDARD_WORK_ITEM_REQUEST);

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
            final MvcResult createWorkItemResult = createValidWorkItem(STANDARD_WORK_ITEM_REQUEST);
            final String createWorkItemJson = createWorkItemResult.getResponse().getContentAsString();
            final WorkItemResponse createWorkItemResponse = objectMapper.readValue(createWorkItemJson, WorkItemResponse.class);
            final long id = createWorkItemResponse.id();

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
        void createWorkItem_getByIdEndpoint_okResponse_bodyValidated(){

        }
    }

    private MvcResult createWorkItem(final CreateWorkItemRequest createWorkItemRequest) throws Exception {
        return mockMvc.perform(post(WORK_ITEMS_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWorkItemRequest)))
                .andReturn();
    }

    private MvcResult createValidWorkItem(final CreateWorkItemRequest createWorkItemRequest) throws Exception {
        return mockMvc.perform(post(WORK_ITEMS_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWorkItemRequest)))
                .andExpect(status().isCreated())
                .andReturn();
    }
}
