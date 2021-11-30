package com.example.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class TimeTable {

    @ValueRangeProvider(id = "timeslotRange")
    @ProblemFactCollectionProperty
    private List<Timeslot> timeslotList;

    @ValueRangeProvider(id = "workOrderRange")
    @ProblemFactCollectionProperty
    private List<WorkOrder> workOrderList;

    @PlanningEntityCollectionProperty
    private List<AtomicWorkOrderPart> atomicWorkOrderPartList;

    @PlanningScore
    private HardMediumSoftScore score;

    public TimeTable(List<Timeslot> timeslotList, List<WorkOrder> workOrderList, List<AtomicWorkOrderPart> atomicWorkOrderPartList) {
        this.timeslotList = timeslotList;
        this.workOrderList = workOrderList;
        this.atomicWorkOrderPartList = atomicWorkOrderPartList;
    }

    public TimeTable() {
    }

    public List<Timeslot> getTimeslotList() {
        return timeslotList;
    }

    public void setTimeslotList(List<Timeslot> timeslotList) {
        this.timeslotList = timeslotList;
    }

    public List<WorkOrder> getWorkOrderList() {
        return workOrderList;
    }

    public void setWorkOrderList(List<WorkOrder> workOrderList) {
        this.workOrderList = workOrderList;
    }

    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    public List<AtomicWorkOrderPart> getAtomicWorkOrderPartList() {
        return atomicWorkOrderPartList;
    }

    public void setAtomicWorkOrderPartList(List<AtomicWorkOrderPart> atomicWorkOrderPartList) {
        this.atomicWorkOrderPartList = atomicWorkOrderPartList;
    }
}
