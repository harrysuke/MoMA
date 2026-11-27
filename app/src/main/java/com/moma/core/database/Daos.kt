package com.moma.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GymDao {
    @Query("SELECT * FROM gyms LIMIT 1")
    fun observeGym(): Flow<GymEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGyms(gyms: List<GymEntity>)

    @Query("SELECT * FROM gym_programs ORDER BY dayIndex, startTime")
    fun observePrograms(): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM gym_programs ORDER BY dayIndex, startTime")
    suspend fun programs(): List<ProgramEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrograms(programs: List<ProgramEntity>)

    @Query("UPDATE gym_programs SET assignedCoachId = :coachId WHERE id = :programId")
    suspend fun assignCoach(programId: String, coachId: String?)

    @Query("SELECT * FROM coach_requirements ORDER BY roleTitle")
    fun observeRequirements(): Flow<List<RequirementEntity>>

    @Query("SELECT * FROM coach_requirements WHERE id = :id")
    fun observeRequirement(id: String): Flow<RequirementEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRequirements(requirements: List<RequirementEntity>)
}

@Dao
interface CoachDao {
    @Query("SELECT * FROM coaches ORDER BY rating DESC")
    fun observeCoaches(): Flow<List<CoachEntity>>

    @Query("SELECT * FROM coaches WHERE id = :id")
    fun observeCoach(id: String): Flow<CoachEntity?>

    @Query("SELECT * FROM coaches WHERE id = :id")
    suspend fun coach(id: String): CoachEntity?

    @Query("SELECT * FROM credentials")
    fun observeCredentials(): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials WHERE coachId = :coachId")
    suspend fun credentialsFor(coachId: String): List<CredentialEntity>

    @Query("SELECT * FROM coach_slots")
    fun observeSlots(): Flow<List<SlotEntity>>

    @Query("SELECT * FROM coach_slots WHERE coachId = :coachId ORDER BY dayIndex, time")
    suspend fun slotsFor(coachId: String): List<SlotEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCoaches(coaches: List<CoachEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCredentials(credentials: List<CredentialEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSlots(slots: List<SlotEntity>)

    @Query("UPDATE coach_slots SET status = :status WHERE id = :slotId")
    suspend fun updateSlotStatus(slotId: String, status: String)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY createdAtLabel DESC")
    fun observeBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun booking(id: String): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: BookingEntity)

    @Update
    suspend fun update(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun delete(id: String)
}
