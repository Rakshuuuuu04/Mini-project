package com.sdpms.dto;

import java.math.BigDecimal;
import java.util.List;

public class StudentProgressDTO {

    private Long studentId;
    private String fullName;
    private String rollNumber;
    private String departmentName;
    private Integer currentSemester;
    private Double attendancePercentage;
    private List<SubjectMarksDTO> marksBySubject;
    private BigDecimal sgpa;
    private List<AlertDTO> alerts;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Integer getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(Integer currentSemester) { this.currentSemester = currentSemester; }
    public Double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(Double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    public List<SubjectMarksDTO> getMarksBySubject() { return marksBySubject; }
    public void setMarksBySubject(List<SubjectMarksDTO> marksBySubject) { this.marksBySubject = marksBySubject; }
    public BigDecimal getSgpa() { return sgpa; }
    public void setSgpa(BigDecimal sgpa) { this.sgpa = sgpa; }
    public List<AlertDTO> getAlerts() { return alerts; }
    public void setAlerts(List<AlertDTO> alerts) { this.alerts = alerts; }
}
