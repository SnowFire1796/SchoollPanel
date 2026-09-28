package com.example.ui.screens.student

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.GradeEntity
import com.example.data.local.entity.SubjectEntity
import com.example.ui.components.EmptyView
import com.example.ui.components.GradeScoreBadge
import com.example.util.PersianUtils

@Composable
fun StudentSmartAnalysisScreen(
    subjects: List<SubjectEntity>,
    grades: List<GradeEntity>
) {
    // Current term (term_2) and previous term (term_1) calculations
    val subjectMap = subjects.associateBy { it.id }

    val term2Grades = grades.filter { it.termId == "term_2" }
    val term1Grades = grades.filter { it.termId == "term_1" }

    // Map each subject to (Term1 score, Term2 score)
    data class SubjectComparison(
        val subject: SubjectEntity,
        val t1Score: Double?,
        val t2Score: Double?,
        val diff: Double?
    )

    val comparisons = subjects.map { sub ->
        val t1 = term1Grades.find { it.subjectId == sub.id && it.gradeType == "نهایی" }?.score
        val t2 = term2Grades.find { it.subjectId == sub.id && it.gradeType == "نهایی" }?.score
        val diff = if (t1 != null && t2 != null) t2 - t1 else null
        SubjectComparison(sub, t1, t2, diff)
    }

    val evaluatedTerm2 = comparisons.filter { it.t2Score != null }
    val evaluatedTerm1 = comparisons.filter { it.t1Score != null }

    val currentGpa = if (evaluatedTerm2.isNotEmpty()) evaluatedTerm2.mapNotNull { it.t2Score }.average() else null
    val prevGpa = if (evaluatedTerm1.isNotEmpty()) evaluatedTerm1.mapNotNull { it.t1Score }.average() else null
    val gpaDiff = if (currentGpa != null && prevGpa != null) currentGpa - prevGpa else null

    // Top subjects (highest score in current term)
    val sortedByCurrent = evaluatedTerm2.sortedByDescending { it.t2Score ?: 0.0 }
    val topSubjects = sortedByCurrent.take(3)
    val lowSubjects = sortedByCurrent.takeLast(3).reversed()

    // Most improved and declined subjects
    val improvedSubjects = comparisons.filter { (it.diff ?: 0.0) > 0 }.sortedByDescending { it.diff ?: 0.0 }
    val declinedSubjects = comparisons.filter { (it.diff ?: 0.0) < 0 }.sortedBy { it.diff ?: 0.0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
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
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "بررسی هوشمند عملکرد تحصیلی",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "تحلیل تحلیلی، نقاط قوت و سنجش پیشرفت بر پایه نمرات واقعی",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        if (evaluatedTerm2.isEmpty() && evaluatedTerm1.isEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                EmptyView(message = "جهت ارائه بررسی هوشمند، حداقل نمرات یک دوره تحصیلی لازم است.")
            }
        } else {
            // Overall Metric Comparison Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "وضعیت معدل و روند پیشرفت",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "معدل نوبت جاری",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentGpa != null) PersianUtils.formatScore(currentGpa) else "—",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (prevGpa != null) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "معدل نوبت قبل",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = PersianUtils.formatScore(prevGpa),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (gpaDiff != null) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            )

                            val isUp = gpaDiff >= 0
                            val changeColor = if (isUp) Color(0xFF059669) else Color(0xFFDC2626)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "تغییر معدل",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                        contentDescription = null,
                                        tint = changeColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = (if (isUp) "+" else "") + PersianUtils.formatScore(gpaDiff),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = changeColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Strengths Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "نقاط قوت (دروس با بیشترین نمره و تسلط)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    topSubjects.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${item.subject.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            GradeScoreBadge(score = item.t2Score)
                        }
                    }
                }
            }

            // Opportunities for Improvement Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "دروس نیازمند تمرین بیشتر",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    lowSubjects.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${item.subject.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            GradeScoreBadge(score = item.t2Score)
                        }
                    }
                }
            }

            // Strategic Insights Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "راهبردها و توصیه‌های آموزشی",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val insights = mutableListOf<String>()

                    if (currentGpa != null && currentGpa >= 18.0) {
                        insights.add("عملکرد شما در این نوبت در رتبه برتر و ممتاز مدرسه قرار دارد. تداوم مطالعه پیوسته حفظ گردد.")
                    } else if (currentGpa != null && currentGpa >= 15.0) {
                        insights.add("عملکرد کلی خوب و قابل تقدیر است؛ با تمرکز روی دروس ضریب‌بالا می‌توانید معدل خود را به ممتاز ارتقا دهید.")
                    }

                    if (improvedSubjects.isNotEmpty()) {
                        val impNames = improvedSubjects.take(2).joinToString(" و ") { it.subject.name }
                        insights.add("رشد نمرات شما در $impNames نشان‌دهنده اثربخشی تمرین‌های اخیر شماست.")
                    }

                    if (declinedSubjects.isNotEmpty()) {
                        val decNames = declinedSubjects.take(2).joinToString(" و ") { it.subject.name }
                        insights.add("در دروس $decNames افت جزئی مشاهده شد؛ پیشنهاد می‌شود با حل نمونه سوالات آزمون‌ها آمادگی خود را افزایش دهید.")
                    }

                    if (insights.isEmpty()) {
                        insights.add("با ثبت نمرات سایر دروس، تحلیل هوشمند به‌صورت خودکار به‌روزرسانی خواهد شد.")
                    }

                    insights.forEach { insight ->
                        Text(
                            text = "✔ $insight",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
