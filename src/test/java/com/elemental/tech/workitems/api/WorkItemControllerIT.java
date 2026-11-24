package com.elemental.tech.workitems.api;

// TODO: 2. Left imports as a hint.
import com.elemental.tech.App;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.persistence.WorkItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkItemControllerIT {

    private static final String WORK_ITEMS_PATH = "/api/work-items";
    private static final String WORK_ITEMS_BY_ID_PATH = WORK_ITEMS_PATH + "{work_item_id}";

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
            // TODO: 1. Please identify an appropriate testing strat
            final MvcResult result = createWorkItem(STANDARD_WORK_ITEM_REQUEST);
            final String workItemResponseJson = result.getResponse().getContentAsString();
            final WorkItemResponse workItemResponse = objectMapper.readValue(workItemResponseJson, WorkItemResponse.class);


            // TODO: 3. enhance and add further tests
        }
    }

    @Nested
    class GetWorkItemsEndpointTest{

        @Test
        void reqShouldReturnOK() throws Exception {
            mockMvc.perform(get(WORK_ITEMS_PATH))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetWorkItemByIdEndpointTest{

        @Test
        void createWorkItem_getByIdEndpoint_okResponse_bodyValidated(){

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
                .andExpect(status().isCreated())
                .andReturn();
    }
}
