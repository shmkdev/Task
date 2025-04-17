package org.test.task;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.test.task.entity.TimeEntry;
import org.test.task.repository.TimeEntryRepository;
import org.test.task.service.TimeEntryService;
import org.test.task.sheduler.TimeScheduler;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentCaptor.captor;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class TaskApplicationTests {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private TimeEntryRepository repository;

    @Autowired
    private TimeEntryService service;

    @Autowired
    private TimeScheduler scheduler;

    @Test
    void testLogTime() {
        // Вызываем метод напрямую
        scheduler.logTime();

        // Проверяем, что метод сервиса был вызван
        verify(service, times(1)).addTimeEntry();
    }

//    @Test
//    void testGetTimeEntriesReturnsOrderedList() throws Exception {
//        service.addTimeEntry();
//        Thread.sleep(1000);
//        service.addTimeEntry();
//
//        mockMvc.perform(get("/api/time"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").exists())
//                .andExpect(jsonPath("$[1].id").exists())
//                .andDo(print());
//    }

    @Test
    void testProcessQueueAfterDatabaseRecovery() throws InterruptedException {
        postgres.stop();
        service.addTimeEntry();
        Thread.sleep(1000);
        service.addTimeEntry();
        postgres.start();

        service.processQueue();

        // Assert
        List<TimeEntry> savedEntities = repository.findAllByOrderByIdAsc();

        assertEquals(2, savedEntities.size());
    }

}
