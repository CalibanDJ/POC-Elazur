package com.example.domain;

public class TimeslotStrengthComparator implements Comparator<Timeslot> {

    public int compare(Timeslot a, Timeslot b) {
        return new CompareToBuilder()
                .append(a.getId(), b.getId())
                .toComparison();
    }

}