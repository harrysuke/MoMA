package com.moma.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        GymEntity::class,
        ProgramEntity::class,
        RequirementEntity::class,
        CoachEntity::class,
        CredentialEntity::class,
        SlotEntity::class,
        BookingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MomaDatabase : RoomDatabase() {
    abstract fun gymDao(): GymDao
    abstract fun coachDao(): CoachDao
    abstract fun bookingDao(): BookingDao

    companion object {
        const val NAME = "moma.db"
    }
}
