package com.sdpms.repository;

import com.sdpms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUsername(String username);

    Optional<Student> findByRollNumber(String rollNumber);

    boolean existsByRollNumber(String rollNumber);

    List<Student> findByDepartmentId(Long departmentId);

    List<Student> findByDepartmentIdAndCurrentSemester(Long departmentId, Integer semester);

    @Query("SELECT s FROM Student s WHERE s.department.id = :deptId AND s.active = true")
    List<Student> findActiveByDepartment(Long deptId);
}
