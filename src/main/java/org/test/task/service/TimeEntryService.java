package org.test.task.service;

import org.test.task.dto.TimeEntryDto;

import java.util.List;

public interface TimeEntryService {
    public void addTimeEntry();
    public List<TimeEntryDto> findAllEntries();
    public void processQueue();
}
