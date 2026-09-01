package com.example.devopstaskapi.task;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task with id %d was not found".formatted(id));
    }
}
