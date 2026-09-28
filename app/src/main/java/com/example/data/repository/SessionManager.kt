package com.example.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class UserRole(val title: String) {
    STUDENT("دانش‌آموز و اولیا"),
    TEACHER("دبیر گرامی"),
    ADMIN("مدیریت مدرسه")
}

data class UserSession(
    val isLoggedIn: Boolean = false,
    val role: UserRole = UserRole.STUDENT,
    val userId: String = "",
    val username: String = "",
    val fullName: String = "",
    val classId: String = "",
    val token: String = ""
)

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("school_panel_session", Context.MODE_PRIVATE)

    private val _sessionState = MutableStateFlow(loadSession())
    val sessionState: StateFlow<UserSession> = _sessionState.asStateFlow()

    private fun loadSession(): UserSession {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val roleStr = prefs.getString("role", UserRole.STUDENT.name) ?: UserRole.STUDENT.name
        val role = try {
            UserRole.valueOf(roleStr)
        } catch (_: Exception) {
            UserRole.STUDENT
        }
        val userId = prefs.getString("user_id", "") ?: ""
        val username = prefs.getString("username", "") ?: ""
        val fullName = prefs.getString("full_name", "") ?: ""
        val classId = prefs.getString("class_id", "") ?: ""
        val token = prefs.getString("token", "") ?: ""

        return UserSession(
            isLoggedIn = isLoggedIn,
            role = role,
            userId = userId,
            username = username,
            fullName = fullName,
            classId = classId,
            token = token
        )
    }

    fun saveSession(session: UserSession) {
        prefs.edit()
            .putBoolean("is_logged_in", session.isLoggedIn)
            .putString("role", session.role.name)
            .putString("user_id", session.userId)
            .putString("username", session.username)
            .putString("full_name", session.fullName)
            .putString("class_id", session.classId)
            .putString("token", session.token)
            .apply()
        _sessionState.value = session
    }

    fun logout() {
        prefs.edit().clear().apply()
        _sessionState.value = UserSession(isLoggedIn = false)
    }
}
