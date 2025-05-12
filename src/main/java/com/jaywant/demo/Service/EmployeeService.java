package com.jaywant.demo.Service;

import com.jaywant.demo.Entity.Attendance;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.AttendanceRepo;
import com.jaywant.demo.Repo.EmployeeRepo;
import com.jaywant.demo.Repo.SubAdminRepo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

  @Autowired
  private EmployeeRepo employeeRepo;

  @Autowired
  private SubAdminRepo subAdminRepo;

  @Autowired
  private AttendanceRepo attendanceRepo;

  /**
   * Finds an employee by full name (concatenated firstName and lastName).
   */
  public Employee findByEmployeeName(String fullName) {
    return employeeRepo.findByFullName(fullName);
  }

  public Optional<Employee> findByEmail(String email) {
    return employeeRepo.findByEmail(email);
  }

  public Employee findBySubadminIdAndFullName(int subadminId, String employeeFullName) {
    return employeeRepo.findBySubadminIdAndFullName(subadminId, employeeFullName);
  }

  public List<Attendance> addAttendance(int subAdminId, String fullName, List<Attendance> attendances) {
    Employee emp = employeeRepo.findByFullName(fullName);
    Subadmin subadmin = subAdminRepo.findById(subAdminId).orElse(null);

    if (emp == null || subadmin == null) {
      throw new RuntimeException("Employee or Subadmin not found");
    }

    for (Attendance attendance : attendances) {
      attendance.setEmployee(emp);
    }

    return attendanceRepo.saveAll(attendances);
  }

  public void updatePassword(int empId, String newPassword) {
    Optional<Employee> opt = employeeRepo.findById(empId);
    if (!opt.isPresent()) {
      throw new RuntimeException("Employee not found with id: " + empId);
    }
    Employee emp = opt.get();
    // direct assignment, no encoding
    emp.setPassword(newPassword);
    employeeRepo.save(emp);
  }

}
