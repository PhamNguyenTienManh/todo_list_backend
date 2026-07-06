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
import com.srt.todo_list.specification.WorkSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkService {
    WorkRepository workRepository;
    WorkMapper workMapper;
    AuthenticationService authenticationService;

    public WorkResponse create(CreateWorkRequest request) {
        User user = authenticationService.getCurrentUser();
        Work work = workMapper.toWork(request);
        work.setUser(user);
        work.setStatus(WorkStatus.TODO);
        return workMapper.toResponse(workRepository.save(work));
    }

    public WorkResponse update(String id, UpdateWorkRequest request) {
        User user = authenticationService.getCurrentUser();
        Work work = workRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.WORK_NOT_FOUND));
        workMapper.updateWork(work, request);
        return workMapper.toResponse(workRepository.save(work));
    }

    public void delete(String id) {
        User user = authenticationService.getCurrentUser();
        Work work = workRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.WORK_NOT_FOUND));
        workRepository.delete(work);
    }

    public WorkResponse changeStatus(String id, ChangeStatusWorkRequest request) {
        User user = authenticationService.getCurrentUser();
        Work work = workRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.WORK_NOT_FOUND));
        work.setStatus(request.getStatus());
        return workMapper.toResponse(workRepository.save(work));
    }

    public Page<WorkResponse> getWorksByUser(
            String keyword,
            WorkStatus status,
            int page,
            int size) {
        User user = authenticationService.getCurrentUser();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return workRepository
                .findAll(
                        WorkSpecification.filter(user, keyword, status),
                        pageable)
                .map(workMapper::toResponse);
    }
}
