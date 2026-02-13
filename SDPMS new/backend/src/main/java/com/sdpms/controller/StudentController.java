package com.sdpms.controller;

import com.sdpms.dto.AlertDTO;
import com.sdpms.dto.StudentProgressDTO;
import com.sdpms.model.StudentAlert;
import com.sdpms.service.StudentAlertService;
import com.sdpms.service.StudentService;
import com.sdpms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;
    private final UserService userService;
    private final StudentAlertService alertService;

    public StudentController(StudentService studentService, UserService userService, StudentAlertService alertService) {
        this.studentService = studentService;
        this.userService = userService;
        this.alertService = alertService;
    }

    @GetMapping("/progress")
    public ResponseEntity<StudentProgressDTO> getProgress(Authentication auth) {
        var student = userService.findStudentByUsername(auth.getName()).orElse(null);
        if (student == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(studentService.getProgress(student.getId()));
    }

    @GetMapping("/attendance-percentage")
    public ResponseEntity<Double> getAttendancePercentage(Authentication auth,
                                                           @RequestParam(required = false) Long subjectId) {
        var student = userService.findStudentByUsername(auth.getName()).orElse(null);
        if (student == null) return ResponseEntity.notFound().build();
        double pct = subjectId != null
                ? studentService.getAttendancePercentage(student.getId(), subjectId)
                : studentService.getOverallAttendancePercentage(student.getId());
        return ResponseEntity.ok(pct);
    }

    @GetMapping("/alerts")
    public List<AlertDTO> getAlerts(Authentication auth) {
        var student = userService.findStudentByUsername(auth.getName()).orElse(null);
        if (student == null) return List.of();
        return alertService.getByStudent(student.getId()).stream().map(a -> {
            AlertDTO dto = new AlertDTO();
            dto.setId(a.getId());
            dto.setType(a.getType());
            dto.setTitle(a.getTitle());
            dto.setMessage(a.getMessage());
            dto.setReadStatus(a.isReadStatus());
            dto.setCreatedAt(a.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/alerts/unread-count")
    public long getUnreadAlertCount(Authentication auth) {
        var student = userService.findStudentByUsername(auth.getName()).orElse(null);
        if (student == null) return 0;
        return alertService.getUnreadCount(student.getId());
    }

    @PutMapping("/alerts/{id}/read")
    public ResponseEntity<Void> markAlertRead(Authentication auth, @PathVariable Long id) {
        var student = userService.findStudentByUsername(auth.getName()).orElse(null);
        if (student == null) return ResponseEntity.notFound().build();
        alertService.markAsRead(id, student.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/alerts/read-all")
    public ResponseEntity<Void> markAllAlertsRead(Authentication auth) {
        var student = userService.findStudentByUsername(auth.getName()).orElse(null);
        if (student == null) return ResponseEntity.notFound().build();
        alertService.markAllAsRead(student.getId());
        return ResponseEntity.ok().build();
    }
}
