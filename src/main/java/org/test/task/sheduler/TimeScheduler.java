package org.test.task.sheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.test.task.service.TimeEntryService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class TimeScheduler {
    private static final Logger log = LoggerFactory.getLogger(TimeScheduler.class);
    private final TimeEntryService service;
    private final DataSource dataSource;

    @Scheduled(fixedRate = 1000)
    public void logTime() {
        service.addTimeEntry();
    }

    @Scheduled(initialDelay = 5000, fixedRate = 5000)
    public void saveBatch() {
        if (!isDatabaseAvailable()) {
            log.warn("DB is unavailable, skipping time entry");
            return;
        }

        try {
            service.processQueue();
        } catch (Exception e) {
            log.error("Failed to log time entry, DB is unavailable", e);
        }
    }

    private boolean isDatabaseAvailable() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(1000);
        } catch (SQLException e) {
            return false;
        }
    }
}
