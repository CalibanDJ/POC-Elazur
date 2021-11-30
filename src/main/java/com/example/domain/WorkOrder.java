package com.example.domain;

import java.time.LocalDate;

public class WorkOrder {

    private Long id;
    private Long duration;
    private LocalDate endDate;
    private final Long delay = 2L;

    public WorkOrder() {
    }

    public WorkOrder(Long id, Long duration, LocalDate endDate) {
        this.id = id;
        this.duration = duration;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public Long getDuration() {
        return duration;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Long getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
