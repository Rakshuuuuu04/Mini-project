package com.sdpms.service;

import com.sdpms.dto.FacultyDTO;
import com.sdpms.model.Department;
import com.sdpms.model.Faculty;
import com.sdpms.model.Role;
import com.sdpms.repository.DepartmentRepository;
import com.sdpms.repository.FacultyRepository;
import com.sdpms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public FacultyService(FacultyRepository facultyRepository, DepartmentRepository departmentRepository,
                           UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Faculty create(Faculty faculty, String plainPassword) {
        if (userRepository.existsByUsername(faculty.getUsername()))
            throw new IllegalArgumentException("Username already exists");
        if (faculty.getEmployeeId() != null && facultyRepository.existsByEmployeeId(faculty.getEmployeeId()))
            throw new IllegalArgumentException("Employee ID already exists");
        faculty.setPasswordHash(passwordEncoder.encode(plainPassword));
        faculty.setRole(Role.FACULTY);
        return facultyRepository.save(faculty);
    }

    @Transactional
    public Faculty update(Long id, Faculty updates) {
        Faculty f = facultyRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Faculty not found"));
        if (updates.getFullName() != null) f.setFullName(updates.getFullName());
        if (updates.getEmail() != null) f.setEmail(updates.getEmail());
        if (updates.getPhone() != null) f.setPhone(updates.getPhone());
        if (updates.getDepartment() != null) f.setDepartment(updates.getDepartment());
        if (updates.isActive() != f.isActive()) f.setActive(updates.isActive());
        return facultyRepository.save(f);
    }

    public Optional<Faculty> getById(Long id) {
        return facultyRepository.findById(id);
    }

    public List<FacultyDTO> listAll() {
        return facultyRepository.findAll().stream().map(FacultyDTO::fromEntity).collect(Collectors.toList());
    }

    public List<FacultyDTO> listByDepartment(Long departmentId) {
        return facultyRepository.findByDepartmentId(departmentId).stream().map(FacultyDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (!facultyRepository.existsById(id)) throw new NoSuchElementException("Faculty not found");
        facultyRepository.deleteById(id);
    }
}
