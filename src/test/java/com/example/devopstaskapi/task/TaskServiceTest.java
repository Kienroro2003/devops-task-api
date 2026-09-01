package com.example.devopstaskapi.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-01T00:00:00Z");

    @Mock
    private TaskRepository taskRepository;

    @Test
    void createDefaultsStatusAndNormalizesText() {
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TaskService service = new TaskService(taskRepository, Clock.fixed(NOW, ZoneOffset.UTC));

        TaskResponse response = service.create(new CreateTaskRequest("  Ship API  ", "  Add CI  ", null));

        assertThat(response.title()).isEqualTo("Ship API");
        assertThat(response.description()).isEqualTo("Add CI");
        assertThat(response.status()).isEqualTo(TaskStatus.TODO);
        assertThat(response.createdAt()).isEqualTo(NOW);
        assertThat(response.updatedAt()).isEqualTo(NOW);
        verify(taskRepository).save(any(Task.class));
    }
}
