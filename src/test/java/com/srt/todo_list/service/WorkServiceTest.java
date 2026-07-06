package com.srt.todo_list.service;

import com.srt.todo_list.dto.request.ChangeStatusWorkRequest;
import com.srt.todo_list.dto.request.CreateWorkRequest;
import com.srt.todo_list.dto.request.UpdateWorkRequest;
import com.srt.todo_list.dto.response.WorkResponse;
import com.srt.todo_list.entity.User;
import com.srt.todo_list.entity.Work;
import com.srt.todo_list.enums.WorkStatus;
import com.srt.todo_list.exception.AppException;
import com.srt.todo_list.exception.ErrorCode;
import com.srt.todo_list.mapper.WorkMapper;
import com.srt.todo_list.repository.WorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkServiceTest {

    @Mock
    private WorkRepository workRepository;

    @Mock
    private WorkMapper workMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private WorkService workService;

    private User user;
    private Work work;
    private Work savedWork;
    private CreateWorkRequest createWorkRequest;
    private UpdateWorkRequest updateWorkRequest;
    private ChangeStatusWorkRequest changeStatusWorkRequest;
    private WorkResponse expectedResponse;

    @BeforeEach
    void initData() {
        user = User.builder()
                .id("00f0f5d2")
                .username("john")
                .fullName("John Doe")
                .build();
        work = Work.builder()
                .id("work-1")
                .title("Learn Spring Boot")
                .description("Write unit tests")
                .status(WorkStatus.TODO)
                .build();
        savedWork = Work.builder()
                .id("work-1")
                .title("Learn Spring Boot")
                .description("Write unit tests")
                .status(WorkStatus.TODO)
                .user(user)
                .build();
        createWorkRequest = CreateWorkRequest.builder()
                .title("Learn Spring Boot")
                .description("Write unit tests")
                .build();
        updateWorkRequest = UpdateWorkRequest.builder()
                .title("Learn Spring Boot Advanced")
                .description("Update unit tests")
                .status(WorkStatus.IN_PROGRESS)
                .build();
        changeStatusWorkRequest = ChangeStatusWorkRequest.builder()
                .status(WorkStatus.DONE)
                .build();
        expectedResponse = WorkResponse.builder()
                .id("work-1")
                .title("Learn Spring Boot")
                .description("Write unit tests")
                .status(WorkStatus.TODO)
                .build();
    }

    @Test
    void create_success() {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(workMapper.toWork(createWorkRequest)).thenReturn(work);
        when(workRepository.save(work)).thenReturn(savedWork);
        when(workMapper.toResponse(savedWork)).thenReturn(expectedResponse);

        WorkResponse actualResponse = workService.create(createWorkRequest);

        assertSame(expectedResponse, actualResponse);
        assertSame(user, work.getUser());
        assertEquals(WorkStatus.TODO, work.getStatus());
        verify(workRepository).save(work);
    }

    @Test
    void update_success() {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(workRepository.findByIdAndUser("work-1", user)).thenReturn(Optional.of(work));
        when(workRepository.save(work)).thenReturn(savedWork);
        when(workMapper.toResponse(savedWork)).thenReturn(expectedResponse);

        WorkResponse actualResponse = workService.update("work-1", updateWorkRequest);

        assertSame(expectedResponse, actualResponse);
        verify(workMapper).updateWork(work, updateWorkRequest);
        verify(workRepository).save(work);
    }

    @Test
    void update_whenWorkNotFound_thenThrowWorkNotFound() {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(workRepository.findByIdAndUser("work-1", user)).thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> workService.update("work-1", updateWorkRequest)
        );

        assertEquals(ErrorCode.WORK_NOT_FOUND, exception.getErrorCode());
        verify(workMapper, never()).updateWork(any(), any());
        verify(workRepository, never()).save(any());
    }

    @Test
    void delete_success() {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(workRepository.findByIdAndUser("work-1", user)).thenReturn(Optional.of(work));

        workService.delete("work-1");

        verify(workRepository).delete(work);
    }

    @Test
    void delete_whenWorkNotFound_thenThrowWorkNotFound() {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(workRepository.findByIdAndUser("work-1", user)).thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> workService.delete("work-1")
        );

        assertEquals(ErrorCode.WORK_NOT_FOUND, exception.getErrorCode());
        verify(workRepository, never()).delete(any(Work.class));
    }

    @Test
    void changeStatus_success() {
        WorkResponse doneResponse = WorkResponse.builder()
                .id("work-1")
                .title("Learn Spring Boot")
                .description("Write unit tests")
                .status(WorkStatus.DONE)
                .build();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(workRepository.findByIdAndUser("work-1", user)).thenReturn(Optional.of(work));
        when(workRepository.save(work)).thenReturn(work);
        when(workMapper.toResponse(work)).thenReturn(doneResponse);

        WorkResponse actualResponse = workService.changeStatus("work-1", changeStatusWorkRequest);

        assertSame(doneResponse, actualResponse);
        assertEquals(WorkStatus.DONE, work.getStatus());
        verify(workRepository).save(work);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getWorksByUser_success() {
        Page<Work> workPage = new PageImpl<>(List.of(savedWork));
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(workRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(workPage);
        when(workMapper.toResponse(savedWork)).thenReturn(expectedResponse);

        Page<WorkResponse> responsePage = workService.getWorksByUser(
                "Spring",
                WorkStatus.TODO,
                0,
                10
        );

        assertEquals(1, responsePage.getTotalElements());
        assertSame(expectedResponse, responsePage.getContent().getFirst());
    }
}
