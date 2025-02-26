package com.task.manager.task.manager.backend.repository;

import com.task.manager.task.manager.backend.model.StatusEnum;
import com.task.manager.task.manager.backend.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query(value = "Select * from task where title = :title and deleted = false", nativeQuery = true)
    Optional<Task> findByTitle(String title);

    @Query(value = "Select * from task where status = :status and user_id = :userId and deleted = false",nativeQuery = true)
    Page<Task> findByStatus(Long userId,StatusEnum status,Pageable pageable);

    @Query(value = "SELECT * FROM task WHERE user_id = :userId and deleted = false", nativeQuery = true)
    Page<Task> findByUserId(Long userId, Pageable pageable);
}
