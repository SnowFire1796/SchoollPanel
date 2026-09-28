package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SyncStatus {
    IDLE,
    SYNCING,
    ONLINE_SUCCESS,
    OFFLINE_MODE
}

data class SyncState(
    val status: SyncStatus = SyncStatus.IDLE,
    val lastSyncTime: Long? = null,
    val message: String = "آماده به کار در حالت محلی (آفلاین)"
)

class SyncManager(
    private val repository: SchoolRepository
) {
    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    suspend fun performSync() {
        _syncState.value = _syncState.value.copy(
            status = SyncStatus.SYNCING,
            message = "در حال اتصال به سرور و همگام‌سازی..."
        )

        val result = repository.syncAll()
        if (result.isSuccess) {
            _syncState.value = SyncState(
                status = SyncStatus.ONLINE_SUCCESS,
                lastSyncTime = System.currentTimeMillis(),
                message = "همگام‌سازی با سرور با موفقیت انجام شد."
            )
        } else {
            _syncState.value = SyncState(
                status = SyncStatus.OFFLINE_MODE,
                lastSyncTime = _syncState.value.lastSyncTime,
                message = result.exceptionOrNull()?.message ?: "دسترسی به سرور ممکن نیست؛ اپلیکیشن به صورت آفلاین فعال است."
            )
        }
    }
}
