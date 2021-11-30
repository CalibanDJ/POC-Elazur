package com.example.solver;

import com.example.domain.AtomicWorkOrderPart;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.stream.Collectors;

public class TimeTableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                //allowAllWO(constraintFactory),
                penalizeUnderAssignation(constraintFactory),
                penalizeNullWorkOrder(constraintFactory),
                limitWONumberOnTimeSlot(constraintFactory), // HARD
                limitOverAssignation(constraintFactory),
                penalizeSameOnTimeSlotSameWO(constraintFactory), //HARD
                penaliseGapBetweenWO(constraintFactory),
               // minimizeDistance(constraintFactory),
                /*limitAWOPDurationOnTimeSlot(constraintFactory),
                minimizeDistance(constraintFactory)*/
        };
    }
    private Constraint penalizeSameOnTimeSlotSameWO(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(AtomicWorkOrderPart.class)
                .filter(atomicWorkOrderPart -> atomicWorkOrderPart.getWorkOrder() != null)
                .groupBy(AtomicWorkOrderPart::getTimeslot, ConstraintCollectors.countDistinct(awo -> awo.getWorkOrder().getId()),
                        ConstraintCollectors.count())
                .filter((wo, inte1, inte2) -> inte2 > 1 && !inte1.equals(inte2))
                .penalize("Penalize same WO on TS",
                        HardMediumSoftScore.ONE_HARD);
    }

    private Constraint penaliseGapBetweenWO(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(AtomicWorkOrderPart.class)
                .filter(awop -> awop.getWorkOrder() != null)
                .groupBy(AtomicWorkOrderPart::getWorkOrder,
                        ConstraintCollectors.count(),
                        ConstraintCollectors.sum(awop -> Math.toIntExact(awop.getTimeslot().getId())),
                        ConstraintCollectors.toList(awop -> Math.toIntExact(awop.getTimeslot().getId()))
                )
                .filter((wo, nWO, sTSId, listTS) -> nWO > 1 &&
                        (sTSId - (nWO * (nWO - 1) / 2)) % nWO != 0 ||
                        (sTSId - (nWO * (nWO - 1) / 2)) / nWO != listTS.get(0))
                .penalize("Penalize gap between WO",
                        HardMediumSoftScore.ONE_HARD);

                /*.filter(awop1 -> awop1.getWorkOrder() != null)
                .join(AtomicWorkOrderPart.class)
                .filter((awop1, awop2) -> awop2.getWorkOrder() != null)
                .filter((awop1, awop2) -> awop1.getWorkOrder().getId().equals(awop2.getWorkOrder().getId()))
                .penalize("Penalize gap between WO",
                        HardMediumSoftScore.ONE_HARD,
                        (awop1, awop2) -> Math.abs(Math.toIntExact(awop1.getTimeslot().getId() - awop2.getTimeslot().getId())));*/
    }

    private Constraint penaliseGapBetweenSameWO(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUniquePair(AtomicWorkOrderPart.class)
                .filter((awop1, awop2) -> awop1.getWorkOrder() != null && awop2.getWorkOrder() != null)
                .filter((awop1, awop2) -> awop1.getWorkOrder().getId().equals(awop2.getWorkOrder().getId()))
                .penalize("Penalize gap between WO",
                HardMediumSoftScore.ONE_HARD,
                        (awop1, awop2) -> Math.abs(Math.toIntExact(awop1.getTimeslot().getId() - awop2.getTimeslot().getId())));

                /*.filter(awop1 -> awop1.getWorkOrder() != null)
                .join(AtomicWorkOrderPart.class)
                .filter((awop1, awop2) -> awop2.getWorkOrder() != null)
                .filter((awop1, awop2) -> awop1.getWorkOrder().getId().equals(awop2.getWorkOrder().getId()))
                .penalize("Penalize gap between WO",
                        HardMediumSoftScore.ONE_HARD,
                        (awop1, awop2) -> Math.abs(Math.toIntExact(awop1.getTimeslot().getId() - awop2.getTimeslot().getId())));*/
    }

    private Constraint penalizeUnderAssignation(ConstraintFactory constraintFactory) {
        return constraintFactory.from(AtomicWorkOrderPart.class)
                .filter(atomicWorkOrderPart -> atomicWorkOrderPart.getWorkOrder() != null)
                .groupBy(AtomicWorkOrderPart::getWorkOrder, ConstraintCollectors.sum(awo -> Math.toIntExact(awo.getTimeslot().getDuration())))
                .filter((wo, inte) -> wo.getDuration() > inte)
                .penalize("Penalize WorkOrder that are not assignated",
                        HardMediumSoftScore.ONE_MEDIUM);
    }

    private Constraint limitOverAssignation(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(AtomicWorkOrderPart.class)
                .filter(atomicWorkOrderPart -> atomicWorkOrderPart.getWorkOrder() != null)
                .groupBy(AtomicWorkOrderPart::getWorkOrder, ConstraintCollectors.sum(awo -> Math.toIntExact(awo.getTimeslot().getDuration())))
                .filter((wo, integer) -> wo.getDuration() < integer)
                .penalize("Penalize over assignation WorkOder",
                        HardMediumSoftScore.ONE_MEDIUM,
                        (wo, integer) -> Math.abs(Math.toIntExact(wo.getDuration()) - integer));
    }

    private Constraint penalizeNullWorkOrder(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(AtomicWorkOrderPart.class)
                .filter(atomicWorkOrderPart -> atomicWorkOrderPart.getWorkOrder() == null)
                .penalize("Penalize null WorkOder",
                        HardMediumSoftScore.ONE_MEDIUM);
    }

    private Constraint limitWONumberOnTimeSlot(ConstraintFactory constraintFactory) {
        return constraintFactory.from(AtomicWorkOrderPart.class)
                .filter(awo -> awo.getWorkOrder() != null)
                .groupBy(AtomicWorkOrderPart::getTimeslot, ConstraintCollectors.count())
                .filter((timeslot, integer) -> integer > timeslot.getAllowedWO())
                .penalize("Too many WO on the same timeslot",
                        HardMediumSoftScore.ONE_HARD);
                        //(timeslot, integer) -> (int) (integer - timeslot.getAllowedWO()));
    }
/*
    private Constraint limitAWOPDurationOnTimeSlot(ConstraintFactory constraintFactory) {
        return constraintFactory.from(AtomicWorkOrderPart.class)
                .groupBy(AtomicWorkOrderPart::getTimeslot, ConstraintCollectors.sum(awop -> Math.toIntExact(awop.getDuration())))
                .filter((timeslot, integer) -> integer > timeslot.getDuration())
                .penalize("Sum of AWOP duration exceed timeslot duration",
                        HardSoftScore.ONE_HARD,
                        (timeslot, integer) -> (int) (integer - timeslot.getDuration()));
    }

    private Constraint twoConsecutivePeriods(ConstraintFactory constraintFactory) {
        return constraintFactory.from(AtomicWorkOrderPart.class)
                .ifExists(AtomicWorkOrderPart.class, Joiners.equal(AtomicWorkOrderPart::getTimeslot, atomic -> atomic.getTimeslot().getId() + 1))
                .penalize("2 consecutive periods", HardSoftScore.ONE_HARD);
    }

*/
    private Constraint minimizeDistance(ConstraintFactory constraintFactory) {
        return constraintFactory.from(AtomicWorkOrderPart.class)
                .filter(awo -> awo.getWorkOrder() != null)
                .penalize("AWOP distance should be minimized",
                        HardMediumSoftScore.ONE_HARD,
                        (awo) -> Math.toIntExact(Math.abs(ChronoUnit.DAYS.between(awo.getWorkOrder().getEndDate(), awo.getTimeslot().getDate()))));
    }

}
