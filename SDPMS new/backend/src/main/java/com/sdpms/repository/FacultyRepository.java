package com.sdpms.repository;

import com.sdpms.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByUsername(String username);

    List<Faculty> findByDepartmentId(Long departmentId);

    boolean existsByEmployeeId(String employeeId);
}
