package com.srt.todo_list.controller;

import com.srt.todo_list.dto.request.ChangeStatusWorkRequest;
import com.srt.todo_list.dto.request.CreateWorkRequest;
import com.srt.todo_list.dto.request.UpdateWorkRequest;
import com.srt.todo_list.dto.response.APIResponse;
import com.srt.todo_list.dto.response.WorkResponse;
import com.srt.todo_list.enums.WorkStatus;
import com.srt.todo_list.service.WorkService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/works")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkController {
    WorkService workService;

    @PostMapping
    APIResponse<WorkResponse> create (@RequestBody CreateWorkRequest request) {
        return APIResponse.<WorkResponse>builder()
                .result(workService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    APIResponse<WorkResponse> update(@PathVariable String id, @RequestBody UpdateWorkRequest request) {
        return APIResponse.<WorkResponse>builder()
                .result(workService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    APIResponse<Void> delete(@PathVariable String id) {
        workService.delete(id);
        return APIResponse.<Void>builder()
                .message("Delete work successfully")
                .build();
    }

    @PatchMapping("/{id}/status")
    APIResponse<WorkResponse> changeStatus(@PathVariable String id,
                                           @RequestBody ChangeStatusWorkRequest request) {
        return APIResponse.<WorkResponse>builder()
                .result(workService.changeStatus(id, request))
                .build();
    }

    @GetMapping
    APIResponse<Page<WorkResponse>> getWorks(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) WorkStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return APIResponse.<Page<WorkResponse>>builder()
                .result(workService.getWorksByUser(
                        keyword,
                        status,
                        page,
                        size))
                .build();
    }
}
