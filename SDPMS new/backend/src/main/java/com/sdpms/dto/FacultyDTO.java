package com.sdpms.dto;

import com.sdpms.model.Faculty;
import java.time.LocalDateTime;

public class FacultyDTO {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String employeeId;
    private Long departmentId;
    private String departmentName;
    private boolean active;
    private LocalDateTime createdAt;

    public static FacultyDTO fromEntity(Faculty f) {
        FacultyDTO dto = new FacultyDTO();
        dto.setId(f.getId());
        dto.setUsername(f.getUsername());
        dto.setFullName(f.getFullName());
        dto.setEmail(f.getEmail());
        dto.setPhone(f.getPhone());
        dto.setEmployeeId(f.getEmployeeId());
        if (f.getDepartment() != null) {
            dto.setDepartmentId(f.getDepartment().getId());
            dto.setDepartmentName(f.getDepartment().getName());
        }
        dto.setActive(f.isActive());
        dto.setCreatedAt(f.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
