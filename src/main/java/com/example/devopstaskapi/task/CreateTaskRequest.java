package com.example.devopstaskapi.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 100, message = "title must not exceed 100 characters")
        String title,

        @Size(max = 500, message = "description must not exceed 500 characters")
        String description,

        TaskStatus status
) {
}
