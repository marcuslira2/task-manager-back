package com.task.manager.task.manager.backend.service;

import com.task.manager.task.manager.backend.model.NewTaskRecord;
import com.task.manager.task.manager.backend.model.StatusEnum;
import com.task.manager.task.manager.backend.model.Task;
import com.task.manager.task.manager.backend.model.User;
import com.task.manager.task.manager.backend.repository.TaskRepository;
import com.task.manager.task.manager.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private static final String TASK_NOT_FOUND = "Task not found";

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Page<Task> findAll(Pageable pageable){
        return taskRepository.findAll(pageable);
    }

    public Task findTaskById (Long taskId){
        return taskRepository.findById(taskId).orElseThrow(()-> new NoSuchElementException(TASK_NOT_FOUND));
    }

    public Page<Task> listTasksByStatus(StatusEnum statusEnum, Pageable pageable){
        return taskRepository.listByStatus(statusEnum,pageable);
    }

    public Task create (@Valid NewTaskRecord taskRecord){
        if (taskRecord == null){
            throw new IllegalArgumentException("TaskObject cannot be null");
        }

        User user = userRepository.findById(taskRecord.assignedTo()).orElseThrow(()-> new NoSuchElementException("User not found"));

        Task task = new Task();
        task.setTitle(validateTitle(taskRecord.title()));
        task.setDeadLine(validateDeadLine(taskRecord.deadLine()));
        task.setDescription(taskRecord.description());
        task.setCreateDate(new Date());
        task.setTitle(taskRecord.title());
        task.setStatus(taskRecord.status());
        task.setAssignedTo(user);

        return taskRepository.save(task);
    }

    public void updateTask (Long id, NewTaskRecord taskRecord){
        validateTitle(taskRecord.title());
        Task task = findTaskById(id);

        task.setTitle(taskRecord.title());
        task.setDeadLine(validateDeadLine(taskRecord.deadLine()));
        task.setDescription(taskRecord.description());
        task.setTitle(taskRecord.title());
        task.setStatus(taskRecord.status());

        taskRepository.save(task);
    }

    public void deleteTask (Long id){
        Task task = findTaskById(id);
        taskRepository.deleteById(task.getId());
    }

    public String validateTitle(String title){
        if (title.isEmpty()){
            throw new IllegalArgumentException("Task title already exists");
        }
        taskRepository.findByTitle(title)
                .ifPresent(task -> { throw new IllegalArgumentException("Task title already exists"); });

        return title;
    }

    public Date validateDeadLine(Date date){
        if (date.before(new Date())) {
            throw new IllegalArgumentException("Deadline date cannot be before creation date.");
        }
        return date;
    }
}
