package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.UserRole
import com.example.ui.SchoolViewModel
import com.example.ui.components.SchoolTopBar
import com.example.ui.screens.admin.AdminPanelScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.student.StudentDailyReportScreen
import com.example.ui.screens.student.StudentGradesScreen
import com.example.ui.screens.student.StudentHomeScreen
import com.example.ui.screens.student.StudentReportCardScreen
import com.example.ui.screens.student.StudentSmartAnalysisScreen
import com.example.ui.screens.teacher.TeacherPanelScreen

enum class StudentTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("صفحه اصلی", Icons.Default.Home),
    GRADES("جدول نمرات", Icons.Default.Grade),
    REPORT_CARD("کارنامه", Icons.Default.Assignment),
    DAILY_REPORT("گزارش روزانه", Icons.Default.History),
    SMART_ANALYSIS("تحلیل هوشمند", Icons.Default.Analytics)
}

@Composable
fun AppNavigation(viewModel: SchoolViewModel) {
    val session by viewModel.sessionState.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val isLoginLoading by viewModel.isLoginLoading.collectAsStateWithLifecycle()
    val loginError by viewModel.loginErrorMessage.collectAsStateWithLifecycle()

    var isSettingsOpen by remember { mutableStateOf(false) }
    var currentStudentTab by remember { mutableStateOf(StudentTab.HOME) }

    if (!session.isLoggedIn) {
        LoginScreen(
            onLoginClick = { role, user, pass ->
                viewModel.login(role, user, pass)
            },
            isLoading = isLoginLoading,
            errorMessage = loginError
        )
        return
    }

    if (isSettingsOpen) {
        BackHandler { isSettingsOpen = false }
        Scaffold(
            topBar = {
                SchoolTopBar(
                    title = "تنظیمات و همگام‌سازی",
                    subtitle = "پیکربندی سرور و اتصال داده‌ها",
                    navigationIcon = {
                        IconButton(
                            onClick = { isSettingsOpen = false },
                            modifier = Modifier.testTag("back_from_settings")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SettingsScreen(
                    currentBaseUrl = viewModel.apiClient.baseUrl,
                    onSaveBaseUrl = { viewModel.saveBaseUrl(it) },
                    syncState = syncState,
                    onTriggerSync = { viewModel.triggerSync() },
                    onResetDatabase = { viewModel.resetDatabase() },
                    session = session,
                    onLogout = {
                        isSettingsOpen = false
                        viewModel.logout()
                    }
                )
            }
        }
        return
    }

    // Role-Based Views
    when (session.role) {
        UserRole.STUDENT -> {
            val student by viewModel.currentStudent.collectAsStateWithLifecycle()
            val classes by viewModel.classes.collectAsStateWithLifecycle()
            val subjects by viewModel.subjects.collectAsStateWithLifecycle()
            val terms by viewModel.terms.collectAsStateWithLifecycle()
            val grades by viewModel.studentGrades.collectAsStateWithLifecycle()
            val reports by viewModel.studentReports.collectAsStateWithLifecycle()

            val className = classes.find { it.id == student?.classId }?.name ?: "کلاس نهم الف"

            BackHandler(enabled = currentStudentTab != StudentTab.HOME) {
                currentStudentTab = StudentTab.HOME
            }

            Scaffold(
                topBar = {
                    SchoolTopBar(
                        title = currentStudentTab.title,
                        subtitle = "${student?.firstName ?: ""} ${student?.lastName ?: ""} • $className",
                        syncState = syncState,
                        onSyncClick = { viewModel.triggerSync() },
                        onSettingsClick = { isSettingsOpen = true },
                        onLogoutClick = { viewModel.logout() }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        StudentTab.entries.forEach { tab ->
                            val isSelected = currentStudentTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentStudentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentStudentTab) {
                        StudentTab.HOME -> {
                            StudentHomeScreen(
                                student = student,
                                className = className,
                                grades = grades,
                                subjects = subjects,
                                recentReports = reports,
                                onNavigateToGrades = { currentStudentTab = StudentTab.GRADES },
                                onNavigateToReportCard = { currentStudentTab = StudentTab.REPORT_CARD },
                                onNavigateToDailyReport = { currentStudentTab = StudentTab.DAILY_REPORT },
                                onNavigateToSmartAnalysis = { currentStudentTab = StudentTab.SMART_ANALYSIS }
                            )
                        }
                        StudentTab.GRADES -> {
                            StudentGradesScreen(
                                terms = terms,
                                subjects = subjects,
                                grades = grades
                            )
                        }
                        StudentTab.REPORT_CARD -> {
                            StudentReportCardScreen(
                                student = student,
                                className = className,
                                terms = terms,
                                subjects = subjects,
                                grades = grades
                            )
                        }
                        StudentTab.DAILY_REPORT -> {
                            StudentDailyReportScreen(
                                reports = reports,
                                subjects = subjects
                            )
                        }
                        StudentTab.SMART_ANALYSIS -> {
                            StudentSmartAnalysisScreen(
                                subjects = subjects,
                                grades = grades
                            )
                        }
                    }
                }
            }
        }

        UserRole.TEACHER -> {
            val teacher by viewModel.currentTeacher.collectAsStateWithLifecycle()
            val assignments by viewModel.teacherAssignments.collectAsStateWithLifecycle()
            val classes by viewModel.classes.collectAsStateWithLifecycle()
            val subjects by viewModel.subjects.collectAsStateWithLifecycle()
            val terms by viewModel.terms.collectAsStateWithLifecycle()
            val selectedAsgId by viewModel.selectedTeacherAssignmentId.collectAsStateWithLifecycle()
            val selectedTermId by viewModel.selectedTermId.collectAsStateWithLifecycle()
            val studentsInClass by viewModel.studentsInSelectedClass.collectAsStateWithLifecycle()
            val gradesForAssignment by viewModel.gradesForSelectedAssignment.collectAsStateWithLifecycle()

            Scaffold(
                topBar = {
                    SchoolTopBar(
                        title = "پنل دبیران",
                        subtitle = "${teacher?.firstName ?: ""} ${teacher?.lastName ?: ""} (${teacher?.specialty ?: ""})",
                        syncState = syncState,
                        onSyncClick = { viewModel.triggerSync() },
                        onSettingsClick = { isSettingsOpen = true },
                        onLogoutClick = { viewModel.logout() }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    TeacherPanelScreen(
                        teacher = teacher,
                        assignments = assignments,
                        classes = classes,
                        subjects = subjects,
                        terms = terms,
                        studentsInClass = studentsInClass,
                        gradesForAssignment = gradesForAssignment,
                        selectedAssignmentId = selectedAsgId,
                        onSelectAssignment = { viewModel.selectedTeacherAssignmentId.value = it },
                        selectedTermId = selectedTermId,
                        onSelectTerm = { viewModel.selectedTermId.value = it },
                        onSaveGrade = { stdId, subId, clsId, tId, gType, score ->
                            viewModel.saveGrade(stdId, subId, clsId, tId, gType, score)
                        }
                    )
                }
            }
        }

        UserRole.ADMIN -> {
            val studentCount by viewModel.studentCount.collectAsStateWithLifecycle()
            val teachers by viewModel.teachers.collectAsStateWithLifecycle()
            val classes by viewModel.classes.collectAsStateWithLifecycle()
            val subjects by viewModel.subjects.collectAsStateWithLifecycle()
            val assignments by viewModel.allAssignments.collectAsStateWithLifecycle()

            Scaffold(
                topBar = {
                    SchoolTopBar(
                        title = "پنل مدیریت مدرسه",
                        subtitle = session.fullName,
                        syncState = syncState,
                        onSyncClick = { viewModel.triggerSync() },
                        onSettingsClick = { isSettingsOpen = true },
                        onLogoutClick = { viewModel.logout() }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AdminPanelScreen(
                        studentCount = studentCount,
                        teachers = teachers,
                        classes = classes,
                        subjects = subjects,
                        assignments = assignments,
                        onAddAssignment = { tchId, clsId, subId ->
                            viewModel.addAssignment(tchId, clsId, subId)
                        },
                        onDeleteAssignment = { asgId ->
                            viewModel.deleteAssignment(asgId)
                        }
                    )
                }
            }
        }
    }
}
