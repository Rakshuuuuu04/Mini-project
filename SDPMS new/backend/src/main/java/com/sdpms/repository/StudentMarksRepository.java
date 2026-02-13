package com.sdpms.repository;

import com.sdpms.model.StudentMarks;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentMarksRepository extends JpaRepository<StudentMarks, Long> {

    List<StudentMarks> findByStudentId(Long studentId);

    List<StudentMarks> findByStudentIdAndSemester(Long studentId, Integer semester);

    Optional<StudentMarks> findByStudentIdAndSubjectIdAndSemesterAndExamType(
            Long studentId, Long subjectId, Integer semester, String examType);

    List<StudentMarks> findBySubjectIdAndSemester(Long subjectId, Integer semester);
}
