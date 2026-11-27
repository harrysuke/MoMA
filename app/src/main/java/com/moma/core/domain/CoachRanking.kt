package com.moma.core.domain

import com.moma.core.model.Coach
import com.moma.core.model.CoachRequirement
import com.moma.core.model.DayOfWeek
import com.moma.core.model.MatchScore
import com.moma.core.model.RankedCoach
import com.moma.core.model.SlotStatus
import kotlin.math.max
import kotlin.math.min

/**
 * Explainable ranking used after the credential gate.
 *
 * Weights (sum = 100): style 30, credential 25, availability 15, distance 10,
 * rating 10, price 10. The breakdown is shown in the UI so a gym owner can see
 * *why* a coach was ranked where they were — important when a hiring decision is
 * challenged.
 */
object CoachRanking {

    fun rank(
        requirement: CoachRequirement,
        coaches: List<Coach>,
        targetDay: DayOfWeek
    ): List<RankedCoach> =
        coaches
            .map { coach -> coach to CredentialGate.evaluate(requirement, coach) }
            .filter { (_, gate) -> gate.passed }
            .map { (coach, gate) -> RankedCoach(coach = coach, gate = gate, score = score(requirement, coach, targetDay)) }
            .sortedByDescending { it.score.total }

    fun score(requirement: CoachRequirement, coach: Coach, targetDay: DayOfWeek): MatchScore {
        val stylePoints = if (coach.styles.contains(requirement.style)) 30 else 18

        val yearsOver = coach.yearsExperience - requirement.minimumYears
        val credentialPoints = when {
            yearsOver >= 5 -> 25
            yearsOver >= 2 -> 22
            yearsOver >= 0 -> 18
            else -> 10
        }

        val freeOnDay = coach.availability.count { it.day == targetDay && it.status == SlotStatus.FREE }
        val availabilityPoints = when {
            freeOnDay >= 2 -> 15
            freeOnDay == 1 -> 12
            coach.availability.any { it.status == SlotStatus.FREE } -> 6
            else -> 0
        }

        val distancePoints = max(0, (10 - (coach.distanceKm * 1.2)).toInt())

        val ratingPoints = min(10, ((coach.rating / 5.0) * 10).toInt())

        val pricePoints = when {
            coach.hourlyRate <= 100.0 -> 10
            coach.hourlyRate <= 130.0 -> 8
            coach.hourlyRate <= 160.0 -> 6
            else -> 4
        }

        return MatchScore(
            total = stylePoints + credentialPoints + availabilityPoints + distancePoints + ratingPoints + pricePoints,
            stylePoints = stylePoints,
            credentialPoints = credentialPoints,
            availabilityPoints = availabilityPoints,
            distancePoints = distancePoints,
            ratingPoints = ratingPoints,
            pricePoints = pricePoints
        )
    }
}
