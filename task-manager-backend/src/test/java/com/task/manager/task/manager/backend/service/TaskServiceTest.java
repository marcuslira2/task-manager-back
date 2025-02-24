package com.task.manager.task.manager.backend.service;

import com.task.manager.task.manager.backend.model.*;
import com.task.manager.task.manager.backend.repository.TaskRepository;
import com.task.manager.task.manager.backend.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private UserRepository userRepository; // Mock do UserRepository

    @Mock
    private TaskRepository taskRepository;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

    }

    @Test
    @DisplayName("Should create a task successfully")
    @Description("Ensures that a task is successfully created with valid inputs.")
    void shouldCreateTaskSuccessfully() {
        User user  = mockUser();
        Date deadLine = mockDeadLine();
        NewTaskRecord taskRecord = new NewTaskRecord("Título",
                "Descrição",
                StatusEnum.PENDING,
                deadLine,
                user.getId()
        );
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(100L);
            return task;
        });

        Task task = taskService.create(taskRecord);

        assertNotNull(task);
        assertEquals("Título", task.getTitle());
        assertEquals(StatusEnum.PENDING, task.getStatus());
        assertNotNull(task.getCreateDate());
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    @Description("Ensures that an exception is thrown when attempting to create a task with a null title.")
    void shouldThrowExceptionWhenTitleIsNull() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        User user  = mockUser();
        Date deadLine = mockDeadLine();

        NewTaskRecord taskRecord = new NewTaskRecord(
                null,
                "Valid description",
                StatusEnum.PENDING,
                deadLine,
                user.getId()
        );

        Set<ConstraintViolation<NewTaskRecord>> violations = validator.validate(taskRecord);

        assertFalse(violations.isEmpty());
        assertEquals("Title cannot be blank or null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should throw exception when description is null")
    @Description("Ensures that an exception is thrown when attempting to create a task with a null description.")
    void shouldThrowExceptionWhenDescriptionIsNull() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        User user  = mockUser();
        Date deadLine = mockDeadLine();

        NewTaskRecord taskRecord = new NewTaskRecord(
                "Valid description",
                null,
                StatusEnum.PENDING,
                deadLine,
                user.getId()
        );

        Set<ConstraintViolation<NewTaskRecord>> violations = validator.validate(taskRecord);

        assertFalse(violations.isEmpty());
        assertEquals("Description cannot be blank or null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should throw exception when status is null")
    @Description("Ensures that an exception is thrown when attempting to create a task with a null status.")
    void shouldThrowExceptionWhenStatusIsNull() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        User user  = mockUser();
        Date deadLine = mockDeadLine();

        NewTaskRecord taskRecord = new NewTaskRecord(
                "Valid description",
                "Valid description",
                null,
                deadLine,
                user.getId()
        );

        Set<ConstraintViolation<NewTaskRecord>> violations = validator.validate(taskRecord);

        assertFalse(violations.isEmpty());
        assertEquals("Status cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should throw exception when deadline is null")
    @Description("Ensures that an exception is thrown when attempting to create a task with a null deadline.")
    void shouldThrowExceptionWhenDeadLineIsNull() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        User user  = mockUser();

        NewTaskRecord taskRecord = new NewTaskRecord(
                "Valid description",
                "Valid description",
                StatusEnum.PENDING,
                null,
                user.getId()
        );

        Set<ConstraintViolation<NewTaskRecord>> violations = validator.validate(taskRecord);
        assertFalse(violations.isEmpty());
        assertEquals("Deadline cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should throw exception when assigned user is null")
    @Description("Ensures that an exception is thrown when attempting to create a task without an assigned user.")
    void shouldThrowExceptionWhenAssignedUserIsNull() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Date deadLine = mockDeadLine();

        NewTaskRecord taskRecord = new NewTaskRecord(
                "Valid description",
                "Valid description",
                StatusEnum.PENDING,
                deadLine,
                null
        );

        Set<ConstraintViolation<NewTaskRecord>> violations = validator.validate(taskRecord);

        assertFalse(violations.isEmpty());
        assertEquals("User cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should throw exception when task record is null")
    @Description("Ensures that an exception is thrown when a null task record is passed.")
    void shouldThrowExceptionWhenTaskRecordIsNull() {
        assertThrows(IllegalArgumentException.class, () -> taskService.create(null));
    }

    @Test
    @DisplayName("Should throw exception when deadline is in the past")
    @Description("Ensures that an exception is thrown when the task deadline is set to a past date.")
    void shouldThrowExceptionWhenDeadLineIsInThePast() {
        User user  = mockUser();
        Date pastDate = new Date(System.currentTimeMillis() - 86400000);
        NewTaskRecord taskRecord = new NewTaskRecord(
                "Title",
                "Descrição",
                StatusEnum.PENDING,
                pastDate,
                user.getId()
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> taskService.create(taskRecord));
    }


    @Test
    @DisplayName("Should list tasks by assigned user")
    @Description("Verifies that tasks are listed correctly when filtered by assigned user ID.")
    void shouldListTasksByAssignTo() {
        Task task = mockTask();

        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(taskRepository.findByAssignedTo(1L, pageable)).thenReturn(taskPage);

        Page<Task> result = taskService.listTasksByAssignTo(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taskRepository, times(1)).findByAssignedTo(1L, pageable);
    }

    @Test
    @DisplayName("Should find task by ID when task exists")
    @Description("Ensures that a task is found successfully when a valid ID is provided.")
    void shouldFindTaskById_WhenTaskExists() {
        Task task = mockTask();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.findTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when task not found")
    @Description("Ensures that an exception is thrown when trying to find a task that does not exist.")
    void shouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> taskService.findTaskById(99L));
        verify(taskRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should list tasks by status")
    @Description("Verifies that tasks are listed correctly when filtered by status.")
    void shouldListTasksByStatus() {
        Task task = mockTask();
        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(taskRepository.listByStatus(StatusEnum.PENDING, 1L, pageable)).thenReturn(taskPage);

        Page<Task> result = taskService.listTasksByStatus(1L, StatusEnum.PENDING, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taskRepository, times(1)).listByStatus(StatusEnum.PENDING, 1L, pageable);
    }

    @Test
    @DisplayName("Should update task successfully")
    @Description("Ensures that an existing task is updated correctly with valid data.")
    void shouldUpdateTaskSuccessfully() {
        User user  = mockUser();
        Date deadLine = mockDeadLine();
        Task task = mockTask();

        NewTaskRecord updatedTaskRecord = new NewTaskRecord(
                "Updated Task",
                "Updated Task",
                StatusEnum.COMPLETED,
                deadLine,
                user.getId()
        );

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.updateTask(1L, updatedTaskRecord);

        assertEquals("Updated Task", task.getTitle());
        assertEquals(StatusEnum.COMPLETED, task.getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Should throw exception when task title already exists")
    @Description("Ensures that an exception is thrown when trying to update a task with an already existing title.")
    void shouldThrowException_WhenTaskTitleExists() {
        User user = mockUser();
        Date deadLine = mockDeadLine();
        Task existingTask = mockTaskValidate();

        Task duplicateTask = new Task();
        duplicateTask.setTitle(existingTask.getTitle());

        NewTaskRecord updatedTaskRecord = new NewTaskRecord(
                existingTask.getTitle(),
                "Updated Task",
                StatusEnum.COMPLETED,
                deadLine,
                user.getId()
        );

        when(taskRepository.findById(2L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.findByTitle(existingTask.getTitle())).thenReturn(Optional.of(duplicateTask));

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(2L, updatedTaskRecord));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should delete task successfully")
    @Description("Ensures that an existing task is deleted successfully.")
    void shouldDeleteTaskSuccessfully() {
        Task task = mockTask();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting a non-existent task")
    @Description("Ensures that an exception is thrown when attempting to delete a task that does not exist.")
    void shouldThrowException_WhenDeletingNonExistentTask() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> taskService.deleteTask(99L));
        verify(taskRepository, times(1)).findById(99L);
    }

    protected User mockUser(){
        User user = new User();

        user.setId(1L);
        user.setUsername("teste1");
        user.setEmail("teste@hotmail.com");
        user.setPassword("1245667");

        return user;
    }

    protected Date mockDeadLine(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 2);

       return calendar.getTime();
    }

    protected Task mockTask(){
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus(StatusEnum.PENDING);
        task.setAssignedTo(mockUser());
        task.setDeadLine(mockDeadLine());
        task.setDescription("Mock");
        task.setCreateDate(new Date());

        return task;
    }

    protected Task mockTaskValidate(){
        Task task = new Task();
        task.setId(2L);
        task.setTitle("Updated Task");
        task.setStatus(StatusEnum.PENDING);
        task.setAssignedTo(mockUser());
        task.setDeadLine(mockDeadLine());
        task.setDescription("Mock");
        task.setCreateDate(new Date());

        return task;
    }

}