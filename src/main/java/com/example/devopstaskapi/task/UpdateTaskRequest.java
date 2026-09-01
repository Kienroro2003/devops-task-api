package com.example.devopstaskapi.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 100, message = "title must not exceed 100 characters")
        String title,

        @Size(max = 500, message = "description must not exceed 500 characters")
        String description,

        @NotNull(message = "status must not be null")
        TaskStatus status
) {
}
