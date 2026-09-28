package com.example.ui.screens.student

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DailyReportEntity
import com.example.data.local.entity.GradeEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.ui.components.EmptyView
import com.example.ui.components.GradeScoreBadge
import com.example.util.PersianUtils

@Composable
fun StudentHomeScreen(
    student: StudentEntity?,
    className: String,
    grades: List<GradeEntity>,
    subjects: List<SubjectEntity>,
    recentReports: List<DailyReportEntity>,
    onNavigateToGrades: () -> Unit,
    onNavigateToReportCard: () -> Unit,
    onNavigateToDailyReport: () -> Unit,
    onNavigateToSmartAnalysis: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Calculate current term average
    val term2Finals = grades.filter { it.termId == "term_2" && it.gradeType == "نهایی" }
    val term1Finals = grades.filter { it.termId == "term_1" && it.gradeType == "نهایی" }

    val currentGpa = if (term2Finals.isNotEmpty()) {
        term2Finals.map { it.score }.average()
    } else if (term1Finals.isNotEmpty()) {
        term1Finals.map { it.score }.average()
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "سلام ${student?.firstName ?: "دانش‌آموز"} عزیز!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${className} • کد دانش‌آموزی: ${PersianUtils.toPersianDigits(student?.studentCode ?: "—")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "دبیرستان علامه طباطبایی • سال ۱۴۰۳-۱۴۰۴",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // GPA Circle Badge
                    if (currentGpa != null) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = PersianUtils.formatScore(currentGpa),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "معدل نوبت",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Navigation Grid
        Text(
            text = "دسترسی سریع",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HomeNavCard(
                title = "جدول نمرات",
                subtitle = "نمرات مستمر و پایانی",
                icon = Icons.Default.Grade,
                iconColor = MaterialTheme.colorScheme.primary,
                bgColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                onClick = onNavigateToGrades,
                modifier = Modifier
                    .weight(1f)
                    .testTag("nav_grades_card")
            )
            HomeNavCard(
                title = "کارنامه تحصیلی",
                subtitle = "مشاهده کارنامه دوره‌ای",
                icon = Icons.Default.Assignment,
                iconColor = Color(0xFF0D9488),
                bgColor = Color(0xFFCCFBF1),
                onClick = onNavigateToReportCard,
                modifier = Modifier
                    .weight(1f)
                    .testTag("nav_report_card")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HomeNavCard(
                title = "گزارش روزانه",
                subtitle = "تاریخچه تغییرات نمرات",
                icon = Icons.Default.History,
                iconColor = Color(0xFFD97706),
                bgColor = Color(0xFFFEF3C7),
                onClick = onNavigateToDailyReport,
                modifier = Modifier
                    .weight(1f)
                    .testTag("nav_daily_report_card")
            )
            HomeNavCard(
                title = "بررسی هوشمند",
                subtitle = "تحلیل نقاط قوت و افت",
                icon = Icons.Default.Analytics,
                iconColor = Color(0xFF7C3AED),
                bgColor = Color(0xFFEDE9FE),
                onClick = onNavigateToSmartAnalysis,
                modifier = Modifier
                    .weight(1f)
                    .testTag("nav_smart_analysis_card")
            )
        }

        // Recent Updates Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "آخرین تغییرات نمرات شما",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "مشاهده همه",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToDailyReport() }
            )
        }

        if (recentReports.isEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                EmptyView(message = "هنوز نمره‌ای برای شما ثبت نشده است.")
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentReports.take(4).forEach { report ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = report.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = PersianUtils.formatDate(report.timestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "ساعت ${PersianUtils.formatTime(report.timestamp)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            GradeScoreBadge(score = report.newScore)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HomeNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
