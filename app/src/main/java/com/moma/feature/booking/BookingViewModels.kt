package com.moma.feature.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moma.core.data.BookingRepository
import com.moma.core.data.GymRepository
import com.moma.core.model.Booking
import com.moma.core.model.Coach
import com.moma.core.model.GymProgram
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

data class CheckoutUiState(
    val loading: Boolean = true,
    val coach: Coach? = null,
    val program: GymProgram? = null,
    val slotTime: String = "19:00",
    val paying: Boolean = false,
    val confirmed: Boolean = false,
    val error: String? = null
) {
    val price: Double get() = coach?.hourlyRate ?: 0.0
    val commission: Double get() = round(price * BookingRepository.GYM_COMMISSION_RATE * 100) / 100
    val coachPayout: Double get() = price - commission
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gymRepository: GymRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val bookingId: String = savedStateHandle["bookingId"] ?: ""
    private val coachId: String = savedStateHandle["coachId"] ?: ""
    private val programId: String = savedStateHandle["programId"] ?: ""

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            gymRepository.ensureSeeded()
            val coach = gymRepository.observeCoach(coachId).first()
            val programs = gymRepository.observePrograms().first()
            val program = programs.firstOrNull { it.id == programId }
            _state.value = CheckoutUiState(
                loading = false,
                coach = coach,
                program = program,
                slotTime = program?.startTime ?: "19:00"
            )
        }
    }

    fun pay() {
        if (_state.value.paying || bookingId.isBlank()) return
        _state.value = _state.value.copy(paying = true)
        viewModelScope.launch {
            bookingRepository.confirmPayment(bookingId)
            _state.value = _state.value.copy(paying = false, confirmed = true)
        }
    }
}

data class BookingsUiState(
    val loading: Boolean = true,
    val upcoming: List<Booking> = emptyList(),
    val past: List<Booking> = emptyList()
) {
    val totalSpend: Double get() = upcoming.filter { it.status.name == "CONFIRMED" }.sumOf { it.price }
    val totalCommission: Double get() = upcoming.filter { it.status.name == "CONFIRMED" }.sumOf { it.commission }
}

@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    gymRepository: GymRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookingsUiState())
    val state: StateFlow<BookingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            gymRepository.ensureSeeded()
            bookingRepository.observeBookings().collect { bookings ->
                _state.value = BookingsUiState(
                    loading = false,
                    upcoming = bookings.filter { it.status.name != "CANCELLED" },
                    past = bookings.filter { it.status.name == "CANCELLED" }
                )
            }
        }
    }

    fun cancel(id: String) {
        viewModelScope.launch { bookingRepository.cancel(id) }
    }

    fun complete(id: String) {
        viewModelScope.launch { bookingRepository.complete(id) }
    }
}
