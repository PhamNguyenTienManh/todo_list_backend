package com.srt.todo_list.dto.response;

import com.srt.todo_list.enums.WorkStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkResponse {
    String id;
    String title;
    String description;
    WorkStatus status;
}
