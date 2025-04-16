package org.test.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.task.entity.TimeEntry;

import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    List<TimeEntry> findAllByOrderByIdAsc();
}
