package com.sdpms.repository;

import com.sdpms.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByDepartmentId(Long departmentId);

    List<Subject> findByDepartmentIdAndSemester(Long departmentId, Integer semester);
}
