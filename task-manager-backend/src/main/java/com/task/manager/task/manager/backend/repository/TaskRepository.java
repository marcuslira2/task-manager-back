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

    Page<Task> findByAssignedTo(Long assignTo, Pageable pageable);

    @Query(value = "Select * from task where title = :title", nativeQuery = true)
    Optional<Task> findByTitle(String title);

    @Query(value = "Select * from task where status = :status and assignedTo = :assignedTo",nativeQuery = true)
    Page<Task> listByStatus(StatusEnum status,Long assignedTo,Pageable pageable);
}
