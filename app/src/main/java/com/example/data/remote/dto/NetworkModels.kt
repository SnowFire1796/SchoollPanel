package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String, // کد ملی یا کد پرسنلی یا admin
    val password: String, // تاریخ تولد یا رمز عبور
    val role: String      // STUDENT, TEACHER, ADMIN
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val role: String?,
    val userId: String?,
    val fullName: String?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class GradeDto(
    val id: String,
    val studentId: String,
    val subjectId: String,
    val classId: String,
    val academicYearId: String,
    val termId: String,
    val gradeType: String,
    val score: Double,
    val createdAt: Long,
    val updatedAt: Long,
    val createdBy: String,
    val updatedBy: String
)

@JsonClass(generateAdapter = true)
data class GradeSubmitRequest(
    val studentId: String,
    val subjectId: String,
    val classId: String,
    val termId: String,
    val gradeType: String,
    val score: Double,
    val teacherId: String,
    val teacherName: String
)

@JsonClass(generateAdapter = true)
data class DailyReportDto(
    val id: String,
    val studentId: String,
    val subjectId: String,
    val eventType: String,
    val description: String,
    val previousScore: Double?,
    val newScore: Double,
    val timestamp: Long,
    val recordedBy: String
)

@JsonClass(generateAdapter = true)
data class SyncPushRequest(
    val unsyncedGrades: List<GradeDto>
)

@JsonClass(generateAdapter = true)
data class SyncResponse(
    val success: Boolean,
    val serverTimestamp: Long,
    val updatedGrades: List<GradeDto>?,
    val newReports: List<DailyReportDto>?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class AssignmentDto(
    val id: String,
    val teacherId: String,
    val classId: String,
    val subjectId: String
)
