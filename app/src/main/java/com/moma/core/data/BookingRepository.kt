package com.moma.core.data

import com.moma.core.database.BookingDao
import com.moma.core.database.BookingEntity
import com.moma.core.model.Booking
import com.moma.core.model.BookingStatus
import com.moma.core.model.Coach
import com.moma.core.model.GymProgram
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

/**
 * Booking state machine:
 * PENDING_PAYMENT -> CONFIRMED -> COMPLETED
 *                 -> CANCELLED
 *
 * In production the transitions are owned by the server (see the documented
 * booking/payment sequence). This local implementation keeps the same states so
 * the UI does not have to change when the API is wired in.
 */
@Singleton
class BookingRepository @Inject constructor(
    private val bookingDao: BookingDao
) {

    companion object {
        /** Platform commission on gym-sourced bookings. */
        const val GYM_COMMISSION_RATE = 0.15
    }

    fun observeBookings(): Flow<List<Booking>> =
        bookingDao.observeBookings().map { list -> list.map { it.toDomain() } }

    suspend fun createBooking(
        gymId: String,
        coach: Coach,
        program: GymProgram,
        time: String
    ): String {
        val commission = round(coach.hourlyRate * GYM_COMMISSION_RATE * 100) / 100
        val id = "bk-" + UUID.randomUUID().toString().take(8)
        bookingDao.insert(
            BookingEntity(
                id = id,
                gymId = gymId,
                coachId = coach.id,
                coachName = coach.name,
                programTitle = program.title,
                styleId = program.style.id,
                ruleSetId = program.ruleSet.id,
                dayIndex = program.day.index,
                time = time,
                price = coach.hourlyRate,
                commission = commission,
                status = BookingStatus.PENDING_PAYMENT.name,
                createdAtLabel = "Just now"
            )
        )
        return id
    }

    suspend fun confirmPayment(bookingId: String) {
        bookingDao.updateStatus(bookingId, BookingStatus.CONFIRMED.name)
    }

    suspend fun complete(bookingId: String) {
        bookingDao.updateStatus(bookingId, BookingStatus.COMPLETED.name)
    }

    suspend fun cancel(bookingId: String) {
        bookingDao.updateStatus(bookingId, BookingStatus.CANCELLED.name)
    }

    suspend fun delete(bookingId: String) = bookingDao.delete(bookingId)
}
