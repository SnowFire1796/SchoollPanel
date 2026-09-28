package com.example.ui.screens.settings

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
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.repository.SyncState
import com.example.data.repository.SyncStatus
import com.example.data.repository.UserSession
import com.example.util.PersianUtils
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    currentBaseUrl: String,
    onSaveBaseUrl: (String) -> Unit,
    syncState: SyncState,
    onTriggerSync: () -> Unit,
    onResetDatabase: () -> Unit,
    session: UserSession,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var urlInput by remember { mutableStateOf(currentBaseUrl) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "اطلاعات حساب کاربری",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "نام کاربر: ${session.fullName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "نقش: ${session.role.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "نام کاربری / کد: ${PersianUtils.toPersianDigits(session.username)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_logout_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("خروج از حساب کاربری")
                    }
                }
            }

            // Sync Status & Controls Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "وضعیت همگام‌سازی (Offline-First)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "برنامه به گونه‌ای طراحی شده که نمرات دریافت شده را به شکل پایدار در حافظه محلی دستگاه (Room Database) ذخیره کند تا حتی در صورت قطع اینترنت همیشه در دسترس باشند.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (syncState.status) {
                                    SyncStatus.ONLINE_SUCCESS -> Color(0xFFDCFCE7)
                                    SyncStatus.SYNCING -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color(0xFFFEF3C7)
                                }
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "وضعیت: " + when (syncState.status) {
                                    SyncStatus.ONLINE_SUCCESS -> "متصل و همگام با سرور"
                                    SyncStatus.SYNCING -> "در حال برقراری ارتباط با سرور..."
                                    else -> "فعال در حالت محلی آفلاین"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = when (syncState.status) {
                                    SyncStatus.ONLINE_SUCCESS -> Color(0xFF166534)
                                    SyncStatus.SYNCING -> MaterialTheme.colorScheme.primary
                                    else -> Color(0xFFB45309)
                                }
                            )
                            if (syncState.lastSyncTime != null) {
                                Text(
                                    text = "آخرین همگام‌سازی: ${PersianUtils.formatDate(syncState.lastSyncTime)} ساعت ${PersianUtils.formatTime(syncState.lastSyncTime)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onTriggerSync,
                        enabled = syncState.status != SyncStatus.SYNCING,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_sync_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (syncState.status == SyncStatus.SYNCING) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("در حال همگام‌سازی...")
                        } else {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("همگام‌سازی دستی با سرور")
                        }
                    }
                }
            }

            // Backend Base URL Configuration (Section 5)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تنظیم آدرس سرور (Base URL)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "آدرس پایه API سرور جهت اتصال به Backend. در محیط توسعه یا سرور ابری می‌توانید این آدرس را سفارشی کنید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("Base URL API") },
                        placeholder = { Text("https://api.schoolpanel.ir/api/v1/") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("base_url_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onSaveBaseUrl(urlInput)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("آدرس سرور با موفقیت ذخیره شد.")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_base_url_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ذخیره آدرس")
                        }

                        OutlinedButton(
                            onClick = {
                                urlInput = "https://api.schoolpanel.ir/api/v1/"
                                onSaveBaseUrl("https://api.schoolpanel.ir/api/v1/")
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("آدرس پیش‌فرض بازنشانی شد.")
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("پیش‌فرض")
                        }
                    }
                }
            }

            // Reset & Seed Database for Evaluators
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مدیریت داده‌های دیتابیس محلی",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "با فشردن این دکمه، دیتابیس Room با ۴۰۰ دانش‌آموز، ۵ معلم، ۱۳ درس و نمرات اولیه مجدداً مقداردهی اولیه خواهد شد.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            onResetDatabase()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("اطلاعات اولیه با موفقیت بازنشانی گردید.")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reset_db_btn")
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("بارگذاری مجدد داده‌های اولیه مدرسه (Reset & Seed)")
                    }
                }
            }

            // About App Section (درباره برنامه)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("about_app_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "درباره برنامه",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "SchoolPanel",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "v 0.1.0",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ساخته شده توسط ابوالفضل نورآذری",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "©2026 All rights reserved",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Build IDE: SP-AF-2026-v010",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
