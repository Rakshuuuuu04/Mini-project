package com.sdpms.controller;

import com.sdpms.dto.StudentDTO;
import com.sdpms.model.Attendance;
import com.sdpms.model.StudentMarks;
import com.sdpms.model.Subject;
import com.sdpms.service.AttendanceService;
import com.sdpms.service.MarksService;
import com.sdpms.service.StudentService;
import com.sdpms.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/faculty")
@PreAuthorize("hasRole('FACULTY') or hasRole('ADMIN')")
public class FacultyController {

    private final AttendanceService attendanceService;
    private final MarksService marksService;
    private final StudentService studentService;
    private final SubjectService subjectService;

    public FacultyController(AttendanceService attendanceService, MarksService marksService,
                              StudentService studentService, SubjectService subjectService) {
        this.attendanceService = attendanceService;
        this.marksService = marksService;
        this.studentService = studentService;
        this.subjectService = subjectService;
    }

    @GetMapping("/students")
    public List<StudentDTO> listStudents(@RequestParam Long departmentId, @RequestParam(required = false) Integer semester) {
        if (semester != null) return studentService.listByDepartmentAndSemester(departmentId, semester);
        return studentService.listByDepartment(departmentId);
    }

    @GetMapping("/subjects")
    public List<Subject> listSubjects(@RequestParam Long departmentId, @RequestParam(required = false) Integer semester) {
        if (semester != null) return subjectService.listByDepartmentAndSemester(departmentId, semester);
        return subjectService.listByDepartment(departmentId);
    }

    @PostMapping("/attendance")
    public ResponseEntity<Attendance> markAttendance(@RequestBody Map<String, Object> body) {
        Long studentId = Long.valueOf(body.get("studentId").toString());
        Long subjectId = Long.valueOf(body.get("subjectId").toString());
        LocalDate date = body.get("date") != null ? LocalDate.parse(body.get("date").toString()) : LocalDate.now();
        boolean present = Boolean.TRUE.equals(body.get("present"));
        String remarks = (String) body.get("remarks");
        return ResponseEntity.ok(attendanceService.mark(studentId, subjectId, date, present, remarks));
    }

    @GetMapping("/attendance")
    public List<Attendance> getAttendance(@RequestParam(required = false) Long studentId,
                                           @RequestParam(required = false) Long subjectId,
                                           @RequestParam(required = false) String date) {
        if (studentId != null && subjectId != null)
            return attendanceService.getByStudentAndSubject(studentId, subjectId);
        if (subjectId != null && date != null)
            return attendanceService.getBySubjectAndDate(subjectId, LocalDate.parse(date));
        return List.of();
    }

    @PostMapping("/attendance/bulk")
    public ResponseEntity<Void> markAttendanceBulk(@RequestBody Map<String, Object> body) {
        Long subjectId = Long.valueOf(body.get("subjectId").toString());
        LocalDate date = body.get("date") != null ? LocalDate.parse(body.get("date").toString()) : LocalDate.now();
        @SuppressWarnings("unchecked")
        List<Long> presentIds = (List<Long>) ((List<?>) body.get("presentStudentIds")).stream()
                .map(o -> Long.valueOf(o.toString())).toList();
        attendanceService.markBulk(subjectId, date, presentIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/marks")
    public ResponseEntity<StudentMarks> saveMarks(@RequestBody Map<String, Object> body) {
        Long studentId = Long.valueOf(body.get("studentId").toString());
        Long subjectId = Long.valueOf(body.get("subjectId").toString());
        Integer semester = Integer.valueOf(body.get("semester").toString());
        String examType = (String) body.getOrDefault("examType", "SEMESTER");
        java.math.BigDecimal internal = body.get("internalMarks") != null ? new java.math.BigDecimal(body.get("internalMarks").toString()) : null;
        java.math.BigDecimal semesterMarks = body.get("semesterMarks") != null ? new java.math.BigDecimal(body.get("semesterMarks").toString()) : null;
        return ResponseEntity.ok(marksService.saveOrUpdate(studentId, subjectId, semester, examType, internal, semesterMarks));
    }

    @GetMapping("/marks")
    public List<StudentMarks> getMarks(@RequestParam(required = false) Long studentId,
                                        @RequestParam(required = false) Long subjectId,
                                        @RequestParam(required = false) Integer semester) {
        if (studentId != null && semester != null) return marksService.getByStudentAndSemester(studentId, semester);
        if (subjectId != null && semester != null) return marksService.getBySubjectAndSemester(subjectId, semester);
        if (studentId != null) return marksService.getByStudent(studentId);
        return List.of();
    }
}
