package com.moma.feature.gym

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moma.core.data.BookingRepository
import com.moma.core.data.GymRepository
import com.moma.core.model.Coach
import com.moma.core.model.CoachRequirement
import com.moma.core.model.CoverageRow
import com.moma.core.model.DayOfWeek
import com.moma.core.model.Gym
import com.moma.core.model.GymProgram
import com.moma.core.model.RankedCoach
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class GymHomeUiState(
    val loading: Boolean = true,
    val gym: Gym? = null,
    val programs: List<GymProgram> = emptyList(),
    val coverage: List<CoverageRow> = emptyList(),
    val today: DayOfWeek = DayOfWeek.MON,
    val assignedCount: Int = 0,
    val openRoles: Int = 0
) {
    val todayPrograms: List<GymProgram> get() = programs.filter { it.day == today }
}

@HiltViewModel
class GymHomeViewModel @Inject constructor(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GymHomeUiState())
    val state: StateFlow<GymHomeUiState> = _state.asStateFlow()

    private val today = DayOfWeek.fromIndex(LocalDate.now().dayOfWeek.value)

    init {
        viewModelScope.launch {
            gymRepository.ensureSeeded()
            combine(
                gymRepository.observeGym(),
                gymRepository.observePrograms(),
                gymRepository.observeCoverage()
            ) { gym, programs, coverage ->
                GymHomeUiState(
                    loading = false,
                    gym = gym,
                    programs = programs,
                    coverage = coverage,
                    today = today,
                    assignedCount = coverage.count { it.assignedCoach != null },
                    openRoles = coverage.count { it.assignedCoach == null }
                )
            }.collect { _state.value = it }
        }
    }
}

data class CurriculumUiState(
    val loading: Boolean = true,
    val days: List<DaySection> = emptyList()
)

data class DaySection(
    val day: DayOfWeek,
    val programs: List<GymProgram>,
    val coachNames: Map<String, String>
)

@HiltViewModel
class CurriculumViewModel @Inject constructor(
    gymRepository: GymRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CurriculumUiState())
    val state: StateFlow<CurriculumUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            gymRepository.ensureSeeded()
            combine(
                gymRepository.observePrograms(),
                gymRepository.observeCoaches()
            ) { programs, coaches ->
                val names = coaches.associate { it.id to it.name }
                CurriculumUiState(
                    loading = false,
                    days = DayOfWeek.entries.map { day ->
                        DaySection(day, programs.filter { it.day == day }.sortedBy { it.startTime }, names)
                    }
                )
            }.collect { _state.value = it }
        }
    }
}

data class ShortlistUiState(
    val loading: Boolean = true,
    val requirement: CoachRequirement? = null,
    val ranked: List<RankedCoach> = emptyList(),
    val rejected: List<Pair<Coach, String>> = emptyList(),
    val programDay: DayOfWeek = DayOfWeek.WED
)

@HiltViewModel
class ShortlistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gymRepository: GymRepository
) : ViewModel() {

    private val requirementId: String = savedStateHandle["requirementId"] ?: "req-judo"

    private val _state = MutableStateFlow(ShortlistUiState())
    val state: StateFlow<ShortlistUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            gymRepository.ensureSeeded()
            combine(
                gymRepository.observeRequirement(requirementId),
                gymRepository.observeCoaches(),
                gymRepository.observePrograms()
            ) { requirement, coaches, programs ->
                if (requirement == null) {
                    ShortlistUiState(loading = false)
                } else {
                    val program = programs.firstOrNull {
                        it.style == requirement.style && !it.isOpenMat
                    }
                    val day = program?.day ?: DayOfWeek.WED
                    val gated = coaches.map { it to com.moma.core.domain.CredentialGate.evaluate(requirement, it) }
                    val passed = gated.filter { it.second.passed }.map { it.first }
                    val rejected = gated.filterNot { it.second.passed }
                        .map { it.first to it.second.reason }
                    ShortlistUiState(
                        loading = false,
                        requirement = requirement,
                        ranked = com.moma.core.domain.CoachRanking.rank(requirement, passed, day),
                        rejected = rejected,
                        programDay = day
                    )
                }
            }.collect { _state.value = it }
        }
    }
}

data class CoachDetailUiState(
    val loading: Boolean = true,
    val coach: Coach? = null,
    val requirement: CoachRequirement? = null,
    val gatePassed: Boolean? = null,
    val gateReason: String = "",
    val selectedSlot: String? = null,
    val bookingMessage: String? = null
)

@HiltViewModel
class CoachDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gymRepository: GymRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val coachId: String = savedStateHandle["coachId"] ?: "coach-arif"
    private val requirementId: String = savedStateHandle["requirementId"] ?: "req-judo"

    private val _state = MutableStateFlow(CoachDetailUiState())
    val state: StateFlow<CoachDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            gymRepository.ensureSeeded()
            combine(
                gymRepository.observeCoach(coachId),
                gymRepository.observeRequirement(requirementId)
            ) { coach, requirement ->
                val gate = if (coach != null && requirement != null) {
                    com.moma.core.domain.CredentialGate.evaluate(requirement, coach)
                } else {
                    null
                }
                CoachDetailUiState(
                    loading = false,
                    coach = coach,
                    requirement = requirement,
                    gatePassed = gate?.passed,
                    gateReason = gate?.reason.orEmpty(),
                    selectedSlot = _state.value.selectedSlot,
                    bookingMessage = _state.value.bookingMessage
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun selectSlot(slotId: String) {
        _state.value = _state.value.copy(selectedSlot = slotId)
    }

    fun requestBooking(onCreated: (String) -> Unit) {
        val coach = _state.value.coach ?: return
        val requirement = _state.value.requirement ?: return
        val slotId = _state.value.selectedSlot
        if (slotId == null) {
            _state.value = _state.value.copy(bookingMessage = "Pick a slot first.")
            return
        }
        viewModelScope.launch {
            val slot = coach.availability.firstOrNull { it.id == slotId }
            val program = GymProgram(
                id = "prog-" + requirement.style.id,
                gymId = requirement.gymId,
                day = slot?.day ?: DayOfWeek.WED,
                style = requirement.style,
                title = requirement.roleTitle.substringAfter("— ").ifBlank { requirement.roleTitle },
                focus = requirement.notes,
                ruleSet = requirement.ruleSet,
                startTime = slot?.time ?: "19:00",
                endTime = slot?.time ?: "20:30",
                level = "All levels",
                coachRole = requirement.roleTitle,
                assignedCoachId = coach.id
            )
            val id = bookingRepository.createBooking(
                gymId = requirement.gymId,
                coach = coach,
                program = program,
                time = slot?.time ?: "19:00"
            )
            onCreated(id)
        }
    }
}

data class RequirementFormState(
    val roleTitle: String = "",
    val style: com.moma.core.model.MartialStyle = com.moma.core.model.MartialStyle.JUDO,
    val ruleSet: com.moma.core.model.RuleSet = com.moma.core.model.RuleSet.NONE,
    val credential: com.moma.core.model.CredentialType = com.moma.core.model.CredentialType.DAN_GRADE,
    val minimumGrade: String = "",
    val minimumYears: String = "3",
    val sessionsPerWeek: String = "1",
    val notes: String = "",
    val submitted: Boolean = false
) {
    val isValid: Boolean
        get() = roleTitle.isNotBlank() && minimumGrade.isNotBlank() && (minimumYears.toIntOrNull() ?: 0) > 0
}

@HiltViewModel
class RequirementViewModel @Inject constructor(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _form = MutableStateFlow(RequirementFormState())
    val form: StateFlow<RequirementFormState> = _form.asStateFlow()

    fun update(transform: (RequirementFormState) -> RequirementFormState) {
        _form.value = transform(_form.value)
    }

    fun submit() {
        if (!_form.value.isValid) return
        viewModelScope.launch {
            gymRepository.ensureSeeded()
            _form.value = _form.value.copy(submitted = true)
        }
    }
}
