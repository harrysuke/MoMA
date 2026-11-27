package com.moma.feature.profile

import androidx.lifecycle.ViewModel
import com.moma.core.datastore.SessionStore
import com.moma.core.datastore.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val role: UserRole = UserRole.GYM_OWNER,
    val signedIn: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionStore: SessionStore
) : ViewModel() {

    private val _state = MutableStateFlow(
        ProfileUiState(
            name = sessionStore.displayName,
            role = sessionStore.role,
            signedIn = sessionStore.isSignedIn
        )
    )
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun switchRole(role: UserRole) {
        sessionStore.role = role
        _state.value = _state.value.copy(role = role)
    }

    fun signOut() {
        sessionStore.signOut()
        _state.value = ProfileUiState(signedIn = false)
    }
}
