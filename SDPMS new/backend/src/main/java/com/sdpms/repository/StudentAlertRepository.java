package com.sdpms.repository;

import com.sdpms.model.StudentAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentAlertRepository extends JpaRepository<StudentAlert, Long> {

    List<StudentAlert> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<StudentAlert> findByStudentIdAndReadStatus(Long studentId, boolean readStatus);

    long countByStudentIdAndReadStatus(Long studentId, boolean readStatus);
}
