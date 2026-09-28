package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.GradeEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TermEntity
import com.example.ui.components.EmptyView
import com.example.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentReportCardScreen(
    student: StudentEntity?,
    className: String,
    terms: List<TermEntity>,
    subjects: List<SubjectEntity>,
    grades: List<GradeEntity>
) {
    var selectedTermIndex by remember { mutableIntStateOf(if (terms.size > 1) 1 else 0) }
    val currentTerm = terms.getOrNull(selectedTermIndex)
    val termId = currentTerm?.id ?: "term_2"

    val termGrades = grades.filter { it.termId == termId }

    // Weighted calculations
    var totalWeightedScore = 0.0
    var totalUnits = 0

    val reportRows = subjects.map { subject ->
        val cont = termGrades.find { it.subjectId == subject.id && it.gradeType == "مستمر" }?.score
        val fin = termGrades.find { it.subjectId == subject.id && it.gradeType == "پایانی" }?.score
        val finalTermScore = termGrades.find { it.subjectId == subject.id && it.gradeType == "نهایی" }?.score
            ?: if (cont != null && fin != null) (cont + fin) / 2.0 else null

        if (finalTermScore != null) {
            totalWeightedScore += finalTermScore * subject.unitCount
            totalUnits += subject.unitCount
        }

        ReportCardRow(
            subject = subject,
            unit = subject.unitCount,
            continuous = cont,
            finalScore = fin,
            termScore = finalTermScore
        )
    }

    val overallGpa = if (totalUnits > 0) totalWeightedScore / totalUnits else null

    // Previous term calculation for comparison
    val otherTermId = if (termId == "term_2") "term_1" else null
    val prevTermGrades = if (otherTermId != null) grades.filter { it.termId == otherTermId && it.gradeType == "نهایی" } else emptyList()
    val prevGpa = if (prevTermGrades.isNotEmpty()) prevTermGrades.map { it.score }.average() else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Term Tabs
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTermIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                terms.forEachIndexed { index, term ->
                    Tab(
                        selected = selectedTermIndex == index,
                        onClick = { selectedTermIndex = index },
                        text = {
                            Text(
                                text = "کارنامه ${term.title}",
                                fontWeight = if (selectedTermIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        if (reportRows.isEmpty() || termGrades.isEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                EmptyView(message = "کارنامه‌ای برای ${currentTerm?.title ?: "این دوره"} صادر نشده است.")
            }
        } else {
            // Official Report Card Sheet Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Formal Header
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "جمهوری اسلامی ایران",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "وزارت آموزش و پرورش • اداره کل شهر تهران",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "کارنامه تحصیلی دانش‌آموز",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "دبیرستان نمونه علامه طباطبایی • سال تحصیلی ۱۴۰۳-۱۴۰۴ (${currentTerm?.title})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Student Info Badge Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = "نام و نام خانوادگی: ${student?.firstName ?: ""} ${student?.lastName ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "کلاس: $className",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = "کد ملی: ${PersianUtils.toPersianDigits(student?.nationalCode ?: "—")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "شماره دانش‌آموزی: ${PersianUtils.toPersianDigits(student?.studentCode ?: "—")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Horizontal Scrollable Formal Table
                    val horizontalScrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        Column(
                            modifier = Modifier
                                .width(500.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ردیف", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                                Text("نام درس", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(160.dp))
                                Text("واحد", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
                                Text("مستمر", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
                                Text("پایانی", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
                                Text("نمره نهایی", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                            }

                            reportRows.forEachIndexed { index, row ->
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                val bg = if (index % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(bg)
                                        .padding(vertical = 8.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(PersianUtils.toPersianDigits(index + 1), style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                                    Text(row.subject.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, modifier = Modifier.width(160.dp))
                                    Text(PersianUtils.toPersianDigits(row.unit), style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
                                    Text(if (row.continuous != null) PersianUtils.formatScore(row.continuous) else "—", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
                                    Text(if (row.finalScore != null) PersianUtils.formatScore(row.finalScore) else "—", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
                                    Text(
                                        text = if (row.termScore != null) PersianUtils.formatScore(row.termScore) else "—",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (row.termScore != null && row.termScore >= 10.0) MaterialTheme.colorScheme.primary else Color(0xFFDC2626),
                                        modifier = Modifier.width(80.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary Results Footer
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "معدل کل کارنامه (میانگین وزنی):",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = if (overallGpa != null) PersianUtils.formatScore(overallGpa) else "—",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "تعداد کل واحدهای درسی: ${PersianUtils.toPersianDigits(totalUnits)} واحد",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "وضعیت قبولی: قبولی ممتاز",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }

                            if (prevGpa != null && overallGpa != null) {
                                val diff = overallGpa - prevGpa
                                val diffFormatted = PersianUtils.formatScore(kotlin.math.abs(diff))
                                val diffText = if (diff >= 0) "افزایش عملکرد: +$diffFormatted نمره نسبت به نوبت اول" else "تغییر عملکرد: -$diffFormatted نمره نسبت به نوبت اول"
                                Text(
                                    text = diffText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (diff >= 0) Color(0xFF047857) else Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ReportCardRow(
    val subject: SubjectEntity,
    val unit: Int,
    val continuous: Double?,
    val finalScore: Double?,
    val termScore: Double?
)
