package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey val id: String = "sch_1",
    val name: String = "دبیرستان نمونه علامه طباطبایی",
    val code: String = "100234",
    val province: String = "تهران",
    val district: String = "منطقه ۵",
    val phone: String = "02144556677"
)

@Entity(tableName = "academic_years")
data class AcademicYearEntity(
    @PrimaryKey val id: String = "ay_1403_1404",
    val title: String = "سال تحصیلی ۱۴۰۳-۱۴۰۴",
    val isCurrent: Boolean = true
)

@Entity(tableName = "terms")
data class TermEntity(
    @PrimaryKey val id: String,
    val academicYearId: String = "ay_1403_1404",
    val title: String, // نوبت اول / نوبت دوم
    val isCurrent: Boolean = false
)

@Entity(tableName = "shifts")
data class ShiftEntity(
    @PrimaryKey val id: String = "shift_morning",
    val name: String = "نوبت صبح",
    val schoolId: String = "sch_1"
)

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey val id: String,
    val name: String, // مثلاً: نهم الف
    val gradeLevel: Int = 9,
    val schoolId: String = "sch_1",
    val shiftId: String = "shift_morning"
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val unitCount: Int = 3, // ضریب / واحد درس
    val code: String
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val nationalCode: String, // کد ملی (نام کاربری)
    val birthDate: String,    // تاریخ تولد (رمز عبور - مثال: 1388/05/14)
    val firstName: String,
    val lastName: String,
    val studentCode: String,
    val classId: String,
    val fatherName: String
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val id: String,
    val personnelCode: String, // نام کاربری
    val password: String,      // رمز عبور
    val firstName: String,
    val lastName: String,
    val phone: String,
    val specialty: String      // تخصص / دبیر
)

@Entity(tableName = "admins")
data class AdminEntity(
    @PrimaryKey val id: String,
    val username: String,
    val password: String,
    val fullName: String,
    val schoolId: String = "sch_1"
)

@Entity(tableName = "teacher_assignments")
data class TeacherAssignmentEntity(
    @PrimaryKey val id: String,
    val teacherId: String,
    val classId: String,
    val subjectId: String
)

@Entity(tableName = "grades")
data class GradeEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val subjectId: String,
    val classId: String,
    val academicYearId: String = "ay_1403_1404",
    val termId: String, // term_1 یا term_2
    val gradeType: String, // مستمر / پایانی / نهایی
    val score: Double,     // نمره از ۰ تا ۲۰
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String,
    val updatedBy: String,
    val isSynced: Boolean = true
)

@Entity(tableName = "daily_reports")
data class DailyReportEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val subjectId: String,
    val eventType: String, // نمره جدید / ویرایش نمره
    val description: String,
    val previousScore: Double? = null,
    val newScore: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val recordedBy: String
)
