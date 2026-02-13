package com.sdpms.controller;

import com.sdpms.dto.DepartmentDTO;
import com.sdpms.dto.FacultyDTO;
import com.sdpms.dto.StudentDTO;
import com.sdpms.model.*;
import com.sdpms.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DepartmentService departmentService;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final SubjectService subjectService;

    public AdminController(DepartmentService departmentService, StudentService studentService,
                           FacultyService facultyService, SubjectService subjectService) {
        this.departmentService = departmentService;
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.subjectService = subjectService;
    }

    // ---------- Departments ----------
    @GetMapping("/departments")
    public List<DepartmentDTO> listDepartments() {
        return departmentService.listAll();
    }

    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(department));
    }

    @PutMapping("/departments/{id}")
    public Department updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        return departmentService.update(id, department);
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Students ----------
    @GetMapping("/students")
    public List<StudentDTO> listStudents(@RequestParam(required = false) Long departmentId) {
        if (departmentId != null) return studentService.listByDepartment(departmentId);
        return studentService.listAll();
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable Long id) {
        return studentService.getById(id)
                .map(StudentDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody Map<String, Object> body) {
        Student s = mapToStudent(body);
        String password = (String) body.get("password");
        if (password == null || password.isBlank()) return ResponseEntity.badRequest().build();
        Department dept = null;
        if (body.get("departmentId") != null) {
            dept = departmentService.getById(Long.valueOf(body.get("departmentId").toString()));
        }
        s.setDepartment(dept);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(s, password));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Student s = studentService.getById(id).orElse(null);
        if (s == null) return ResponseEntity.notFound().build();
        if (body.get("fullName") != null) s.setFullName((String) body.get("fullName"));
        if (body.get("email") != null) s.setEmail((String) body.get("email"));
        if (body.get("phone") != null) s.setPhone((String) body.get("phone"));
        if (body.get("currentSemester") != null) s.setCurrentSemester(Integer.valueOf(body.get("currentSemester").toString()));
        if (body.get("academicYear") != null) s.setAcademicYear((String) body.get("academicYear"));
        if (body.get("departmentId") != null) {
            s.setDepartment(departmentService.getById(Long.valueOf(body.get("departmentId").toString())));
        }
        if (body.get("active") != null) s.setActive(Boolean.TRUE.equals(body.get("active")));
        return ResponseEntity.ok(studentService.update(id, s));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Faculty ----------
    @GetMapping("/faculty")
    public List<FacultyDTO> listFaculty(@RequestParam(required = false) Long departmentId) {
        if (departmentId != null) return facultyService.listByDepartment(departmentId);
        return facultyService.listAll();
    }

    @PostMapping("/faculty")
    public ResponseEntity<Faculty> createFaculty(@RequestBody Map<String, Object> body) {
        Faculty f = mapToFaculty(body);
        String password = (String) body.get("password");
        if (password == null || password.isBlank()) return ResponseEntity.badRequest().build();
        if (body.get("departmentId") != null) {
            f.setDepartment(departmentService.getById(Long.valueOf(body.get("departmentId").toString())));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(facultyService.create(f, password));
    }

    @PutMapping("/faculty/{id}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Faculty f = facultyService.getById(id).orElse(null);
        if (f == null) return ResponseEntity.notFound().build();
        if (body.get("fullName") != null) f.setFullName((String) body.get("fullName"));
        if (body.get("email") != null) f.setEmail((String) body.get("email"));
        if (body.get("phone") != null) f.setPhone((String) body.get("phone"));
        if (body.get("departmentId") != null) {
            f.setDepartment(departmentService.getById(Long.valueOf(body.get("departmentId").toString())));
        }
        if (body.get("active") != null) f.setActive(Boolean.TRUE.equals(body.get("active")));
        return ResponseEntity.ok(facultyService.update(id, f));
    }

    @DeleteMapping("/faculty/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Subjects ----------
    @GetMapping("/subjects")
    public List<Subject> listSubjects(@RequestParam(required = false) Long departmentId,
                                       @RequestParam(required = false) Integer semester) {
        if (departmentId != null) {
            if (semester != null) return subjectService.listByDepartmentAndSemester(departmentId, semester);
            return subjectService.listByDepartment(departmentId);
        }
        return List.of();
    }

    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody Map<String, Object> body) {
        Subject subject = new Subject();
        subject.setName((String) body.get("name"));
        subject.setCode((String) body.get("code"));
        if (body.get("semester") != null) subject.setSemester(Integer.valueOf(body.get("semester").toString()));
        if (body.get("credits") != null) subject.setCredits(Integer.valueOf(body.get("credits").toString()));
        if (body.get("departmentId") != null) {
            subject.setDepartment(departmentService.getById(Long.valueOf(body.get("departmentId").toString())));
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(subject));
    }

    @PutMapping("/subjects/{id}")
    public Subject updateSubject(@PathVariable Long id, @RequestBody Subject subject) {
        if (subject.getDepartment() != null && subject.getDepartment().getId() != null) {
            subject.setDepartment(departmentService.getById(subject.getDepartment().getId()));
        }
        return subjectService.update(id, subject);
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Student mapToStudent(Map<String, Object> body) {
        Student s = new Student();
        s.setUsername((String) body.get("username"));
        s.setFullName((String) body.get("fullName"));
        s.setEmail((String) body.get("email"));
        s.setPhone((String) body.get("phone"));
        s.setRollNumber((String) body.get("rollNumber"));
        if (body.get("currentSemester") != null) s.setCurrentSemester(Integer.valueOf(body.get("currentSemester").toString()));
        s.setAcademicYear((String) body.get("academicYear"));
        return s;
    }

    private Faculty mapToFaculty(Map<String, Object> body) {
        Faculty f = new Faculty();
        f.setUsername((String) body.get("username"));
        f.setFullName((String) body.get("fullName"));
        f.setEmail((String) body.get("email"));
        f.setPhone((String) body.get("phone"));
        f.setEmployeeId((String) body.get("employeeId"));
        return f;
    }
}
