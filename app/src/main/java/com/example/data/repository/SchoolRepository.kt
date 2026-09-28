package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.local.entity.ClassEntity
import com.example.data.local.entity.DailyReportEntity
import com.example.data.local.entity.GradeEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TeacherAssignmentEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.TermEntity
import com.example.data.remote.ApiClient
import com.example.data.remote.dto.GradeSubmitRequest
import com.example.data.remote.dto.LoginRequest
import com.example.data.remote.dto.SyncPushRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

data class SubjectGradeSummary(
    val subject: SubjectEntity,
    val continuousGrade: GradeEntity?,
    val finalGrade: GradeEntity?,
    val termGrade: Double?,
    val status: String
)

data class ReportCardData(
    val student: StudentEntity,
    val className: String,
    val term: TermEntity,
    val academicYearTitle: String,
    val subjectGrades: List<SubjectGradeSummary>,
    val totalUnits: Int,
    val overallAverage: Double,
    val passed: Boolean,
    val statusText: String
)

data class SmartAnalysisData(
    val student: StudentEntity,
    val currentTermAverage: Double,
    val previousTermAverage: Double?,
    val difference: Double?,
    val highestSubjects: List<Pair<SubjectEntity, Double>>,
    val lowestSubjects: List<Pair<SubjectEntity, Double>>,
    val insights: List<String>
)

class SchoolRepository(
    private val database: AppDatabase,
    private val apiClient: ApiClient,
    val sessionManager: SessionManager
) {
    private val schoolDao = database.schoolDao()
    private val classDao = database.classDao()
    private val studentDao = database.studentDao()
    private val teacherDao = database.teacherDao()
    private val adminDao = database.adminDao()
    private val subjectDao = database.subjectDao()
    private val assignmentDao = database.teacherAssignmentDao()
    private val gradeDao = database.gradeDao()
    private val dailyReportDao = database.dailyReportDao()

    suspend fun ensureDatabaseInitialized() = withContext(Dispatchers.IO) {
        val studentCount = studentDao.getStudentCount().firstOrNull() ?: 0
        if (studentCount == 0) {
            DatabaseSeeder.seedDatabase(database)
        }
    }

    suspend fun resetDatabaseToDefaults() = withContext(Dispatchers.IO) {
        database.clearAllTables()
        DatabaseSeeder.seedDatabase(database)
    }

    // Auth
    suspend fun login(
        role: UserRole,
        usernameOrCode: String,
        passwordOrBirth: String
    ): Result<UserSession> = withContext(Dispatchers.IO) {
        ensureDatabaseInitialized()

        // 1. Try remote login first if server is available
        try {
            val response = apiClient.apiService.login(
                LoginRequest(
                    username = usernameOrCode.trim(),
                    password = passwordOrBirth.trim(),
                    role = role.name
                )
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                val session = UserSession(
                    isLoggedIn = true,
                    role = role,
                    userId = body.userId ?: "",
                    username = usernameOrCode.trim(),
                    fullName = body.fullName ?: "",
                    token = body.token ?: UUID.randomUUID().toString()
                )
                sessionManager.saveSession(session)
                return@withContext Result.success(session)
            }
        } catch (_: Exception) {
            // Server offline or network error, proceed to reliable local Room auth
        }

        // 2. Local Database Authentication
        when (role) {
            UserRole.STUDENT -> {
                val student = studentDao.getStudentByNationalCode(usernameOrCode.trim())
                if (student == null) {
                    return@withContext Result.failure(Exception("دانش‌آموزی با این کد ملی یافت نشد."))
                }
                // Check birthDate matching (supports 1388/06/15 or 13880615)
                val cleanInputBirth = passwordOrBirth.trim().replace("/", "")
                val cleanDbBirth = student.birthDate.replace("/", "")
                if (cleanInputBirth != cleanDbBirth) {
                    return@withContext Result.failure(Exception("تاریخ تولد وارد شده با پرونده دانش‌آموز همخوانی ندارد."))
                }

                val session = UserSession(
                    isLoggedIn = true,
                    role = UserRole.STUDENT,
                    userId = student.id,
                    username = student.nationalCode,
                    fullName = "${student.firstName} ${student.lastName}",
                    classId = student.classId,
                    token = UUID.randomUUID().toString()
                )
                sessionManager.saveSession(session)
                Result.success(session)
            }

            UserRole.TEACHER -> {
                val teacher = teacherDao.getTeacherByPersonnelCode(usernameOrCode.trim())
                if (teacher == null) {
                    return@withContext Result.failure(Exception("دبیری با این کد پرسنلی یافت نشد."))
                }
                if (teacher.password != passwordOrBirth.trim()) {
                    return@withContext Result.failure(Exception("رمز عبور وارد شده صحیح نمی‌باشد."))
                }

                val session = UserSession(
                    isLoggedIn = true,
                    role = UserRole.TEACHER,
                    userId = teacher.id,
                    username = teacher.personnelCode,
                    fullName = "${teacher.firstName} ${teacher.lastName} (${teacher.specialty})",
                    token = UUID.randomUUID().toString()
                )
                sessionManager.saveSession(session)
                Result.success(session)
            }

            UserRole.ADMIN -> {
                val admin = adminDao.getAdminByUsername(usernameOrCode.trim())
                if (admin == null) {
                    return@withContext Result.failure(Exception("نام کاربری مدیریت یافت نشد."))
                }
                if (admin.password != passwordOrBirth.trim()) {
                    return@withContext Result.failure(Exception("رمز عبور مدیر صحیح نمی‌باشد."))
                }

                val session = UserSession(
                    isLoggedIn = true,
                    role = UserRole.ADMIN,
                    userId = admin.id,
                    username = admin.username,
                    fullName = admin.fullName,
                    token = UUID.randomUUID().toString()
                )
                sessionManager.saveSession(session)
                Result.success(session)
            }
        }
    }

    // Queries
    fun getSchool() = schoolDao.getSchool()
    fun getAcademicYears() = schoolDao.getAcademicYears()
    fun getTerms() = schoolDao.getTerms()
    fun getAllClasses() = classDao.getAllClasses()
    fun getAllSubjects() = subjectDao.getAllSubjects()
    fun getAllTeachers() = teacherDao.getAllTeachers()
    fun getAllStudents() = studentDao.getAllStudents()
    fun getStudentCount() = studentDao.getStudentCount()
    fun getStudentsByClass(classId: String) = studentDao.getStudentsByClass(classId)
    suspend fun getStudentById(id: String) = studentDao.getStudentById(id)
    suspend fun getClassById(id: String) = classDao.getClassById(id)

    // Assignments
    fun getAllAssignments() = assignmentDao.getAllAssignments()
    fun getAssignmentsByTeacher(teacherId: String) = assignmentDao.getAssignmentsByTeacher(teacherId)

    suspend fun addTeacherAssignment(teacherId: String, classId: String, subjectId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val existing = assignmentDao.getAssignment(classId, subjectId)
        if (existing != null) {
            return@withContext Result.failure(Exception("این درس در این کلاس قبلاً به دبیر دیگری تخصیص داده شده است."))
        }
        val assignment = TeacherAssignmentEntity(
            id = "asg_${UUID.randomUUID()}",
            teacherId = teacherId,
            classId = classId,
            subjectId = subjectId
        )
        assignmentDao.insertAssignment(assignment)
        Result.success(Unit)
    }

    suspend fun deleteAssignment(id: String) = withContext(Dispatchers.IO) {
        assignmentDao.deleteAssignmentById(id)
    }

    // Grades
    fun getGradesByStudent(studentId: String) = gradeDao.getGradesByStudent(studentId)
    fun getGradesByStudentAndTerm(studentId: String, termId: String) = gradeDao.getGradesByStudentAndTerm(studentId, termId)
    fun getGradesForClassAndSubject(classId: String, subjectId: String, termId: String) =
        gradeDao.getGradesForClassAndSubject(classId, subjectId, termId)

    suspend fun saveOrUpdateGrade(
        studentId: String,
        subjectId: String,
        classId: String,
        termId: String,
        gradeType: String, // مستمر یا پایانی
        score: Double,
        recordedByName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (score < 0.0 || score > 20.0) {
            return@withContext Result.failure(Exception("نمره باید مقداری بین ۰ تا ۲۰ باشد."))
        }

        val existingGrade = gradeDao.getSpecificGrade(studentId, subjectId, termId, gradeType)
        val subject = subjectDao.getSubjectById(subjectId)
        val subjectTitle = subject?.name ?: "درس"

        val now = System.currentTimeMillis()
        val isEdit = existingGrade != null
        val previousScore = existingGrade?.score

        val gradeId = existingGrade?.id ?: "grd_${UUID.randomUUID()}"
        val gradeEntity = GradeEntity(
            id = gradeId,
            studentId = studentId,
            subjectId = subjectId,
            classId = classId,
            academicYearId = "ay_1403_1404",
            termId = termId,
            gradeType = gradeType,
            score = score,
            createdAt = existingGrade?.createdAt ?: now,
            updatedAt = now,
            createdBy = existingGrade?.createdBy ?: recordedByName,
            updatedBy = recordedByName,
            isSynced = false
        )

        gradeDao.insertGrade(gradeEntity)

        // Automatically update the "نهایی" (final term calculation) if continuous & final both exist
        val otherType = if (gradeType == "مستمر") "پایانی" else "مستمر"
        val otherGrade = gradeDao.getSpecificGrade(studentId, subjectId, termId, otherType)
        if (otherGrade != null) {
            val finalCalculatedScore = (score + otherGrade.score) / 2.0
            val existingFinalTerm = gradeDao.getSpecificGrade(studentId, subjectId, termId, "نهایی")
            val termFinalEntity = GradeEntity(
                id = existingFinalTerm?.id ?: "grd_t_${UUID.randomUUID()}",
                studentId = studentId,
                subjectId = subjectId,
                classId = classId,
                academicYearId = "ay_1403_1404",
                termId = termId,
                gradeType = "نهایی",
                score = finalCalculatedScore,
                createdAt = existingFinalTerm?.createdAt ?: now,
                updatedAt = now,
                createdBy = recordedByName,
                updatedBy = recordedByName,
                isSynced = false
            )
            gradeDao.insertGrade(termFinalEntity)
        }

        // Daily Report generation
        val eventType = if (isEdit) "ویرایش نمره" else "نمره جدید"
        val desc = if (isEdit) {
            "نمره $gradeType $subjectTitle از $previousScore به $score تغییر یافت."
        } else {
            "نمره $gradeType $subjectTitle ثبت شد: $score"
        }

        val report = DailyReportEntity(
            id = "rep_${UUID.randomUUID()}",
            studentId = studentId,
            subjectId = subjectId,
            eventType = eventType,
            description = desc,
            previousScore = previousScore,
            newScore = score,
            timestamp = now,
            recordedBy = recordedByName
        )
        dailyReportDao.insertReport(report)

        // Attempt background API push
        try {
            apiClient.apiService.submitGrade(
                GradeSubmitRequest(
                    studentId = studentId,
                    subjectId = subjectId,
                    classId = classId,
                    termId = termId,
                    gradeType = gradeType,
                    score = score,
                    teacherId = "",
                    teacherName = recordedByName
                )
            )
        } catch (_: Exception) {
            // Keep in local DB with isSynced = false
        }

        Result.success(Unit)
    }

    // Daily Reports
    fun getReportsByStudent(studentId: String): Flow<List<DailyReportEntity>> =
        dailyReportDao.getReportsByStudent(studentId)

    fun getAllRecentReports(): Flow<List<DailyReportEntity>> =
        dailyReportDao.getAllRecentReports()

    // Sync
    suspend fun syncAll(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val unsynced = gradeDao.getUnsyncedGrades()
            val dtos = unsynced.map {
                com.example.data.remote.dto.GradeDto(
                    id = it.id,
                    studentId = it.studentId,
                    subjectId = it.subjectId,
                    classId = it.classId,
                    academicYearId = it.academicYearId,
                    termId = it.termId,
                    gradeType = it.gradeType,
                    score = it.score,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    createdBy = it.createdBy,
                    updatedBy = it.updatedBy
                )
            }

            val response = apiClient.apiService.syncWithServer(SyncPushRequest(dtos))
            if (response.isSuccessful && response.body()?.success == true) {
                // Mark grades as synced
                unsynced.forEach {
                    gradeDao.insertGrade(it.copy(isSynced = true))
                }
                Result.success("همگام‌سازی با سرور با موفقیت انجام شد.")
            } else {
                Result.failure(Exception("پاسخ ناموفق از سرور مرکزی"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("سرور در دسترس نیست. اطلاعات به صورت محلی ذخیره گردید."))
        }
    }
}
