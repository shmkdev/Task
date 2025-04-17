package org.test.task.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
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
        queue.add(timeEntry);
        log.info("Saved timeEntry to Queue: {}", timeEntry);
    }


    @Override
    @Transactional
    public void processQueue() {
        if (!queue.isEmpty()) {
            repository.saveAll(queue);
            log.info("Saved timeEntry batch to DB: {}", queue);
        }
    }
}

