package com.moma.core.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class UserRole(val label: String) {
    GYM_OWNER("Gym owner"),
    COACH("Coach"),
    STUDENT("Student")
}

/**
 * Session store. Tokens live in EncryptedSharedPreferences (backed by the Android
 * Keystore); if the keystore is unavailable we fall back to private prefs rather
 * than crashing, and the app degrades to "signed out on restart".
 */
@Singleton
class SessionStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences = runCatching {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "moma_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as SharedPreferences
    }.getOrElse {
        context.getSharedPreferences("moma_session_fallback", Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    var role: UserRole
        get() = runCatching { UserRole.valueOf(prefs.getString(KEY_ROLE, null) ?: UserRole.GYM_OWNER.name) }
            .getOrDefault(UserRole.GYM_OWNER)
        set(value) = prefs.edit().putString(KEY_ROLE, value.name).apply()

    var displayName: String
        get() = prefs.getString(KEY_NAME, "Axis BJJ Academy") ?: "Axis BJJ Academy"
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    val isSignedIn: Boolean get() = !token.isNullOrBlank()

    fun signIn(email: String, role: UserRole, name: String) {
        this.token = "local-dev-token"
        this.role = role
        this.displayName = name.ifBlank { email.substringBefore('@') }
    }

    fun signOut() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_TOKEN = "auth_token"
        const val KEY_ROLE = "user_role"
        const val KEY_NAME = "display_name"
    }
}
