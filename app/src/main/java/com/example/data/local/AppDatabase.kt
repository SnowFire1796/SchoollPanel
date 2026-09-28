package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AdminDao
import com.example.data.local.dao.ClassDao
import com.example.data.local.dao.DailyReportDao
import com.example.data.local.dao.GradeDao
import com.example.data.local.dao.SchoolDao
import com.example.data.local.dao.StudentDao
import com.example.data.local.dao.SubjectDao
import com.example.data.local.dao.TeacherAssignmentDao
import com.example.data.local.dao.TeacherDao
import com.example.data.local.entity.AcademicYearEntity
import com.example.data.local.entity.AdminEntity
import com.example.data.local.entity.ClassEntity
import com.example.data.local.entity.DailyReportEntity
import com.example.data.local.entity.GradeEntity
import com.example.data.local.entity.SchoolEntity
import com.example.data.local.entity.ShiftEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TeacherAssignmentEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.TermEntity

@Database(
    entities = [
        SchoolEntity::class,
        AcademicYearEntity::class,
        TermEntity::class,
        ShiftEntity::class,
        ClassEntity::class,
        SubjectEntity::class,
        StudentEntity::class,
        TeacherEntity::class,
        AdminEntity::class,
        TeacherAssignmentEntity::class,
        GradeEntity::class,
        DailyReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
    abstract fun classDao(): ClassDao
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun adminDao(): AdminDao
    abstract fun subjectDao(): SubjectDao
    abstract fun teacherAssignmentDao(): TeacherAssignmentDao
    abstract fun gradeDao(): GradeDao
    abstract fun dailyReportDao(): DailyReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "school_panel_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
