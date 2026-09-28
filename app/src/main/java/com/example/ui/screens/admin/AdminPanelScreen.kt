package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.ClassEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TeacherAssignmentEntity
import com.example.data.local.entity.TeacherEntity
import com.example.ui.components.EmptyView
import com.example.util.PersianUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    studentCount: Int,
    teachers: List<TeacherEntity>,
    classes: List<ClassEntity>,
    subjects: List<SubjectEntity>,
    assignments: List<TeacherAssignmentEntity>,
    onAddAssignment: (teacherId: String, classId: String, subjectId: String) -> Unit,
    onDeleteAssignment: (assignmentId: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    val teacherMap = teachers.associateBy { it.id }
    val classMap = classes.associateBy { it.id }
    val subjectMap = subjects.associateBy { it.id }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Admin Dashboard Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "پنل مدیریت واحد آموزشی",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "دبیرستان نمونه علامه طباطبایی • سال ۱۴۰۳-۱۴۰۴",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Stats Quick Overview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "دانش‌آموزان",
                    value = PersianUtils.toPersianDigits(studentCount),
                    icon = Icons.Default.Group,
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "معلمان",
                    value = PersianUtils.toPersianDigits(teachers.size),
                    icon = Icons.Default.School,
                    color = Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "کلاس‌ها",
                    value = PersianUtils.toPersianDigits(classes.size),
                    icon = Icons.Default.Class,
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "دروس",
                    value = PersianUtils.toPersianDigits(subjects.size),
                    icon = Icons.Default.Book,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                )
            }

            // Admin Sections Tab Row
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("تخصیص معلمان", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("لیست دبیران", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("۱۳ درس مصوب", fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Teacher Assignments List (Teacher + Class + Subject)
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "جدول تخصیص دبیران به کلاس و درس",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "معلم + کلاس + درس (تخصیص اختصاصی)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = { showAddDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("add_assignment_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تخصیص جدید", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            if (assignments.isEmpty()) {
                                EmptyView(message = "هنوز تخصیصی ثبت نشده است.")
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(assignments) { asg ->
                                        val teacher = teacherMap[asg.teacherId]
                                        val cls = classMap[asg.classId]
                                        val sub = subjectMap[asg.subjectId]

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AssignmentInd,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = "${teacher?.firstName ?: ""} ${teacher?.lastName ?: ""}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "کلاس: ${cls?.name ?: ""} | درس: ${sub?.name ?: ""}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = {
                                                    onDeleteAssignment(asg.id)
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("تخصیص با موفقیت حذف شد.")
                                                    }
                                                },
                                                modifier = Modifier.testTag("delete_asg_${asg.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "حذف تخصیص",
                                                    tint = Color(0xFFDC2626)
                                                )
                                            }
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Teachers List
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(teachers) { teacher ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "${teacher.firstName} ${teacher.lastName}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${teacher.specialty} • کد پرسنلی: ${PersianUtils.toPersianDigits(teacher.personnelCode)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = teacher.phone,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                            }
                        }
                    }
                }

                2 -> {
                    // Subjects List (13 subjects)
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(subjects) { subject ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = subject.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "کد درس: ${PersianUtils.toPersianDigits(subject.code)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${PersianUtils.toPersianDigits(subject.unitCount)} واحد",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                            }
                        }
                    }
                }
            }
        }

        // Add Teacher Assignment Dialog
        if (showAddDialog) {
            var selectedTeacherId by remember { mutableStateOf(teachers.firstOrNull()?.id ?: "") }
            var selectedClassId by remember { mutableStateOf(classes.firstOrNull()?.id ?: "") }
            var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "") }
            var addError by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = {
                    Text(
                        text = "ایجاد تخصیص جدید معلم",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "معلم، کلاس و درس مورد نظر را جهت ایجاد دسترسی نمره‌دهی انتخاب کنید:",
                            style = MaterialTheme.typography.bodySmall
                        )

                        // Teacher Dropdown
                        var teacherExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = teacherExpanded,
                            onExpandedChange = { teacherExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = teacherMap[selectedTeacherId]?.let { "${it.firstName} ${it.lastName} (${it.specialty})" } ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("انتخاب دبیر") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = teacherExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = teacherExpanded,
                                onDismissRequest = { teacherExpanded = false }
                            ) {
                                teachers.forEach { tch ->
                                    DropdownMenuItem(
                                        text = { Text("${tch.firstName} ${tch.lastName} (${tch.specialty})") },
                                        onClick = {
                                            selectedTeacherId = tch.id
                                            teacherExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Class Dropdown
                        var classExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = classExpanded,
                            onExpandedChange = { classExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = classMap[selectedClassId]?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("انتخاب کلاس") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = classExpanded,
                                onDismissRequest = { classExpanded = false }
                            ) {
                                classes.forEach { cls ->
                                    DropdownMenuItem(
                                        text = { Text(cls.name) },
                                        onClick = {
                                            selectedClassId = cls.id
                                            classExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Subject Dropdown
                        var subjectExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = subjectExpanded,
                            onExpandedChange = { subjectExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = subjectMap[selectedSubjectId]?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("انتخاب درس") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = subjectExpanded,
                                onDismissRequest = { subjectExpanded = false }
                            ) {
                                subjects.forEach { sub ->
                                    DropdownMenuItem(
                                        text = { Text(sub.name) },
                                        onClick = {
                                            selectedSubjectId = sub.id
                                            subjectExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        if (!addError.isNullOrBlank()) {
                            Text(
                                text = addError ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedTeacherId.isBlank() || selectedClassId.isBlank() || selectedSubjectId.isBlank()) {
                                addError = "لطفاً تمام موارد را انتخاب کنید."
                                return@Button
                            }
                            onAddAssignment(selectedTeacherId, selectedClassId, selectedSubjectId)
                            showAddDialog = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("تخصیص با موفقیت ایجاد گردید.")
                            }
                        },
                        modifier = Modifier.testTag("confirm_add_assignment")
                    ) {
                        Text("ایجاد تخصیص")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showAddDialog = false }) {
                        Text("انصراف")
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
