package com.srt.todo_list.specification;

import com.srt.todo_list.entity.User;
import com.srt.todo_list.entity.Work;
import com.srt.todo_list.enums.WorkStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class WorkSpecification {

    public static Specification<Work> filter(User user, String keyword, WorkStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Chỉ lấy work của user hiện tại
            predicates.add(cb.equal(root.get("user"), user));
            // Search title
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("title")),
                                "%" + keyword.toLowerCase() + "%"
                        )
                );
            }
            // Filter status
            if (status != null) {
                predicates.add(
                        cb.equal(root.get("status"), status)
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
