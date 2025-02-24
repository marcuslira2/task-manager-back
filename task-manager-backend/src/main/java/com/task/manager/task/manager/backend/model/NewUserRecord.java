package com.task.manager.task.manager.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO used for creating a new User")
public record NewUserRecord(

        @Schema(description = "Name of the user", example = "Johnny")
        @NotBlank(message = "Name cannot be blank or null")
        String username,

        @Schema(description = "Email of the user", example = "example@example.com")
        @NotBlank(message = "Email cannot be blank or null")
        String email,

        @Schema(description = "Password of the user", example = "abacate")
        @NotBlank(message = "Password cannot be blank or null")
        String password
) {
}
