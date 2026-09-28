package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools LIMIT 1")
    fun getSchool(): Flow<SchoolEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchool(school: SchoolEntity)

    @Query("SELECT * FROM academic_years")
    fun getAcademicYears(): Flow<List<AcademicYearEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcademicYear(year: AcademicYearEntity)

    @Query("SELECT * FROM terms")
    fun getTerms(): Flow<List<TermEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerms(terms: List<TermEntity>)

    @Query("SELECT * FROM shifts LIMIT 1")
    fun getShift(): Flow<ShiftEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: ShiftEntity)
}

@Dao
interface ClassDao {
    @Query("SELECT * FROM classes ORDER BY name ASC")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE id = :id")
    suspend fun getClassById(id: String): ClassEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<ClassEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEntity: ClassEntity)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY lastName ASC, firstName ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: String): StudentEntity?

    @Query("SELECT * FROM students WHERE nationalCode = :nationalCode LIMIT 1")
    suspend fun getStudentByNationalCode(nationalCode: String): StudentEntity?

    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY lastName ASC, firstName ASC")
    fun getStudentsByClass(classId: String): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY lastName ASC")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers WHERE id = :id")
    suspend fun getTeacherById(id: String): TeacherEntity?

    @Query("SELECT * FROM teachers WHERE personnelCode = :code LIMIT 1")
    suspend fun getTeacherByPersonnelCode(code: String): TeacherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeachers(teachers: List<TeacherEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity)
}

@Dao
interface AdminDao {
    @Query("SELECT * FROM admins WHERE username = :username LIMIT 1")
    suspend fun getAdminByUsername(username: String): AdminEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminEntity)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY code ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)
}

@Dao
interface TeacherAssignmentDao {
    @Query("SELECT * FROM teacher_assignments")
    fun getAllAssignments(): Flow<List<TeacherAssignmentEntity>>

    @Query("SELECT * FROM teacher_assignments WHERE teacherId = :teacherId")
    fun getAssignmentsByTeacher(teacherId: String): Flow<List<TeacherAssignmentEntity>>

    @Query("SELECT * FROM teacher_assignments WHERE classId = :classId AND subjectId = :subjectId LIMIT 1")
    suspend fun getAssignment(classId: String, subjectId: String): TeacherAssignmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<TeacherAssignmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: TeacherAssignmentEntity)

    @Delete
    suspend fun deleteAssignment(assignment: TeacherAssignmentEntity)

    @Query("DELETE FROM teacher_assignments WHERE id = :id")
    suspend fun deleteAssignmentById(id: String)
}

@Dao
interface GradeDao {
    @Query("SELECT * FROM grades WHERE studentId = :studentId ORDER BY updatedAt DESC")
    fun getGradesByStudent(studentId: String): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades WHERE studentId = :studentId AND termId = :termId")
    fun getGradesByStudentAndTerm(studentId: String, termId: String): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades WHERE studentId = :studentId AND subjectId = :subjectId AND termId = :termId AND gradeType = :gradeType LIMIT 1")
    suspend fun getSpecificGrade(studentId: String, subjectId: String, termId: String, gradeType: String): GradeEntity?

    @Query("SELECT * FROM grades WHERE classId = :classId AND subjectId = :subjectId AND termId = :termId")
    fun getGradesForClassAndSubject(classId: String, subjectId: String, termId: String): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades ORDER BY updatedAt DESC")
    fun getAllGrades(): Flow<List<GradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrades(grades: List<GradeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: GradeEntity)

    @Update
    suspend fun updateGrade(grade: GradeEntity)

    @Query("DELETE FROM grades WHERE id = :id")
    suspend fun deleteGradeById(id: String)

    @Query("SELECT * FROM grades WHERE isSynced = 0")
    suspend fun getUnsyncedGrades(): List<GradeEntity>
}

@Dao
interface DailyReportDao {
    @Query("SELECT * FROM daily_reports WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getReportsByStudent(studentId: String): Flow<List<DailyReportEntity>>

    @Query("SELECT * FROM daily_reports ORDER BY timestamp DESC LIMIT 50")
    fun getAllRecentReports(): Flow<List<DailyReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: DailyReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<DailyReportEntity>)
}
