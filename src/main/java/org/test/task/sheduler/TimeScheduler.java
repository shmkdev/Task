package org.test.task.sheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.test.task.service.TimeEntryService;

@Component
@RequiredArgsConstructor
public class TimeScheduler {
    private final TimeEntryService service;

    @Scheduled(fixedRate = 1000)
    public void logTime() {
        service.addTimeEntry();
    }

    @Scheduled(fixedRate = 5000)
    public void saveBatch() {
        service.processQueue();
    }
}
