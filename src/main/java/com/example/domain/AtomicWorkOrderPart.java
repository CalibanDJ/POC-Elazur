package com.example.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;

@PlanningEntity
public class AtomicWorkOrderPart {

    @PlanningId
    private Long id;

    private Timeslot timeslot;

    private WorkOrder workOrder;


    public AtomicWorkOrderPart() {
    }

    public AtomicWorkOrderPart(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    public Timeslot getTimeslot() {
        return timeslot;
    }

    @PlanningVariable(valueRangeProviderRefs = "workOrderRange", nullable = true)
    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    @Override
    public String toString() {
        return "AtomicWorkOrderPart{" +
                "id=" + id +
                '}';
    }
}
