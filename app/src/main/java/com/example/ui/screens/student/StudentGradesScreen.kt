package com.example.ui.screens.student

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.GradeEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TermEntity
import com.example.ui.components.EmptyView
import com.example.ui.components.GradeScoreBadge
import com.example.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentGradesScreen(
    terms: List<TermEntity>,
    subjects: List<SubjectEntity>,
    grades: List<GradeEntity>
) {
    var selectedTermIndex by remember { mutableIntStateOf(if (terms.size > 1) 1 else 0) }
    val currentTerm = terms.getOrNull(selectedTermIndex)
    val termId = currentTerm?.id ?: "term_2"

    val termGrades = grades.filter { it.termId == termId }

    // Calculate term final grades
    val subjectRows = subjects.map { subject ->
        val continuous = termGrades.find { it.subjectId == subject.id && it.gradeType == "مستمر" }?.score
        val finalScore = termGrades.find { it.subjectId == subject.id && it.gradeType == "پایانی" }?.score
        val termScore = termGrades.find { it.subjectId == subject.id && it.gradeType == "نهایی" }?.score
            ?: if (continuous != null && finalScore != null) (continuous + finalScore) / 2.0 else null

        SubjectGradeRowModel(
            subject = subject,
            continuous = continuous,
            finalScore = finalScore,
            termScore = termScore
        )
    }

    val finalScoresList = subjectRows.mapNotNull { it.termScore }
    val overallAverage = if (finalScoresList.isNotEmpty()) finalScoresList.average() else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Term Selector Tabs
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
                                text = "${term.title} ${if (term.isCurrent) "(نوبت جاری)" else ""}",
                                fontWeight = if (selectedTermIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Table Card Container
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            if (subjectRows.isEmpty() || termGrades.isEmpty()) {
                EmptyView(message = "هنوز نمره‌ای برای ${currentTerm?.title ?: "این نوبت"} ثبت نشده است.")
            } else {
                val horizontalScrollState = rememberScrollState()

                Column(modifier = Modifier.fillMaxSize()) {
                    // Scrollable Table container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        Column(modifier = Modifier.width(460.dp)) {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ردیف",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(36.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "عنوان درس",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(150.dp)
                                )
                                Text(
                                    text = "مستمر",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(74.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "پایانی",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(74.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "نمره نهایی",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(84.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                            // Table Content Rows
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                itemsIndexed(subjectRows) { index, row ->
                                    val rowBg = if (index % 2 == 0)
                                        MaterialTheme.colorScheme.surface
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBg)
                                            .padding(vertical = 10.dp, horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = PersianUtils.toPersianDigits(index + 1),
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.width(36.dp),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = row.subject.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.width(150.dp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Box(
                                            modifier = Modifier.width(74.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            GradeScoreBadge(score = row.continuous)
                                        }
                                        Box(
                                            modifier = Modifier.width(74.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            GradeScoreBadge(score = row.finalScore)
                                        }
                                        Box(
                                            modifier = Modifier.width(84.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            GradeScoreBadge(score = row.termScore, large = true)
                                        }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                }
                            }
                        }
                    }

                    // Table Footer / Overall GPA
                    if (overallAverage != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            tonalElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "میانگین کل (معدل ${currentTerm?.title ?: ""}):",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = PersianUtils.formatScore(overallAverage),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class SubjectGradeRowModel(
    val subject: SubjectEntity,
    val continuous: Double?,
    val finalScore: Double?,
    val termScore: Double?
)
