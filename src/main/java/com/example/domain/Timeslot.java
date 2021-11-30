package com.example.domain;


import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.time.LocalDate;

public class Timeslot {

    private Long id;

    private LocalDate date;
    private Long duration;
    private Long allowedWO;

    private Timeslot() {
    }

    public Timeslot(Long id, LocalDate date, Long duration, Long allowedWO) {
        this.id = id;
        this.date = date;
        this.duration = duration;
        this.allowedWO = allowedWO;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }
    public Long getDuration() {
        return duration;
    }

    public Long getAllowedWO() {
        return allowedWO;
    }


    @Override
    public String toString() {
        return "Timeslot{" +
                "id=" + id +
                ", duration=" + duration +
                ", allowedWO=" + allowedWO +
                '}';
    }
}
