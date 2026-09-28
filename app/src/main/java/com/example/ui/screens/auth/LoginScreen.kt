package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: (role: UserRole, username: String, pass: String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    var selectedRoleIndex by remember { mutableIntStateOf(0) }
    val roles = listOf(UserRole.STUDENT, UserRole.TEACHER, UserRole.ADMIN)
    val currentRole = roles[selectedRoleIndex]

    var usernameInput by remember { mutableStateOf("0012345678") }
    var passwordInput by remember { mutableStateOf("1388/06/15") }
    var passwordVisible by remember { mutableStateOf(false) }

    fun switchRole(index: Int) {
        selectedRoleIndex = index
        when (roles[index]) {
            UserRole.STUDENT -> {
                usernameInput = "0012345678"
                passwordInput = "1388/06/15"
            }
            UserRole.TEACHER -> {
                usernameInput = "10001"
                passwordInput = "teacher123"
            }
            UserRole.ADMIN -> {
                usernameInput = "admin"
                passwordInput = "admin123"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Emblem / Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "نشان سامانه مدرسه",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "سامانه مدیریت نمرات مدرسه",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "دبیرستان نمونه علامه طباطبایی • نسخه V0.1",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Role Tabs
                    PrimaryTabRow(
                        selectedTabIndex = selectedRoleIndex,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(
                            selected = selectedRoleIndex == 0,
                            onClick = { switchRole(0) },
                            text = { Text("دانش‌آموز", fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp)) }
                        )
                        Tab(
                            selected = selectedRoleIndex == 1,
                            onClick = { switchRole(1) },
                            text = { Text("معلم", fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(20.dp)) }
                        )
                        Tab(
                            selected = selectedRoleIndex == 2,
                            onClick = { switchRole(2) },
                            text = { Text("مدیر", fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null, modifier = Modifier.size(20.dp)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Input 1: Username / National Code / Personnel
                    val userLabel = when (currentRole) {
                        UserRole.STUDENT -> "کد ملی دانش‌آموز"
                        UserRole.TEACHER -> "کد پرسنلی دبیر"
                        UserRole.ADMIN -> "نام کاربری مدیر"
                    }
                    val userPlaceholder = when (currentRole) {
                        UserRole.STUDENT -> "مثال: ۰۰۱۲۳۴۵۶۷۸"
                        UserRole.TEACHER -> "مثال: ۱۰۰۰۱"
                        UserRole.ADMIN -> "admin"
                    }

                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text(userLabel) },
                        placeholder = { Text(userPlaceholder) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (currentRole == UserRole.STUDENT) Icons.Default.Badge else Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (currentRole == UserRole.STUDENT || currentRole == UserRole.TEACHER)
                                KeyboardType.Number
                            else
                                KeyboardType.Text
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input 2: Password / Birth Date
                    val passLabel = when (currentRole) {
                        UserRole.STUDENT -> "تاریخ تولد دانش‌آموز"
                        UserRole.TEACHER -> "رمز عبور"
                        UserRole.ADMIN -> "رمز عبور مدیر"
                    }
                    val passPlaceholder = when (currentRole) {
                        UserRole.STUDENT -> "۱۳۸۸/۰۶/۱۵ یا ۱۳۸۸۰۶۱۵"
                        UserRole.TEACHER -> "رمز ورود"
                        UserRole.ADMIN -> "رمز عبور"
                    }

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text(passLabel) },
                        placeholder = { Text(passPlaceholder) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (currentRole == UserRole.STUDENT) Icons.Default.DateRange else Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "مخفی کردن" else "نمایش"
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible || currentRole == UserRole.STUDENT)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (currentRole == UserRole.STUDENT) KeyboardType.Text else KeyboardType.Password
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    if (currentRole == UserRole.STUDENT) {
                        Text(
                            text = "رمز عبور دانش‌آموزان به صورت پیش‌فرض تاریخ تولد هجری شمسی است.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                        )
                    }

                    // Error Alert
                    AnimatedVisibility(visible = !errorMessage.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEE2E2))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF991B1B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            onLoginClick(currentRole, usernameInput, passwordInput)
                        },
                        enabled = !isLoading && usernameInput.isNotBlank() && passwordInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "ورود به سامانه",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Demo Helpers Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ورود سریع تستی (یک کلیک):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                switchRole(0)
                                usernameInput = "0012345678"
                                passwordInput = "1388/06/15"
                                onLoginClick(UserRole.STUDENT, "0012345678", "1388/06/15")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("دانش‌آموز", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = {
                                switchRole(1)
                                usernameInput = "10001"
                                passwordInput = "teacher123"
                                onLoginClick(UserRole.TEACHER, "10001", "teacher123")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("دبیر ریاضی", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = {
                                switchRole(2)
                                usernameInput = "admin"
                                passwordInput = "admin123"
                                onLoginClick(UserRole.ADMIN, "admin", "admin123")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("مدیر مدرسه", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
