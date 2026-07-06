package com.srt.todo_list.repository;

import com.srt.todo_list.entity.User;
import com.srt.todo_list.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, String>, JpaSpecificationExecutor<Work> {
    Optional<Work> findByIdAndUser(String id, User user);
}
