package com.example.ui.screens.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.ClassEntity
import com.example.data.local.entity.GradeEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TeacherAssignmentEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.TermEntity
import com.example.ui.components.EmptyView
import com.example.ui.components.GradeScoreBadge
import com.example.util.PersianUtils
import kotlinx.coroutines.launch

@Composable
fun TeacherPanelScreen(
    teacher: TeacherEntity?,
    assignments: List<TeacherAssignmentEntity>,
    classes: List<ClassEntity>,
    subjects: List<SubjectEntity>,
    terms: List<TermEntity>,
    studentsInClass: List<StudentEntity>,
    gradesForAssignment: List<GradeEntity>,
    selectedAssignmentId: String?,
    onSelectAssignment: (String) -> Unit,
    selectedTermId: String,
    onSelectTerm: (String) -> Unit,
    onSaveGrade: (studentId: String, subjectId: String, classId: String, termId: String, gradeType: String, score: Double) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val classMap = classes.associateBy { it.id }
    val subjectMap = subjects.associateBy { it.id }

    // Dialog state for grade submission / editing
    var editingStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var selectedGradeType by remember { mutableStateOf("مستمر") }
    var scoreInputText by remember { mutableStateOf("") }
    var dialogError by remember { mutableStateOf<String?>(null) }

    val activeAssignment = assignments.find { it.id == selectedAssignmentId } ?: assignments.firstOrNull()

    LaunchedEffect(assignments) {
        if (selectedAssignmentId == null && assignments.isNotEmpty()) {
            onSelectAssignment(assignments.first().id)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Teacher Profile Header
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
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${teacher?.firstName ?: ""} ${teacher?.lastName ?: ""} (${teacher?.specialty ?: "دبیر"})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "کد پرسنلی: ${PersianUtils.toPersianDigits(teacher?.personnelCode ?: "")} • پنل ثبت و ویرایش نمرات",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Assignments Selector (Class + Subject)
            Text(
                text = "کلاس‌ها و دروس تخصیص‌یافته به شما:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (assignments.isEmpty()) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    EmptyView(message = "هنوز کلاسی به شما تخصیص داده نشده است.")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    assignments.forEach { asg ->
                        val cls = classMap[asg.classId]
                        val sub = subjectMap[asg.subjectId]
                        val isSelected = asg.id == activeAssignment?.id

                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectAssignment(asg.id) },
                            label = {
                                Text(
                                    text = "${cls?.name ?: ""} - ${sub?.name ?: ""}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                // Term Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "نوبت:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    terms.forEach { term ->
                        FilterChip(
                            selected = term.id == selectedTermId,
                            onClick = { onSelectTerm(term.id) },
                            label = { Text(term.title) }
                        )
                    }
                }

                // Student Grade List
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "نام دانش‌آموز",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = "مستمر",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.8f)
                            )
                            Text(
                                text = "پایانی",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.8f)
                            )
                            Text(
                                text = "عملیات",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.9f)
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        if (studentsInClass.isEmpty()) {
                            EmptyView(message = "دانش‌آموزی در این کلاس ثبت نشده است.")
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(studentsInClass) { student ->
                                    val contGrade = gradesForAssignment.find {
                                        it.studentId == student.id && it.termId == selectedTermId && it.gradeType == "مستمر"
                                    }
                                    val finGrade = gradesForAssignment.find {
                                        it.studentId == student.id && it.termId == selectedTermId && it.gradeType == "پایانی"
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1.2f)) {
                                            Text(
                                                text = "${student.firstName} ${student.lastName}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "کد: ${PersianUtils.toPersianDigits(student.studentCode)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Box(
                                            modifier = Modifier.weight(0.8f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            GradeScoreBadge(score = contGrade?.score)
                                        }

                                        Box(
                                            modifier = Modifier.weight(0.8f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            GradeScoreBadge(score = finGrade?.score)
                                        }

                                        Box(
                                            modifier = Modifier.weight(0.9f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    editingStudent = student
                                                    selectedGradeType = "مستمر"
                                                    scoreInputText = contGrade?.score?.toString() ?: ""
                                                    dialogError = null
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("edit_grade_btn_${student.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "ثبت نمره",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (contGrade != null || finGrade != null) "ویرایش" else "ثبت",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Grade Entry / Edit Dialog
        if (editingStudent != null && activeAssignment != null) {
            val student = editingStudent!!
            val sub = subjectMap[activeAssignment.subjectId]

            AlertDialog(
                onDismissRequest = { editingStudent = null },
                title = {
                    Text(
                        text = "ثبت نمره درس ${sub?.name ?: ""}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "دانش‌آموز: ${student.firstName} ${student.lastName} (کد: ${PersianUtils.toPersianDigits(student.studentCode)})",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Grade type switcher (مستمر / پایانی)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedGradeType == "مستمر",
                                onClick = {
                                    selectedGradeType = "مستمر"
                                    val current = gradesForAssignment.find {
                                        it.studentId == student.id && it.termId == selectedTermId && it.gradeType == "مستمر"
                                    }
                                    scoreInputText = current?.score?.toString() ?: ""
                                    dialogError = null
                                },
                                label = { Text("نمره مستمر") }
                            )
                            FilterChip(
                                selected = selectedGradeType == "پایانی",
                                onClick = {
                                    selectedGradeType = "پایانی"
                                    val current = gradesForAssignment.find {
                                        it.studentId == student.id && it.termId == selectedTermId && it.gradeType == "پایانی"
                                    }
                                    scoreInputText = current?.score?.toString() ?: ""
                                    dialogError = null
                                },
                                label = { Text("نمره پایانی") }
                            )
                        }

                        OutlinedTextField(
                            value = scoreInputText,
                            onValueChange = {
                                scoreInputText = it
                                dialogError = null
                            },
                            label = { Text("نمره (از ۰ تا ۲۰)") },
                            placeholder = { Text("مثال: ۱۸.۵") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dialog_score_input")
                        )

                        if (!dialogError.isNullOrBlank()) {
                            Text(
                                text = dialogError ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cleanNumber = scoreInputText.trim().replace('/', '.').replace('٫', '.')
                            val scoreVal = cleanNumber.toDoubleOrNull()
                            if (scoreVal == null || scoreVal < 0.0 || scoreVal > 20.0) {
                                dialogError = "لطفاً یک نمره معتبر بین ۰ تا ۲۰ وارد کنید."
                                return@Button
                            }

                            onSaveGrade(
                                student.id,
                                activeAssignment.subjectId,
                                activeAssignment.classId,
                                selectedTermId,
                                selectedGradeType,
                                scoreVal
                            )
                            editingStudent = null
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("نمره با موفقیت ثبت شد.")
                            }
                        },
                        modifier = Modifier.testTag("dialog_confirm_grade")
                    ) {
                        Text("ذخیره نمره")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { editingStudent = null }) {
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
