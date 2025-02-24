package com.task.manager.task.manager.backend.model;

import jakarta.validation.constraints.NotBlank;

public record LoginRecord(
        @NotBlank(message = "Name cannot be blank or null")String username,
        @NotBlank(message = "Password cannot be blank or null")String password
) {
}
