package com.sdpms.dto;

import java.math.BigDecimal;

public class SubjectMarksDTO {

    private String subjectName;
    private String subjectCode;
    private Integer semester;
    private BigDecimal internalMarks;
    private BigDecimal semesterMarks;
    private BigDecimal totalMarks;
    private String grade;
    private BigDecimal gradePoint;

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }
    public BigDecimal getInternalMarks() { return internalMarks; }
    public void setInternalMarks(BigDecimal internalMarks) { this.internalMarks = internalMarks; }
    public BigDecimal getSemesterMarks() { return semesterMarks; }
    public void setSemesterMarks(BigDecimal semesterMarks) { this.semesterMarks = semesterMarks; }
    public BigDecimal getTotalMarks() { return totalMarks; }
    public void setTotalMarks(BigDecimal totalMarks) { this.totalMarks = totalMarks; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public BigDecimal getGradePoint() { return gradePoint; }
    public void setGradePoint(BigDecimal gradePoint) { this.gradePoint = gradePoint; }
}
