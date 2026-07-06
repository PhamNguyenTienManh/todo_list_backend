package com.srt.todo_list.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.todo_list.dto.request.ChangeStatusWorkRequest;
import com.srt.todo_list.dto.request.CreateWorkRequest;
import com.srt.todo_list.dto.request.UpdateWorkRequest;
import com.srt.todo_list.dto.response.WorkResponse;
import com.srt.todo_list.enums.WorkStatus;
import com.srt.todo_list.service.WorkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WorkControllerTest {

    @MockitoBean
    private WorkService workService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateWorkRequest createWorkRequest;
    private UpdateWorkRequest updateWorkRequest;
    private ChangeStatusWorkRequest changeStatusWorkRequest;
    private WorkResponse workResponse;
    private WorkResponse doneWorkResponse;

    @BeforeEach
    void initData() {
        createWorkRequest = CreateWorkRequest.builder()
                .title("Learn Spring Boot")
                .description("Write controller tests")
                .build();
        updateWorkRequest = UpdateWorkRequest.builder()
                .title("Learn Spring Boot Advanced")
                .description("Update controller tests")
                .status(WorkStatus.IN_PROGRESS)
                .build();
        changeStatusWorkRequest = ChangeStatusWorkRequest.builder()
                .status(WorkStatus.DONE)
                .build();
        workResponse = WorkResponse.builder()
                .id("work-1")
                .title("Learn Spring Boot")
                .description("Write controller tests")
                .status(WorkStatus.TODO)
                .build();
        doneWorkResponse = WorkResponse.builder()
                .id("work-1")
                .title("Learn Spring Boot")
                .description("Write controller tests")
                .status(WorkStatus.DONE)
                .build();
    }

    @Test
    void create_success() throws Exception {
        when(workService.create(any(CreateWorkRequest.class))).thenReturn(workResponse);

        mockMvc.perform(post("/works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWorkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value("work-1"))
                .andExpect(jsonPath("$.result.title").value("Learn Spring Boot"))
                .andExpect(jsonPath("$.result.status").value("TODO"));

        verify(workService).create(any(CreateWorkRequest.class));
    }

    @Test
    void update_success() throws Exception {
        when(workService.update(eq("work-1"), any(UpdateWorkRequest.class))).thenReturn(workResponse);

        mockMvc.perform(put("/works/work-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateWorkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value("work-1"))
                .andExpect(jsonPath("$.result.status").value("TODO"));

        verify(workService).update(eq("work-1"), any(UpdateWorkRequest.class));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete("/works/work-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Delete work successfully"));

        verify(workService).delete("work-1");
    }

    @Test
    void changeStatus_success() throws Exception {
        when(workService.changeStatus(eq("work-1"), any(ChangeStatusWorkRequest.class)))
                .thenReturn(doneWorkResponse);

        mockMvc.perform(patch("/works/work-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeStatusWorkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value("work-1"))
                .andExpect(jsonPath("$.result.status").value("DONE"));

        verify(workService).changeStatus(eq("work-1"), any(ChangeStatusWorkRequest.class));
    }

    @Test
    void getWorks_success() throws Exception {
        Page<WorkResponse> page = new PageImpl<>(List.of(workResponse), PageRequest.of(0, 10), 1);
        when(workService.getWorksByUser("Spring", WorkStatus.TODO, 0, 10))
                .thenReturn(page);

        mockMvc.perform(get("/works")
                        .param("keyword", "Spring")
                        .param("status", "TODO")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.content[0].id").value("work-1"))
                .andExpect(jsonPath("$.result.content[0].status").value("TODO"))
                .andExpect(jsonPath("$.result.totalElements").value(1));

        verify(workService).getWorksByUser("Spring", WorkStatus.TODO, 0, 10);
    }

    @Test
    void changeStatus_whenStatusInvalid_thenReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/works/work-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"INVALID"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1008))
                .andExpect(jsonPath("$.message").value("Invalid work status"));
    }
}
