package com.example.data.local

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedDatabase(database: AppDatabase) = withContext(Dispatchers.IO) {
        val schoolDao = database.schoolDao()
        val classDao = database.classDao()
        val subjectDao = database.subjectDao()
        val teacherDao = database.teacherDao()
        val adminDao = database.adminDao()
        val assignmentDao = database.teacherAssignmentDao()
        val studentDao = database.studentDao()
        val gradeDao = database.gradeDao()
        val dailyReportDao = database.dailyReportDao()

        // 1. School
        val school = SchoolEntity(
            id = "sch_1",
            name = "دبیرستان نمونه علامه طباطبایی",
            code = "100234",
            province = "تهران",
            district = "منطقه ۵ آموزش و پرورش",
            phone = "02144882200"
        )
        schoolDao.insertSchool(school)

        // 2. Academic Year
        val academicYear = AcademicYearEntity(
            id = "ay_1403_1404",
            title = "سال تحصیلی ۱۴۰۳-۱۴۰۴",
            isCurrent = true
        )
        schoolDao.insertAcademicYear(academicYear)

        // 3. Terms
        val terms = listOf(
            TermEntity(id = "term_1", academicYearId = "ay_1403_1404", title = "نوبت اول", isCurrent = false),
            TermEntity(id = "term_2", academicYearId = "ay_1403_1404", title = "نوبت دوم", isCurrent = true)
        )
        schoolDao.insertTerms(terms)

        // 4. Shift
        val shift = ShiftEntity(
            id = "shift_morning",
            name = "نوبت صبح",
            schoolId = "sch_1"
        )
        schoolDao.insertShift(shift)

        // 5. Classes
        val classes = listOf(
            ClassEntity(id = "cls_9a", name = "کلاس نهم الف", gradeLevel = 9, schoolId = "sch_1", shiftId = "shift_morning"),
            ClassEntity(id = "cls_9b", name = "کلاس نهم ب", gradeLevel = 9, schoolId = "sch_1", shiftId = "shift_morning"),
            ClassEntity(id = "cls_9c", name = "کلاس نهم ج", gradeLevel = 9, schoolId = "sch_1", shiftId = "shift_morning")
        )
        classDao.insertClasses(classes)

        // 6. 13 Subjects
        val subjects = listOf(
            SubjectEntity(id = "sub_math", name = "ریاضیات", unitCount = 4, code = "101"),
            SubjectEntity(id = "sub_science", name = "علوم تجربی", unitCount = 3, code = "102"),
            SubjectEntity(id = "sub_literature", name = "زبان و ادبیات فارسی", unitCount = 4, code = "103"),
            SubjectEntity(id = "sub_writing", name = "نگارش و انشا", unitCount = 2, code = "104"),
            SubjectEntity(id = "sub_english", name = "زبان انگلیسی", unitCount = 2, code = "105"),
            SubjectEntity(id = "sub_arabic", name = "زبان عربی", unitCount = 2, code = "106"),
            SubjectEntity(id = "sub_heaven_messages", name = "پیام‌های آسمان", unitCount = 2, code = "107"),
            SubjectEntity(id = "sub_quran", name = "آموزش قرآن کریم", unitCount = 2, code = "108"),
            SubjectEntity(id = "sub_social", name = "مطالعات اجتماعی", unitCount = 3, code = "109"),
            SubjectEntity(id = "sub_art", name = "فرهنگ و هنر", unitCount = 2, code = "110"),
            SubjectEntity(id = "sub_work_tech", name = "کار و فناوری", unitCount = 2, code = "111"),
            SubjectEntity(id = "sub_defense", name = "آمادگی دفاعی", unitCount = 2, code = "112"),
            SubjectEntity(id = "sub_pe", name = "تربیت بدنی و سلامت", unitCount = 2, code = "113")
        )
        subjectDao.insertSubjects(subjects)

        // 7. Teachers
        val teachers = listOf(
            TeacherEntity(
                id = "tch_1",
                personnelCode = "10001",
                password = "teacher123",
                firstName = "محسن",
                lastName = "احمدی",
                phone = "09121112233",
                specialty = "دبیر ریاضی"
            ),
            TeacherEntity(
                id = "tch_2",
                personnelCode = "10002",
                password = "teacher123",
                firstName = "رضا",
                lastName = "محمدی",
                phone = "09122223344",
                specialty = "دبیر علوم تجربی"
            ),
            TeacherEntity(
                id = "tch_3",
                personnelCode = "10003",
                password = "teacher123",
                firstName = "علیرضا",
                lastName = "حسینی",
                phone = "09123334455",
                specialty = "دبیر ادبیات و نگارش فارسی"
            ),
            TeacherEntity(
                id = "tch_4",
                personnelCode = "10004",
                password = "teacher123",
                firstName = "فرهاد",
                lastName = "صادقی",
                phone = "09124445566",
                specialty = "دبیر زبان انگلیسی"
            ),
            TeacherEntity(
                id = "tch_5",
                personnelCode = "10005",
                password = "teacher123",
                firstName = "مهدی",
                lastName = "کریمی",
                phone = "09125556677",
                specialty = "دبیر مطالعات اجتماعی و معارف"
            )
        )
        teacherDao.insertTeachers(teachers)

        // 8. Admin
        val admin = AdminEntity(
            id = "adm_1",
            username = "admin",
            password = "admin123",
            fullName = "جناب آقای سلطانی (مدیریت مدرسه)",
            schoolId = "sch_1"
        )
        adminDao.insertAdmin(admin)

        // 9. Teacher Assignments (Teacher + Class + Subject)
        val assignments = listOf(
            TeacherAssignmentEntity(id = "asg_1", teacherId = "tch_1", classId = "cls_9a", subjectId = "sub_math"),
            TeacherAssignmentEntity(id = "asg_2", teacherId = "tch_1", classId = "cls_9b", subjectId = "sub_math"),
            TeacherAssignmentEntity(id = "asg_3", teacherId = "tch_1", classId = "cls_9c", subjectId = "sub_math"),
            TeacherAssignmentEntity(id = "asg_4", teacherId = "tch_2", classId = "cls_9a", subjectId = "sub_science"),
            TeacherAssignmentEntity(id = "asg_5", teacherId = "tch_2", classId = "cls_9b", subjectId = "sub_science"),
            TeacherAssignmentEntity(id = "asg_6", teacherId = "tch_3", classId = "cls_9a", subjectId = "sub_literature"),
            TeacherAssignmentEntity(id = "asg_7", teacherId = "tch_3", classId = "cls_9a", subjectId = "sub_writing"),
            TeacherAssignmentEntity(id = "asg_8", teacherId = "tch_4", classId = "cls_9a", subjectId = "sub_english"),
            TeacherAssignmentEntity(id = "asg_9", teacherId = "tch_4", classId = "cls_9b", subjectId = "sub_english"),
            TeacherAssignmentEntity(id = "asg_10", teacherId = "tch_5", classId = "cls_9a", subjectId = "sub_social"),
            TeacherAssignmentEntity(id = "asg_11", teacherId = "tch_5", classId = "cls_9a", subjectId = "sub_arabic"),
            TeacherAssignmentEntity(id = "asg_12", teacherId = "tch_5", classId = "cls_9a", subjectId = "sub_heaven_messages")
        )
        assignmentDao.insertAssignments(assignments)

        // 10. Students: Primary Student + ~400 Students
        val firstNames = listOf("علی", "محمد", "حسین", "امیررضا", "مهدی", "پارسا", "سینا", "آرمان", "عرفان", "پوریا", "طاها", "کیان", "امیرعلی", "سامان", "دانیال", "نوید", "بردیا", "سهیل", "امیرمهدی", "سروش")
        val lastNames = listOf("رضایی", "محمدی", "کریمی", "احمدی", "حسینی", "صادقی", "موسوی", "جعفری", "کاظمی", "ابراهیمی", "نجفی", "رحیمی", "باقری", "مطهری", "رستمی", "فتحی", "مرادی", "سلیمانی", "دهقان", "یوسفی")
        val fatherNames = listOf("محمد", "رضا", "حسین", "علیرضا", "محمود", "اکبر", "مهدی", "حمید", "مجید", "جواد")

        val studentList = mutableListOf<StudentEntity>()
        // Primary demo student:
        val primaryStudent = StudentEntity(
            id = "std_1",
            nationalCode = "0012345678",
            birthDate = "1388/06/15",
            firstName = "علی",
            lastName = "رضایی",
            studentCode = "90101",
            classId = "cls_9a",
            fatherName = "محمد"
        )
        studentList.add(primaryStudent)

        // Generate ~400 total students distributed across cls_9a, cls_9b, cls_9c
        val classIds = listOf("cls_9a", "cls_9b", "cls_9c")
        for (i in 2..400) {
            val fn = firstNames[(i * 7) % firstNames.size]
            val ln = lastNames[(i * 11) % lastNames.size]
            val fath = fatherNames[(i * 3) % fatherNames.size]
            val cls = classIds[(i - 1) % classIds.size]
            val natCode = String.format("%010d", 1000000000L + i)
            val month = ((i % 12) + 1).let { if (it < 10) "0$it" else "$it" }
            val day = ((i % 28) + 1).let { if (it < 10) "0$it" else "$it" }
            val birth = "1388/$month/$day"
            val stCode = "90" + String.format("%03d", i)

            studentList.add(
                StudentEntity(
                    id = "std_$i",
                    nationalCode = natCode,
                    birthDate = birth,
                    firstName = fn,
                    lastName = ln,
                    studentCode = stCode,
                    classId = cls,
                    fatherName = fath
                )
            )
        }
        studentDao.insertStudents(studentList)

        // 11. Seed Grades for primary student (Ali Rezaei) across both terms
        val gradesList = mutableListOf<GradeEntity>()

        // Term 1 & Term 2 grades for Ali Rezaei across 13 subjects
        // Subject scores data: Pair(continuousScore, finalScore)
        val term1Scores = mapOf(
            "sub_math" to Pair(18.0, 17.5),
            "sub_science" to Pair(19.0, 18.5),
            "sub_literature" to Pair(20.0, 19.5),
            "sub_writing" to Pair(19.5, 19.0),
            "sub_english" to Pair(17.5, 17.0),
            "sub_arabic" to Pair(18.5, 18.0),
            "sub_heaven_messages" to Pair(20.0, 20.0),
            "sub_quran" to Pair(20.0, 20.0),
            "sub_social" to Pair(18.0, 18.5),
            "sub_art" to Pair(19.0, 19.5),
            "sub_work_tech" to Pair(19.0, 19.0),
            "sub_defense" to Pair(18.5, 18.0),
            "sub_pe" to Pair(20.0, 20.0)
        )

        val term2Scores = mapOf(
            "sub_math" to Pair(19.0, 18.5),
            "sub_science" to Pair(19.5, 19.0),
            "sub_literature" to Pair(20.0, 20.0),
            "sub_writing" to Pair(20.0, 19.5),
            "sub_english" to Pair(18.5, 18.0),
            "sub_arabic" to Pair(19.0, 19.0),
            "sub_heaven_messages" to Pair(20.0, 20.0),
            "sub_quran" to Pair(20.0, 20.0),
            "sub_social" to Pair(19.0, 19.0),
            "sub_art" to Pair(20.0, 20.0),
            "sub_work_tech" to Pair(19.5, 19.5),
            "sub_defense" to Pair(19.0, 18.5),
            "sub_pe" to Pair(20.0, 20.0)
        )

        val now = System.currentTimeMillis()
        val oneDay = 86_400_000L

        // Term 1
        term1Scores.forEach { (subId, pair) ->
            val cont = pair.first
            val fin = pair.second
            val termFinal = (cont + fin) / 2.0

            gradesList.add(
                GradeEntity(
                    id = "grd_t1_${subId}_c",
                    studentId = "std_1",
                    subjectId = subId,
                    classId = "cls_9a",
                    academicYearId = "ay_1403_1404",
                    termId = "term_1",
                    gradeType = "مستمر",
                    score = cont,
                    createdAt = now - (60 * oneDay),
                    updatedAt = now - (60 * oneDay),
                    createdBy = "معلم مربوطه",
                    updatedBy = "معلم مربوطه"
                )
            )
            gradesList.add(
                GradeEntity(
                    id = "grd_t1_${subId}_f",
                    studentId = "std_1",
                    subjectId = subId,
                    classId = "cls_9a",
                    academicYearId = "ay_1403_1404",
                    termId = "term_1",
                    gradeType = "پایانی",
                    score = fin,
                    createdAt = now - (45 * oneDay),
                    updatedAt = now - (45 * oneDay),
                    createdBy = "معلم مربوطه",
                    updatedBy = "معلم مربوطه"
                )
            )
            gradesList.add(
                GradeEntity(
                    id = "grd_t1_${subId}_t",
                    studentId = "std_1",
                    subjectId = subId,
                    classId = "cls_9a",
                    academicYearId = "ay_1403_1404",
                    termId = "term_1",
                    gradeType = "نهایی",
                    score = termFinal,
                    createdAt = now - (40 * oneDay),
                    updatedAt = now - (40 * oneDay),
                    createdBy = "معلم مربوطه",
                    updatedBy = "معلم مربوطه"
                )
            )
        }

        // Term 2
        term2Scores.forEach { (subId, pair) ->
            val cont = pair.first
            val fin = pair.second
            val termFinal = (cont + fin) / 2.0

            gradesList.add(
                GradeEntity(
                    id = "grd_t2_${subId}_c",
                    studentId = "std_1",
                    subjectId = subId,
                    classId = "cls_9a",
                    academicYearId = "ay_1403_1404",
                    termId = "term_2",
                    gradeType = "مستمر",
                    score = cont,
                    createdAt = now - (15 * oneDay),
                    updatedAt = now - (15 * oneDay),
                    createdBy = "معلم مربوطه",
                    updatedBy = "معلم مربوطه"
                )
            )
            gradesList.add(
                GradeEntity(
                    id = "grd_t2_${subId}_f",
                    studentId = "std_1",
                    subjectId = subId,
                    classId = "cls_9a",
                    academicYearId = "ay_1403_1404",
                    termId = "term_2",
                    gradeType = "پایانی",
                    score = fin,
                    createdAt = now - (2 * oneDay),
                    updatedAt = now - (2 * oneDay),
                    createdBy = "معلم مربوطه",
                    updatedBy = "معلم مربوطه"
                )
            )
            gradesList.add(
                GradeEntity(
                    id = "grd_t2_${subId}_t",
                    studentId = "std_1",
                    subjectId = subId,
                    classId = "cls_9a",
                    academicYearId = "ay_1403_1404",
                    termId = "term_2",
                    gradeType = "نهایی",
                    score = termFinal,
                    createdAt = now - (1 * oneDay),
                    updatedAt = now - (1 * oneDay),
                    createdBy = "معلم مربوطه",
                    updatedBy = "معلم مربوطه"
                )
            )
        }

        // Also seed sample grades for the rest of students in class 9a so the teacher panel shows full lists
        for (i in 2..20) {
            val sId = "std_$i"
            val scoreMath = 14.0 + (i % 7)
            gradesList.add(
                GradeEntity(
                    id = "grd_t2_${sId}_math_c",
                    studentId = sId,
                    subjectId = "sub_math",
                    classId = "cls_9a",
                    academicYearId = "ay_1403_1404",
                    termId = "term_2",
                    gradeType = "مستمر",
                    score = scoreMath,
                    createdAt = now - (5 * oneDay),
                    updatedAt = now - (5 * oneDay),
                    createdBy = "محسن احمدی",
                    updatedBy = "محسن احمدی"
                )
            )
        }

        gradeDao.insertGrades(gradesList)

        // 12. Daily Reports (History of changes)
        val dailyReports = listOf(
            DailyReportEntity(
                id = "rep_1",
                studentId = "std_1",
                subjectId = "sub_math",
                eventType = "نمره جدید",
                description = "نمره مستمر نوبت دوم ریاضی ثبت شد: ۱۹",
                previousScore = null,
                newScore = 19.0,
                timestamp = now - (15 * oneDay),
                recordedBy = "محسن احمدی (دبیر ریاضی)"
            ),
            DailyReportEntity(
                id = "rep_2",
                studentId = "std_1",
                subjectId = "sub_science",
                eventType = "نمره جدید",
                description = "نمره مستمر علوم تجربی ثبت شد: ۱۹.۵",
                previousScore = null,
                newScore = 19.5,
                timestamp = now - (10 * oneDay),
                recordedBy = "رضا محمدی (دبیر علوم)"
            ),
            DailyReportEntity(
                id = "rep_3",
                studentId = "std_1",
                subjectId = "sub_english",
                eventType = "ویرایش نمره",
                description = "نمره پایانی زبان انگلیسی از ۱۷ به ۱۸ تغییر یافت.",
                previousScore = 17.0,
                newScore = 18.0,
                timestamp = now - (2 * oneDay) - 7200000,
                recordedBy = "فرهاد صادقی (دبیر زبان)"
            ),
            DailyReportEntity(
                id = "rep_4",
                studentId = "std_1",
                subjectId = "sub_math",
                eventType = "نمره جدید",
                description = "نمره پایانی نوبت دوم ریاضی ثبت شد: ۱۸.۵",
                previousScore = null,
                newScore = 18.5,
                timestamp = now - (2 * oneDay),
                recordedBy = "محسن احمدی (دبیر ریاضی)"
            ),
            DailyReportEntity(
                id = "rep_5",
                studentId = "std_1",
                subjectId = "sub_literature",
                eventType = "نمره جدید",
                description = "نمره پایانی نوبت دوم ادبیات فارسی ثبت شد: ۲۰",
                previousScore = null,
                newScore = 20.0,
                timestamp = now - 3600000,
                recordedBy = "علیرضا حسینی (دبیر ادبیات)"
            )
        )
        dailyReportDao.insertReports(dailyReports)
    }
}
