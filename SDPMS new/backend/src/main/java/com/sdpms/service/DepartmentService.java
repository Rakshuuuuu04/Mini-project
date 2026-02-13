package com.sdpms.service;

import com.sdpms.dto.DepartmentDTO;
import com.sdpms.model.Department;
import com.sdpms.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public Department create(Department department) {
        if (departmentRepository.existsByName(department.getName()))
            throw new IllegalArgumentException("Department name already exists");
        return departmentRepository.save(department);
    }

    @Transactional
    public Department update(Long id, Department updates) {
        Department d = departmentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Department not found"));
        if (updates.getName() != null) d.setName(updates.getName());
        if (updates.getCode() != null) d.setCode(updates.getCode());
        return departmentRepository.save(d);
    }

    public Department getById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Department not found"));
    }

    public List<DepartmentDTO> listAll() {
        return departmentRepository.findAll().stream().map(DepartmentDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) throw new NoSuchElementException("Department not found");
        departmentRepository.deleteById(id);
    }
}
