package com.moma.core.data

import com.moma.core.database.BookingDao
import com.moma.core.database.CoachDao
import com.moma.core.database.GymDao
import com.moma.core.database.SeedData
import com.moma.core.domain.CoachRanking
import com.moma.core.domain.CredentialGate
import com.moma.core.model.Coach
import com.moma.core.model.CoachRequirement
import com.moma.core.model.CoverageRow
import com.moma.core.model.DayOfWeek
import com.moma.core.model.Gym
import com.moma.core.model.GymProgram
import com.moma.core.model.RankedCoach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymRepository @Inject constructor(
    private val gymDao: GymDao,
    private val coachDao: CoachDao,
    private val bookingDao: BookingDao
) {

    @Volatile
    private var seeded = false

    suspend fun ensureSeeded() {
        if (seeded) return
        val existing = gymDao.programs()
        if (existing.isEmpty()) {
            gymDao.upsertGyms(listOf(SeedData.gym()))
            gymDao.upsertPrograms(SeedData.programs())
            gymDao.upsertRequirements(SeedData.requirements())
            coachDao.upsertCoaches(SeedData.coaches())
            coachDao.upsertCredentials(SeedData.credentials())
            coachDao.upsertSlots(SeedData.slots())
        }
        seeded = true
    }

    fun observeGym(): Flow<Gym?> = gymDao.observeGym().map { it?.toDomain() }

    fun observePrograms(): Flow<List<GymProgram>> =
        gymDao.observePrograms().map { list -> list.map { it.toDomain() } }

    fun observeRequirements(): Flow<List<CoachRequirement>> =
        gymDao.observeRequirements().map { list -> list.map { it.toDomain() } }

    fun observeRequirement(id: String): Flow<CoachRequirement?> =
        gymDao.observeRequirement(id).map { it?.toDomain() }

    fun observeCoaches(): Flow<List<Coach>> =
        combine(
            coachDao.observeCoaches(),
            coachDao.observeCredentials(),
            coachDao.observeSlots()
        ) { coaches, credentials, slots ->
            coaches.map { entity ->
                entity.toDomain(
                    credentials = credentials.filter { it.coachId == entity.id },
                    slots = slots.filter { it.coachId == entity.id }
                )
            }
        }

    fun observeCoach(id: String): Flow<Coach?> =
        combine(
            coachDao.observeCoach(id),
            coachDao.observeCredentials(),
            coachDao.observeSlots()
        ) { coach, credentials, slots ->
            coach?.toDomain(
                credentials = credentials.filter { it.coachId == id },
                slots = slots.filter { it.coachId == id }
            )
        }

    /** Coverage = which of the four specialist roles the gym can actually staff. */
    fun observeCoverage(): Flow<List<CoverageRow>> =
        combine(
            observeRequirements(),
            observeCoaches(),
            observePrograms()
        ) { requirements, coaches, programs ->
            requirements.map { requirement ->
                val assignedCoachId = programs
                    .firstOrNull { it.style == requirement.style && !it.isOpenMat }
                    ?.assignedCoachId
                val assigned = coaches.firstOrNull { it.id == assignedCoachId }
                val eligible = coaches.filter { CredentialGate.evaluate(requirement, it).passed }
                CoverageRow(
                    requirement = requirement,
                    assignedCoach = assigned,
                    candidateCount = eligible.size,
                    isCovered = assigned != null || eligible.isNotEmpty()
                )
            }
        }

    suspend fun rankedCoachesFor(requirement: CoachRequirement, day: DayOfWeek): List<RankedCoach> {
        val coaches = observeCoaches().first()
        return CoachRanking.rank(requirement, coaches, day)
    }

    suspend fun assignCoach(programId: String, coachId: String?) {
        gymDao.assignCoach(programId, coachId)
    }

    suspend fun setSlotStatus(slotId: String, status: String) {
        coachDao.updateSlotStatus(slotId, status)
    }

    fun observeBookings() = bookingDao.observeBookings().map { list -> list.map { it.toDomain() } }
}
