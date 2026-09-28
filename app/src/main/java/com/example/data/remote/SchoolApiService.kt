package com.example.data.remote

import com.example.data.remote.dto.AssignmentDto
import com.example.data.remote.dto.AuthResponse
import com.example.data.remote.dto.DailyReportDto
import com.example.data.remote.dto.GradeDto
import com.example.data.remote.dto.GradeSubmitRequest
import com.example.data.remote.dto.LoginRequest
import com.example.data.remote.dto.SyncPushRequest
import com.example.data.remote.dto.SyncResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SchoolApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("grades/student/{studentId}")
    suspend fun getStudentGrades(
        @Path("studentId") studentId: String,
        @Query("termId") termId: String? = null
    ): Response<List<GradeDto>>

    @POST("grades/submit")
    suspend fun submitGrade(@Body request: GradeSubmitRequest): Response<GradeDto>

    @GET("reports/student/{studentId}")
    suspend fun getStudentReports(
        @Path("studentId") studentId: String
    ): Response<List<DailyReportDto>>

    @GET("assignments/teacher/{teacherId}")
    suspend fun getTeacherAssignments(
        @Path("teacherId") teacherId: String
    ): Response<List<AssignmentDto>>

    @POST("sync")
    suspend fun syncWithServer(@Body request: SyncPushRequest): Response<SyncResponse>
}
