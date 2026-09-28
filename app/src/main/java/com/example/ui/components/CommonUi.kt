package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SyncState
import com.example.data.repository.SyncStatus
import com.example.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolTopBar(
    title: String,
    subtitle: String? = null,
    syncState: SyncState? = null,
    onSyncClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 3.dp
    ) {
        Column {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    navigationIcon?.invoke()
                },
                actions = {
                    if (onSyncClick != null && syncState != null) {
                        IconButton(
                            onClick = onSyncClick,
                            modifier = Modifier.testTag("sync_button")
                        ) {
                            when (syncState.status) {
                                SyncStatus.SYNCING -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                }
                                SyncStatus.ONLINE_SUCCESS -> {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = "همگام‌سازی شده",
                                        tint = Color(0xFF86EFAC)
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.CloudOff,
                                        contentDescription = "حالت آفلاین محلی",
                                        tint = Color(0xFFFDE047)
                                    )
                                }
                            }
                        }
                    }

                    if (onSettingsClick != null) {
                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier.testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "تنظیمات و سرور",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    if (onLogoutClick != null) {
                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "خروج از حساب",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            // Offline or Sync notice banner
            if (syncState != null) {
                AnimatedVisibility(visible = syncState.status == SyncStatus.OFFLINE_MODE || syncState.status == SyncStatus.SYNCING) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (syncState.status == SyncStatus.SYNCING)
                                    MaterialTheme.colorScheme.secondary
                                else
                                    Color(0xFFB45309)
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (syncState.status == SyncStatus.SYNCING) Icons.Default.Refresh else Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = syncState.message,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GradeScoreBadge(
    score: Double?,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    if (score == null) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = if (large) 12.dp else 8.dp, vertical = if (large) 6.dp else 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "—",
                style = if (large) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val (bg, fg) = when {
        score >= 18.0 -> Pair(Color(0xFFDCFCE7), Color(0xFF166534)) // green
        score >= 15.0 -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1)) // blue
        score >= 10.0 -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309)) // amber
        else -> Pair(Color(0xFFFEE2E2), Color(0xFF991B1B))          // red
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = if (large) 12.dp else 8.dp, vertical = if (large) 6.dp else 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = PersianUtils.formatScore(score),
            style = if (large) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

@Composable
fun EmptyView(
    message: String = "هنوز نمره‌ای ثبت نشده.",
    icon: ImageVector = Icons.Default.School,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
