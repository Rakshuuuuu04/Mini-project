package com.sdpms.service;

import com.sdpms.model.Department;
import com.sdpms.model.Subject;
import com.sdpms.repository.DepartmentRepository;
import com.sdpms.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;

    public SubjectService(SubjectRepository subjectRepository, DepartmentRepository departmentRepository) {
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public Subject create(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject update(Long id, Subject updates) {
        Subject s = subjectRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Subject not found"));
        if (updates.getName() != null) s.setName(updates.getName());
        if (updates.getCode() != null) s.setCode(updates.getCode());
        if (updates.getSemester() != null) s.setSemester(updates.getSemester());
        if (updates.getCredits() != null) s.setCredits(updates.getCredits());
        if (updates.getDepartment() != null) s.setDepartment(updates.getDepartment());
        return subjectRepository.save(s);
    }

    public Subject getById(Long id) {
        return subjectRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Subject not found"));
    }

    public List<Subject> listByDepartment(Long departmentId) {
        return subjectRepository.findByDepartmentId(departmentId);
    }

    public List<Subject> listByDepartmentAndSemester(Long departmentId, Integer semester) {
        return subjectRepository.findByDepartmentIdAndSemester(departmentId, semester);
    }

    @Transactional
    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) throw new NoSuchElementException("Subject not found");
        subjectRepository.deleteById(id);
    }
}
