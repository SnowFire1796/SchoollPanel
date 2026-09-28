package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ClassEntity
import com.example.data.local.entity.DailyReportEntity
import com.example.data.local.entity.GradeEntity
import com.example.data.local.entity.SchoolEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TeacherAssignmentEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.TermEntity
import com.example.data.remote.ApiClient
import com.example.data.repository.SchoolRepository
import com.example.data.repository.SessionManager
import com.example.data.repository.SyncManager
import com.example.data.repository.SyncState
import com.example.data.repository.UserRole
import com.example.data.repository.UserSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    val database = AppDatabase.getDatabase(application)
    val apiClient = ApiClient(application)
    val sessionManager = SessionManager(application)
    val repository = SchoolRepository(database, apiClient, sessionManager)
    val syncManager = SyncManager(repository)

    val sessionState: StateFlow<UserSession> = sessionManager.sessionState
    val syncState: StateFlow<SyncState> = syncManager.syncState

    val school: StateFlow<SchoolEntity?> = repository.getSchool()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val terms: StateFlow<List<TermEntity>> = repository.getTerms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val classes: StateFlow<List<ClassEntity>> = repository.getAllClasses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjects: StateFlow<List<SubjectEntity>> = repository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teachers: StateFlow<List<TeacherEntity>> = repository.getAllTeachers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssignments: StateFlow<List<TeacherAssignmentEntity>> = repository.getAllAssignments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentCount: StateFlow<Int> = repository.getStudentCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 400)

    // Current Student (when student is logged in)
    val currentStudent: StateFlow<StudentEntity?> = sessionState.flatMapLatest { session ->
        if (session.isLoggedIn && session.role == UserRole.STUDENT && session.userId.isNotBlank()) {
            flowOf(repository.getStudentById(session.userId))
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current Student's Grades
    val studentGrades: StateFlow<List<GradeEntity>> = sessionState.flatMapLatest { session ->
        if (session.isLoggedIn && session.role == UserRole.STUDENT && session.userId.isNotBlank()) {
            repository.getGradesByStudent(session.userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Student's Daily Reports
    val studentReports: StateFlow<List<DailyReportEntity>> = sessionState.flatMapLatest { session ->
        if (session.isLoggedIn && session.role == UserRole.STUDENT && session.userId.isNotBlank()) {
            repository.getReportsByStudent(session.userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Teacher Panel State
    val currentTeacher: StateFlow<TeacherEntity?> = sessionState.flatMapLatest { session ->
        if (session.isLoggedIn && session.role == UserRole.TEACHER && session.userId.isNotBlank()) {
            flowOf(database.teacherDao().getTeacherById(session.userId))
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val teacherAssignments: StateFlow<List<TeacherAssignmentEntity>> = sessionState.flatMapLatest { session ->
        if (session.isLoggedIn && session.role == UserRole.TEACHER && session.userId.isNotBlank()) {
            repository.getAssignmentsByTeacher(session.userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedTeacherAssignmentId = MutableStateFlow<String?>(null)
    val selectedTermId = MutableStateFlow("term_2")

    val studentsInSelectedClass: StateFlow<List<StudentEntity>> = selectedTeacherAssignmentId.flatMapLatest { asgId ->
        val asg = allAssignments.value.find { it.id == asgId }
        if (asg != null) {
            repository.getStudentsByClass(asg.classId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gradesForSelectedAssignment: StateFlow<List<GradeEntity>> = selectedTeacherAssignmentId.flatMapLatest { asgId ->
        val asg = allAssignments.value.find { it.id == asgId }
        if (asg != null) {
            repository.getGradesForClassAndSubject(asg.classId, asg.subjectId, selectedTermId.value)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Auth screen state
    val isLoginLoading = MutableStateFlow(false)
    val loginErrorMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.ensureDatabaseInitialized()
            // Auto sync attempt in background
            syncManager.performSync()
        }
    }

    fun login(role: UserRole, usernameOrCode: String, passwordOrBirth: String) {
        viewModelScope.launch {
            isLoginLoading.value = true
            loginErrorMessage.value = null

            val result = repository.login(role, usernameOrCode, passwordOrBirth)
            isLoginLoading.value = false

            if (result.isFailure) {
                loginErrorMessage.value = result.exceptionOrNull()?.message ?: "یه مشکلی پیش اومد. دوباره امتحان کن."
            }
        }
    }

    fun logout() {
        sessionManager.logout()
    }

    fun saveGrade(
        studentId: String,
        subjectId: String,
        classId: String,
        termId: String,
        gradeType: String,
        score: Double
    ) {
        viewModelScope.launch {
            val recordedBy = sessionState.value.fullName.ifBlank { "دبیر مربوطه" }
            repository.saveOrUpdateGrade(
                studentId = studentId,
                subjectId = subjectId,
                classId = classId,
                termId = termId,
                gradeType = gradeType,
                score = score,
                recordedByName = recordedBy
            )
        }
    }

    fun addAssignment(teacherId: String, classId: String, subjectId: String) {
        viewModelScope.launch {
            repository.addTeacherAssignment(teacherId, classId, subjectId)
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            repository.deleteAssignment(assignmentId)
        }
    }

    fun triggerSync() {
        viewModelScope.launch {
            syncManager.performSync()
        }
    }

    fun saveBaseUrl(url: String) {
        apiClient.baseUrl = url
    }

    fun resetDatabase() {
        viewModelScope.launch {
            repository.resetDatabaseToDefaults()
        }
    }
}
