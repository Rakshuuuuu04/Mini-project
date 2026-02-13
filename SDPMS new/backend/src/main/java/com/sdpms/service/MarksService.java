package com.sdpms.service;

import com.sdpms.model.StudentMarks;
import com.sdpms.model.Student;
import com.sdpms.model.Subject;
import com.sdpms.repository.StudentMarksRepository;
import com.sdpms.repository.StudentRepository;
import com.sdpms.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MarksService {

    private static final BigDecimal MAX_INTERNAL = BigDecimal.valueOf(40);
    private static final BigDecimal MAX_SEMESTER = BigDecimal.valueOf(60);
    private static final BigDecimal MAX_TOTAL = BigDecimal.valueOf(100);

    private final StudentMarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentAlertService alertService;

    public MarksService(StudentMarksRepository marksRepository, StudentRepository studentRepository,
                        SubjectRepository subjectRepository, StudentAlertService alertService) {
        this.marksRepository = marksRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.alertService = alertService;
    }

    @Transactional
    public StudentMarks saveOrUpdate(Long studentId, Long subjectId, Integer semester, String examType,
                                      BigDecimal internalMarks, BigDecimal semesterMarks) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NoSuchElementException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new NoSuchElementException("Subject not found"));
        Optional<StudentMarks> existing = marksRepository.findByStudentIdAndSubjectIdAndSemesterAndExamType(studentId, subjectId, semester, examType);
        StudentMarks m;
        if (existing.isPresent()) {
            m = existing.get();
        } else {
            m = new StudentMarks();
            m.setStudent(student);
            m.setSubject(subject);
            m.setSemester(semester);
            m.setExamType(examType);
        }
        if (internalMarks != null) m.setInternalMarks(internalMarks);
        if (semesterMarks != null) m.setSemesterMarks(semesterMarks);
        BigDecimal total = (m.getInternalMarks() != null ? m.getInternalMarks() : BigDecimal.ZERO)
                .add(m.getSemesterMarks() != null ? m.getSemesterMarks() : BigDecimal.ZERO);
        m.setTotalMarks(total);
        m.setGrade(GradeCalculator.marksToGrade(total, MAX_TOTAL));
        m.setGradePoint(GradeCalculator.gradeToPoint(m.getGrade()));
        marksRepository.save(m);
        alertService.checkPoorPerformance(studentId, m);
        return m;
    }

    public List<StudentMarks> getByStudent(Long studentId) {
        return marksRepository.findByStudentId(studentId);
    }

    public List<StudentMarks> getByStudentAndSemester(Long studentId, Integer semester) {
        return marksRepository.findByStudentIdAndSemester(studentId, semester);
    }

    public List<StudentMarks> getBySubjectAndSemester(Long subjectId, Integer semester) {
        return marksRepository.findBySubjectIdAndSemester(subjectId, semester);
    }
}
