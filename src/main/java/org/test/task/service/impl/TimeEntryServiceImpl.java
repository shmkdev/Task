package org.test.task.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.test.task.dto.TimeEntryDto;
import org.test.task.entity.TimeEntry;
import org.test.task.repository.TimeEntryRepository;
import org.test.task.service.TimeEntryService;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeEntryServiceImpl implements TimeEntryService {
    private final TimeEntryRepository repository;
    private final ConcurrentLinkedQueue<TimeEntry> queue = new ConcurrentLinkedQueue<>();

    @Override
    public List<TimeEntryDto> findAllEntries() {
        return repository.findAllByOrderByIdAsc()
                .stream()
                .map(entry -> TimeEntryDto.builder()
                        .timestamp(entry.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void addTimeEntry() {
        TimeEntry timeEntry = new TimeEntry();
        try {
            repository.save(timeEntry);
            log.info("Save timestamp to DB: {}", timeEntry.getTimestamp());
        } catch (Exception e) {
            queue.add(timeEntry);
            log.error("Failed to save timeEntry to DB: {}, add timeEntry to queue", timeEntry, e);
        }
    }


    @Override
    public void processQueue() {
        while (!queue.isEmpty()) {
            try {
                repository.saveAll(queue);
                log.info("Saved timeEntry to DB: {}", queue);
            } catch (Exception e) {
                log.error("Failed to save timeEntry to DB: {}", queue, e);
                break;
            }
        }
    }
}
