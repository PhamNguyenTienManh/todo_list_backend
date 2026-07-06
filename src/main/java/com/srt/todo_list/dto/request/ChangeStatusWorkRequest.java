package com.srt.todo_list.dto.request;

import com.srt.todo_list.enums.WorkStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeStatusWorkRequest {
    WorkStatus status;
}
