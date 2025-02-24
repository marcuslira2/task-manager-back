package com.task.manager.task.manager.backend.controller;

import com.task.manager.task.manager.backend.model.NewUserRecord;
import com.task.manager.task.manager.backend.model.User;
import com.task.manager.task.manager.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "Endpoint for user registration")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User data required for registration",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = NewUserRecord.class)
                    )
            ),
            description = "Creates a new user in the system. The username and email must be unique.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
            }
    )
    public ResponseEntity<String> create(@RequestBody @Valid NewUserRecord newUser) {
        try{
            User user = userService.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User: " + user.getUsername() + " created.");

        }catch (Exception e){
          return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
