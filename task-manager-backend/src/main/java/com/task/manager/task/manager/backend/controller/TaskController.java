package com.task.manager.task.manager.backend.controller;

import com.task.manager.task.manager.backend.model.NewTaskRecord;
import com.task.manager.task.manager.backend.model.StatusEnum;
import com.task.manager.task.manager.backend.model.Task;
import com.task.manager.task.manager.backend.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task Management", description = "Endpoints for managing tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @GetMapping
    @Operation(
            summary = "Retrieve tasks assigned to a user",
            description = "Returns a paginated list of tasks assigned to the given user.",
            parameters = {
                    @Parameter(name = "pageable", hidden = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
            }
    )
    public ResponseEntity<Page<Task>> retrieveTasks(@PageableDefault(size = 10) Pageable pageable){
        try {

            Page<Task> tasks = taskService.findAll(pageable);

            return ResponseEntity.ok(tasks);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Find a task by ID",
            description = "Retrieves a specific task based on its ID.",
            parameters = @Parameter(name = "id", description = "Task ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public ResponseEntity<Task> findTask (@PathVariable Long id){
        return ResponseEntity.ok().body(taskService.findTaskById(id));
    }

    @GetMapping("/tasks/filter")
    @Operation(
            summary = "Find tasks by status",
            description = "Retrieves a paginated list of tasks filtered by status and user ID.",
            parameters = {
                    @Parameter(name = "status", description = "Status of the task", required = true),
                    @Parameter(name = "pageable", hidden = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered tasks retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
            }
    )
    public ResponseEntity<Page<Task>> findTaskByStatus (@RequestParam StatusEnum status,@PageableDefault(size = 10) Pageable pageable ){
        try {
            return ResponseEntity.ok().body(taskService.listTasksByStatus(status, pageable));
        }catch (Exception e){
           throw new RuntimeException(e.getMessage());
        }

    }

    @PostMapping
    @Operation(
            summary = "Create a new task",
            description = "Creates a new task and returns a confirmation message.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Task data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NewTaskRecord.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Title already exists")
            }
    )
    public ResponseEntity<String> createTask (@RequestBody NewTaskRecord taskRecord){
        try{
            Task task = taskService.create(taskRecord);

            return ResponseEntity.status(HttpStatus.CREATED).body("Task " + task.getTitle() + " created.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing task",
            description = "Updates an existing task based on its ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated task data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NewTaskRecord.class))
            ),
            parameters = @Parameter(name = "id", description = "Task ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Task updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public ResponseEntity<String> updateTask (@PathVariable Long id, @RequestBody NewTaskRecord taskRecord){
        try {
            taskService.updateTask(id,taskRecord);

            return ResponseEntity.accepted().body("Task updated.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a task",
            description = "Deletes a task based on its ID.",
            parameters = @Parameter(name = "id", description = "Task ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Task deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid task ID"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public ResponseEntity<String> deleteTask (@PathVariable Long id){
        try{
            taskService.deleteTask(id);
            return ResponseEntity.accepted().body("Task deleted.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }

}
