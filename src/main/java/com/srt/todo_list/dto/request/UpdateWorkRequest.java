package com.srt.todo_list.dto.request;

import com.srt.todo_list.enums.WorkStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateWorkRequest {
    String title;
    String description;
    WorkStatus status;
}
