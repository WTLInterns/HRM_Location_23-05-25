package com.jaywant.demo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jaywant.demo.Entity.Attendance;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.AttendanceRepo;
import com.jaywant.demo.Repo.EmployeeRepo;
import com.jaywant.demo.Repo.SubAdminRepo;

@Service
public class AttendanceService {

  @Autowired
  private EmployeeRepo employeeRepo;

  @Autowired
  private SubAdminRepo subAdminRepo;

  @Autowired
  private AttendanceRepo attendanceRepo;

  

  public List<Attendance> addAttendance(int subAdminId, String fullName, List<Attendance> attendances) {
    Employee emp = employeeRepo.findByFullName(fullName);

    Subadmin subadmin = subAdminRepo.findById(subAdminId).orElse(null);

    if (emp == null || subadmin == null) {
      throw new RuntimeException("Employee or Subadmin not found");
    }

    // Assign the Employee object to each Attendance record
    for (Attendance attendance : attendances) {
      attendance.setEmployee(emp); // 👈 Correct way: pass the whole Employee object
    }

    // Save all attendance records
    return attendanceRepo.saveAll(attendances);
  }

  // 🔁 Bulk update attendance
  public List<Attendance> updateAttendance(int subAdminId, String fullName, List<Attendance> updatedAttendances) {
    Employee emp = employeeRepo.findByFullName(fullName);
    Subadmin subadmin = subAdminRepo.findById(subAdminId).orElse(null);

    if (emp == null || subadmin == null) {
      throw new RuntimeException("Employee or Subadmin not found");
    }

    for (Attendance attendance : updatedAttendances) {
      // Optional: Add validation to ensure the attendance belongs to this employee
      attendance.setEmployee(emp); // Rebind employee object
    }

    return attendanceRepo.saveAll(updatedAttendances);
  }


  

}
