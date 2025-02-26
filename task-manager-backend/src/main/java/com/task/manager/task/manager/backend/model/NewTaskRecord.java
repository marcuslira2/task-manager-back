package com.task.manager.task.manager.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Schema(description = "DTO used for creating a new task")
public record NewTaskRecord(

        @Schema(description = "Title of the task", example = "Implement API")
        @NotBlank(message = "Title cannot be blank or null")
        String title,

        @Schema(description = "Detailed description of the task", example = "Implement API for user management")
        @NotBlank(message = "Description cannot be blank or null")
        String description,

        @Schema(description = "Task status", example = "PENDING")
        @NotNull(message = "Status cannot be null")
        StatusEnum status,

        @Schema(description = "The deadline for the task. Must be a future date, later than the current date and time.",
                example = "2025-03-01T10:00:00Z")
        @NotNull(message = "Deadline cannot be null")
        Date deadLine,

        @Schema(description = "User assigned to the task", example = "1")
        @NotNull(message = "User cannot be null")
        Long assignedTo
) {
}
