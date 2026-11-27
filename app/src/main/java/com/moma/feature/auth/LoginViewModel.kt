package com.moma.feature.auth

import androidx.lifecycle.ViewModel
import com.moma.core.datastore.SessionStore
import com.moma.core.datastore.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.GYM_OWNER,
    val password: String = "",
    val error: String? = null,
    val signedIn: Boolean = false
) {
    val canSubmit: Boolean
        get() = email.contains("@") && password.length >= 6
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionStore: SessionStore
) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginUiState(signedIn = sessionStore.isSignedIn, role = sessionStore.role)
    )
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmail(value: String) = update { it.copy(email = value, error = null) }
    fun onName(value: String) = update { it.copy(name = value) }
    fun onPassword(value: String) = update { it.copy(password = value, error = null) }
    fun onRole(value: UserRole) = update { it.copy(role = value) }

    fun submit() {
        val current = _state.value
        if (!current.canSubmit) {
            _state.value = current.copy(error = "Enter a valid email and a 6+ character password.")
            return
        }
        sessionStore.signIn(current.email, current.role, current.name)
        _state.value = current.copy(signedIn = true)
    }

    fun signOut() {
        sessionStore.signOut()
        _state.value = LoginUiState()
    }

    fun continueAsGuest() {
        sessionStore.signIn("owner@axisbjj.my", UserRole.GYM_OWNER, "Axis BJJ Academy")
        _state.value = _state.value.copy(signedIn = true)
    }

    private fun update(transform: (LoginUiState) -> LoginUiState) {
        _state.value = transform(_state.value)
    }
}
