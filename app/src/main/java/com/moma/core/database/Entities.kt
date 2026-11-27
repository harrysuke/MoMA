package com.moma.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "gyms")
data class GymEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val address: String,
    val tagline: String,
    val verified: Boolean
)

@Entity(
    tableName = "gym_programs",
    foreignKeys = [
        ForeignKey(
            entity = GymEntity::class,
            parentColumns = ["id"],
            childColumns = ["gymId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("gymId"), Index("dayIndex")]
)
data class ProgramEntity(
    @PrimaryKey val id: String,
    val gymId: String,
    val dayIndex: Int,
    val styleId: String,
    val title: String,
    val focus: String,
    val ruleSetId: String,
    val startTime: String,
    val endTime: String,
    val level: String,
    val coachRole: String,
    val assignedCoachId: String?,
    val isOpenMat: Boolean
)

@Entity(
    tableName = "coach_requirements",
    foreignKeys = [
        ForeignKey(
            entity = GymEntity::class,
            parentColumns = ["id"],
            childColumns = ["gymId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("gymId")]
)
data class RequirementEntity(
    @PrimaryKey val id: String,
    val gymId: String,
    val roleTitle: String,
    val styleId: String,
    val ruleSetId: String,
    val requiredCredentialId: String,
    val minimumGrade: String,
    val minimumYears: Int,
    val sessionsPerWeek: Int,
    val notes: String
)

@Entity(tableName = "coaches", indices = [Index("city")])
data class CoachEntity(
    @PrimaryKey val id: String,
    val name: String,
    val headline: String,
    val city: String,
    val distanceKm: Double,
    val hourlyRate: Double,
    val rating: Double,
    val reviewCount: Int,
    val verified: Boolean,
    val yearsExperience: Int,
    val bio: String,
    val styleIdsCsv: String,
    val ruleSetIdsCsv: String
)

@Entity(
    tableName = "credentials",
    foreignKeys = [
        ForeignKey(
            entity = CoachEntity::class,
            parentColumns = ["id"],
            childColumns = ["coachId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("coachId"), Index(value = ["coachId", "typeId"], unique = false)]
)
data class CredentialEntity(
    @PrimaryKey val id: String,
    val coachId: String,
    val typeId: String,
    val label: String,
    val issuer: String,
    val verified: Boolean
)

@Entity(
    tableName = "coach_slots",
    foreignKeys = [
        ForeignKey(
            entity = CoachEntity::class,
            parentColumns = ["id"],
            childColumns = ["coachId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("coachId")]
)
data class SlotEntity(
    @PrimaryKey val id: String,
    val coachId: String,
    val dayIndex: Int,
    val time: String,
    val status: String
)

@Entity(tableName = "bookings", indices = [Index("gymId"), Index("coachId"), Index("status")])
data class BookingEntity(
    @PrimaryKey val id: String,
    val gymId: String,
    val coachId: String,
    val coachName: String,
    val programTitle: String,
    val styleId: String,
    val ruleSetId: String,
    val dayIndex: Int,
    val time: String,
    val price: Double,
    val commission: Double,
    val status: String,
    val createdAtLabel: String
)
