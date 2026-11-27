package com.moma.core.domain

import com.moma.core.data.toDomain
import com.moma.core.database.SeedData
import com.moma.core.model.Coach
import com.moma.core.model.DayOfWeek
import com.moma.core.model.MartialStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The credential gate is the product's promise to the gym: a role advertised as
 * "Judo black belt" must never be staffed by a brown belt, no matter how cheap or
 * well rated that coach is.
 */
class CredentialGateTest {

    private val requirements = SeedData.requirements().map { it.toDomain() }
    private val credentials = SeedData.credentials()
    private val slots = SeedData.slots()

    private val coaches: List<Coach> = SeedData.coaches().map { entity ->
        entity.toDomain(
            credentials = credentials.filter { it.coachId == entity.id },
            slots = slots.filter { it.coachId == entity.id }
        )
    }

    private fun coach(id: String): Coach = coaches.first { it.id == id }
    private fun requirement(id: String) = requirements.first { it.id == id }

    @Test
    fun `verified judo black belt passes the judo requirement`() {
        val result = CredentialGate.evaluate(requirement("req-judo"), coach("coach-arif"))

        assertTrue(result.passed)
        assertEquals("Judo 3rd Dan", result.satisfiedCredential?.label)
        assertTrue(result.missing.isEmpty())
    }

    @Test
    fun `brown belt with unverified document is blocked`() {
        val result = CredentialGate.evaluate(requirement("req-judo"), coach("coach-hafiz"))

        assertFalse(result.passed)
        assertTrue(result.reason.contains("not verified", ignoreCase = true))
    }

    @Test
    fun `wrestler with verified record passes the ADCC requirement`() {
        val result = CredentialGate.evaluate(requirement("req-wrestling"), coach("coach-rafiq"))

        assertTrue(result.passed)
        assertEquals("National squad wrestler 2014–2019", result.satisfiedCredential?.label)
    }

    @Test
    fun `unverified wrestling record is blocked`() {
        val result = CredentialGate.evaluate(requirement("req-wrestling"), coach("coach-suresh"))

        assertFalse(result.passed)
    }

    @Test
    fun `muay thai coach cannot cover the kyokushin role`() {
        val result = CredentialGate.evaluate(requirement("req-kyokushin"), coach("coach-meiling"))

        assertFalse(result.passed)
        assertTrue(result.reason.contains("different style", ignoreCase = true))
    }

    @Test
    fun `kyokushin black belt covers the kyokushin role`() {
        val result = CredentialGate.evaluate(requirement("req-kyokushin"), coach("coach-kenji"))

        assertTrue(result.passed)
    }

    @Test
    fun `muay thai coach covers the muay thai role on a coaching licence`() {
        val result = CredentialGate.evaluate(requirement("req-muaythai"), coach("coach-meiling"))

        assertTrue(result.passed)
        assertEquals("Certified Muay Thai coach", result.satisfiedCredential?.label)
    }

    @Test
    fun `ranking never contains a coach that failed the gate`() {
        val requirement = requirement("req-judo")
        val ranked = CoachRanking.rank(requirement, coaches, DayOfWeek.WED)

        assertTrue(ranked.isNotEmpty())
        assertTrue(ranked.none { it.coach.id == "coach-hafiz" })
        assertTrue(ranked.all { it.gate.passed })
    }

    @Test
    fun `ranking is ordered by descending total score`() {
        val ranked = CoachRanking.rank(requirement("req-judo"), coaches, DayOfWeek.WED)
        val totals = ranked.map { it.score.total }

        assertEquals(totals.sortedDescending(), totals)
    }

    @Test
    fun `score breakdown sums to the total`() {
        val requirement = requirement("req-judo")
        val coach = coach("coach-arif")
        val score = CoachRanking.score(requirement, coach, DayOfWeek.WED)

        val parts = score.stylePoints + score.credentialPoints + score.availabilityPoints +
            score.distancePoints + score.ratingPoints + score.pricePoints
        assertTrue(parts == score.total)
        assertTrue(score.total in 0..100)    }

    @Test
    fun `style mismatch yields reduced style points`() {
        val requirement = requirement("req-muaythai")
        val coach = coach("coach-rafiq")
        val score = CoachRanking.score(requirement, coach, DayOfWeek.FRI)

        assertTrue(score.stylePoints == 18)
        assertEquals(MartialStyle.WRESTLING, coach.styles.first())
    }
}
