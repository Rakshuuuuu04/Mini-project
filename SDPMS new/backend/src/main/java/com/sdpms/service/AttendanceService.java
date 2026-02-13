package com.sdpms.service;

import com.sdpms.model.Attendance;
import com.sdpms.model.Student;
import com.sdpms.model.StudentAlert;
import com.sdpms.model.Subject;
import com.sdpms.repository.AttendanceRepository;
import com.sdpms.repository.StudentAlertRepository;
import com.sdpms.repository.StudentRepository;
import com.sdpms.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AttendanceService {

    private static final double LOW_ATTENDANCE_THRESHOLD = 75.0;

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentAlertRepository alertRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, StudentRepository studentRepository,
                              SubjectRepository subjectRepository, StudentAlertRepository alertRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.alertRepository = alertRepository;
    }

    @Transactional
    public Attendance mark(Long studentId, Long subjectId, LocalDate date, boolean present, String remarks) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NoSuchElementException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new NoSuchElementException("Subject not found"));
        Optional<Attendance> existing = attendanceRepository.findByStudentIdAndSubjectIdAndAttendanceDate(studentId, subjectId, date);
        Attendance a;
        if (existing.isPresent()) {
            a = existing.get();
            a.setPresent(present);
            if (remarks != null) a.setRemarks(remarks);
        } else {
            a = new Attendance();
            a.setStudent(student);
            a.setSubject(subject);
            a.setAttendanceDate(date);
            a.setPresent(present);
            a.setRemarks(remarks);
            attendanceRepository.save(a);
        }
        checkAndCreateLowAttendanceAlert(studentId, subjectId);
        return a;
    }

    @Transactional
    public void markBulk(Long subjectId, LocalDate date, List<Long> presentStudentIds) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new NoSuchElementException("Subject not found"));
        List<Attendance> existing = attendanceRepository.findBySubjectIdAndAttendanceDate(subjectId, date);
        for (Attendance a : existing) {
            a.setPresent(presentStudentIds.contains(a.getStudent().getId()));
        }
        attendanceRepository.saveAll(existing);
        for (Attendance a : existing) {
            checkAndCreateLowAttendanceAlert(a.getStudent().getId(), subjectId);
        }
    }

    public List<Attendance> getByStudentAndSubject(Long studentId, Long subjectId) {
        return attendanceRepository.findAllByStudentAndSubject(studentId, subjectId);
    }

    public List<Attendance> getBySubjectAndDate(Long subjectId, LocalDate date) {
        return attendanceRepository.findBySubjectIdAndAttendanceDate(subjectId, date);
    }

    public double getAttendancePercentage(Long studentId, Long subjectId) {
        List<Attendance> list = attendanceRepository.findAllByStudentAndSubject(studentId, subjectId);
        if (list.isEmpty()) return 0;
        long present = list.stream().filter(Attendance::isPresent).count();
        return (present * 100.0) / list.size();
    }

    private void checkAndCreateLowAttendanceAlert(Long studentId, Long subjectId) {
        List<Attendance> list = attendanceRepository.findAllByStudentAndSubject(studentId, subjectId);
        if (list.size() < 5) return;
        long present = list.stream().filter(Attendance::isPresent).count();
        double pct = (present * 100.0) / list.size();
        if (pct < LOW_ATTENDANCE_THRESHOLD) {
            Student s = studentRepository.findById(studentId).orElse(null);
            Subject sub = subjectRepository.findById(subjectId).orElse(null);
            if (s == null || sub == null) return;
            long existing = alertRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                    .filter(al -> "LOW_ATTENDANCE".equals(al.getType()) && al.getMessage() != null && al.getMessage().contains(sub.getName()))
                    .count();
            if (existing > 0) return;
            StudentAlert alert = new StudentAlert();
            alert.setStudent(s);
            alert.setType("LOW_ATTENDANCE");
            alert.setTitle("Low Attendance Alert");
            alert.setMessage(String.format("Your attendance in %s is %.1f%%. Minimum required is %.0f%%.", sub.getName(), pct, LOW_ATTENDANCE_THRESHOLD));
            alertRepository.save(alert);
        }
    }
}
