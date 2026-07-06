package com.srt.todo_list.mapper;

import com.srt.todo_list.dto.request.CreateWorkRequest;

import com.srt.todo_list.dto.request.UpdateWorkRequest;
import com.srt.todo_list.dto.response.WorkResponse;
import com.srt.todo_list.entity.Work;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface WorkMapper {
    Work toWork(CreateWorkRequest request);

    WorkResponse toResponse(Work work);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateWork(@MappingTarget Work work, UpdateWorkRequest request);
}
