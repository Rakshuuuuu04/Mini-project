package com.sdpms.service;

import com.sdpms.model.StudentAlert;
import com.sdpms.model.StudentMarks;
import com.sdpms.repository.StudentAlertRepository;
import com.sdpms.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StudentAlertService {

    private final StudentAlertRepository alertRepository;
    private final StudentRepository studentRepository;

    public StudentAlertService(StudentAlertRepository alertRepository, StudentRepository studentRepository) {
        this.alertRepository = alertRepository;
        this.studentRepository = studentRepository;
    }

    public List<StudentAlert> getByStudent(Long studentId) {
        return alertRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    public long getUnreadCount(Long studentId) {
        return alertRepository.countByStudentIdAndReadStatus(studentId, false);
    }

    @Transactional
    public void markAsRead(Long alertId, Long studentId) {
        StudentAlert a = alertRepository.findById(alertId).orElseThrow(() -> new NoSuchElementException("Alert not found"));
        if (!a.getStudent().getId().equals(studentId)) throw new IllegalArgumentException("Alert does not belong to student");
        a.setReadStatus(true);
        alertRepository.save(a);
    }

    @Transactional
    public void markAllAsRead(Long studentId) {
        List<StudentAlert> list = alertRepository.findByStudentIdAndReadStatus(studentId, false);
        list.forEach(a -> a.setReadStatus(true));
        alertRepository.saveAll(list);
    }

    @Transactional
    public void checkPoorPerformance(Long studentId, StudentMarks marks) {
        if (marks.getGrade() == null || !marks.getGrade().equals("F")) return;
        var s = studentRepository.findById(studentId);
        if (s.isEmpty()) return;
        StudentAlert alert = new StudentAlert();
        alert.setStudent(s.get());
        alert.setType("POOR_PERFORMANCE");
        alert.setTitle("Performance Alert");
        alert.setMessage("You have received F grade in " + marks.getSubject().getName() + ". Please improve.");
        alertRepository.save(alert);
    }
}
