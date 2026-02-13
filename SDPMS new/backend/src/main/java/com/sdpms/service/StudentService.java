package com.sdpms.service;

import com.sdpms.dto.StudentDTO;
import com.sdpms.dto.StudentProgressDTO;
import com.sdpms.dto.SubjectMarksDTO;
import com.sdpms.dto.AlertDTO;
import com.sdpms.model.*;
import com.sdpms.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttendanceRepository attendanceRepository;
    private final StudentMarksRepository marksRepository;
    private final StudentAlertRepository alertRepository;
    private final SubjectRepository subjectRepository;

    public StudentService(StudentRepository studentRepository, DepartmentRepository departmentRepository,
                          UserRepository userRepository, PasswordEncoder passwordEncoder,
                          AttendanceRepository attendanceRepository, StudentMarksRepository marksRepository,
                          StudentAlertRepository alertRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
        this.alertRepository = alertRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public Student create(Student student, String plainPassword) {
        if (userRepository.existsByUsername(student.getUsername()))
            throw new IllegalArgumentException("Username already exists");
        if (student.getRollNumber() != null && studentRepository.existsByRollNumber(student.getRollNumber()))
            throw new IllegalArgumentException("Roll number already exists");
        student.setPasswordHash(passwordEncoder.encode(plainPassword));
        student.setRole(Role.STUDENT);
        return studentRepository.save(student);
    }

    @Transactional
    public Student update(Long id, Student updates) {
        Student s = studentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Student not found"));
        if (updates.getFullName() != null) s.setFullName(updates.getFullName());
        if (updates.getEmail() != null) s.setEmail(updates.getEmail());
        if (updates.getPhone() != null) s.setPhone(updates.getPhone());
        if (updates.getCurrentSemester() != null) s.setCurrentSemester(updates.getCurrentSemester());
        if (updates.getAcademicYear() != null) s.setAcademicYear(updates.getAcademicYear());
        if (updates.getDepartment() != null) s.setDepartment(updates.getDepartment());
        if (updates.isActive() != s.isActive()) s.setActive(updates.isActive());
        return studentRepository.save(s);
    }

    public Optional<Student> getById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public List<StudentDTO> listAll() {
        return studentRepository.findAll().stream().map(StudentDTO::fromEntity).collect(Collectors.toList());
    }

    public List<StudentDTO> listByDepartment(Long departmentId) {
        return studentRepository.findByDepartmentId(departmentId).stream().map(StudentDTO::fromEntity).collect(Collectors.toList());
    }

    public List<StudentDTO> listByDepartmentAndSemester(Long departmentId, Integer semester) {
        return studentRepository.findByDepartmentIdAndCurrentSemester(departmentId, semester)
                .stream().map(StudentDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) throw new NoSuchElementException("Student not found");
        studentRepository.deleteById(id);
    }

    public double getAttendancePercentage(Long studentId, Long subjectId) {
        List<Attendance> list = attendanceRepository.findAllByStudentAndSubject(studentId, subjectId);
        if (list.isEmpty()) return 0;
        long present = list.stream().filter(Attendance::isPresent).count();
        return (present * 100.0) / list.size();
    }

    public double getOverallAttendancePercentage(Long studentId) {
        List<Attendance> all = attendanceRepository.findByStudentId(studentId);
        if (all.isEmpty()) return 0;
        long present = all.stream().filter(Attendance::isPresent).count();
        return (present * 100.0) / all.size();
    }

    @Transactional(readOnly = true)
    public StudentProgressDTO getProgress(Long studentId) {
        Student s = studentRepository.findById(studentId).orElseThrow(() -> new NoSuchElementException("Student not found"));
        StudentProgressDTO dto = new StudentProgressDTO();
        dto.setStudentId(s.getId());
        dto.setFullName(s.getFullName());
        dto.setRollNumber(s.getRollNumber());
        if (s.getDepartment() != null) dto.setDepartmentName(s.getDepartment().getName());
        dto.setCurrentSemester(s.getCurrentSemester());

        List<Attendance> allAtt = new ArrayList<>();
        if (s.getDepartment() != null) {
            for (Subject sub : subjectRepository.findByDepartmentIdAndSemester(s.getDepartment().getId(), s.getCurrentSemester())) {
                allAtt.addAll(attendanceRepository.findAllByStudentAndSubject(s.getId(), sub.getId()));
            }
        }
        if (!allAtt.isEmpty()) {
            long present = allAtt.stream().filter(Attendance::isPresent).count();
            dto.setAttendancePercentage(Math.round((present * 100.0) / allAtt.size() * 100.0) / 100.0);
        } else {
            dto.setAttendancePercentage(0.0);
        }

        List<StudentMarks> marksList = marksRepository.findByStudentIdAndSemester(studentId, s.getCurrentSemester());
        List<SubjectMarksDTO> marksDTOs = new ArrayList<>();
        BigDecimal totalPoints = BigDecimal.ZERO;
        int totalCredits = 0;
        for (StudentMarks m : marksList) {
            SubjectMarksDTO sm = new SubjectMarksDTO();
            sm.setSubjectName(m.getSubject().getName());
            sm.setSubjectCode(m.getSubject().getCode());
            sm.setSemester(m.getSemester());
            sm.setInternalMarks(m.getInternalMarks());
            sm.setSemesterMarks(m.getSemesterMarks());
            sm.setTotalMarks(m.getTotalMarks());
            sm.setGrade(m.getGrade());
            sm.setGradePoint(m.getGradePoint());
            marksDTOs.add(sm);
            if (m.getSubject().getCredits() != null && m.getGradePoint() != null) {
                totalPoints = totalPoints.add(m.getGradePoint().multiply(BigDecimal.valueOf(m.getSubject().getCredits())));
                totalCredits += m.getSubject().getCredits();
            }
        }
        dto.setMarksBySubject(marksDTOs);
        if (totalCredits > 0) {
            dto.setSgpa(totalPoints.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP));
        }

        List<StudentAlert> alerts = alertRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        dto.setAlerts(alerts.stream().map(a -> {
            AlertDTO ad = new AlertDTO();
            ad.setId(a.getId());
            ad.setType(a.getType());
            ad.setTitle(a.getTitle());
            ad.setMessage(a.getMessage());
            ad.setReadStatus(a.isReadStatus());
            ad.setCreatedAt(a.getCreatedAt());
            return ad;
        }).collect(Collectors.toList()));

        return dto;
    }
}
