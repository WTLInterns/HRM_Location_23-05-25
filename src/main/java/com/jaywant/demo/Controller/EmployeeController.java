package com.jaywant.demo.Controller;

import com.jaywant.demo.DTO.SalaryDTO;
import com.jaywant.demo.Entity.Attendance;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Repo.AttendanceRepo;
import com.jaywant.demo.Repo.EmployeeRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import com.jaywant.demo.Service.EmployeeService;
import com.jaywant.demo.Service.SalaryService;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {

  @Autowired
  private EmployeeRepo employeeRepository;

  @Autowired
  private AttendanceRepo attendanceRepository;

  @Autowired
  private EmployeeService empService;

  @Autowired
  private SalaryService salaryService;

  // =====================================================
  // Attendance Endpoints
  // =====================================================

  /**
   * Fetch all attendance records for an employee by full name and subadmin ID.
   */
  @GetMapping("/{subadminId}/{fullName}/attendance")
  public ResponseEntity<?> getAttendanceByEmployeeFullName(@PathVariable int subadminId,
      @PathVariable String fullName) {
    Employee employee = employeeRepository.findBySubadminIdAndFullName(subadminId, fullName);
    if (employee == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Employee with full name '" + fullName + "' not found for subadmin ID " + subadminId);
    }
    List<Attendance> attendances = attendanceRepository.findByEmployee(employee);
    return ResponseEntity.ok(attendances);
  }

  /**
   * Add a new attendance record for an employee.
   */
  @PostMapping("/{subadminId}/{fullName}/attendance/add")
  public ResponseEntity<?> addAttendance(@PathVariable int subadminId,
      @PathVariable String fullName,
      @RequestBody Attendance newAttendance) {
    Employee employee = employeeRepository.findBySubadminIdAndFullName(subadminId, fullName);
    if (employee == null) {
      return ResponseEntity.badRequest().body("Employee not found for name: " + fullName);
    }

    newAttendance.setEmployee(employee);
    Attendance saved = attendanceRepository.save(newAttendance);
    return ResponseEntity.ok(saved);
  }

  /**
   * Update an existing attendance record.
   */
  @PutMapping("/{subadminId}/{fullName}/attendance/update/{attendanceId}")
  public ResponseEntity<?> updateAttendance(@PathVariable int subadminId,
      @PathVariable String fullName,
      @PathVariable Long attendanceId,
      @RequestBody Attendance updatedData) {
    Employee employee = employeeRepository.findBySubadminIdAndFullName(subadminId, fullName);
    if (employee == null) {
      return ResponseEntity.badRequest().body("Employee not found for name: " + fullName);
    }

    Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);
    if (attendanceOpt.isEmpty()) {
      return ResponseEntity.badRequest().body("Attendance not found with ID: " + attendanceId);
    }

    Attendance attendance = attendanceOpt.get();
    if (attendance.getEmployee() == null || attendance.getEmployee().getEmpId() != employee.getEmpId()) {
      return ResponseEntity.badRequest().body("Attendance does not belong to the employee: " + fullName);
    }

    attendance.setDate(updatedData.getDate());
    attendance.setStatus(updatedData.getStatus());

    Attendance updated = attendanceRepository.save(attendance);
    return ResponseEntity.ok(updated);
  }

  // =====================================================
  // Employee Update / Delete Endpoints
  // =====================================================

  /**
   * Update employee details.
   */
  @PutMapping("/{subadminId}/update/{empId}")
  public ResponseEntity<?> updateEmployee(@PathVariable int subadminId,
      @PathVariable int empId,
      @RequestBody Employee updatedEmployeeData) {

    Optional<Employee> optionalEmployee = employeeRepository.findById(empId);
    if (optionalEmployee.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Employee not found for ID: " + empId);
    }

    Employee existingEmployee = optionalEmployee.get();

    // Check subadmin ownership
    if (existingEmployee.getSubadmin() == null || existingEmployee.getSubadmin().getId() != subadminId) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Employee does not belong to SubAdmin ID: " + subadminId);
    }

    // Update fields
    existingEmployee.setFirstName(updatedEmployeeData.getFirstName());
    existingEmployee.setLastName(updatedEmployeeData.getLastName());
    existingEmployee.setEmail(updatedEmployeeData.getEmail());
    existingEmployee.setPhone(updatedEmployeeData.getPhone());
    existingEmployee.setAadharNo(updatedEmployeeData.getAadharNo());
    existingEmployee.setPanCard(updatedEmployeeData.getPanCard());
    existingEmployee.setEducation(updatedEmployeeData.getEducation());
    existingEmployee.setBloodGroup(updatedEmployeeData.getBloodGroup());
    existingEmployee.setJobRole(updatedEmployeeData.getJobRole());
    existingEmployee.setGender(updatedEmployeeData.getGender());
    existingEmployee.setAddress(updatedEmployeeData.getAddress());
    existingEmployee.setBirthDate(updatedEmployeeData.getBirthDate());
    existingEmployee.setJoiningDate(updatedEmployeeData.getJoiningDate());
    existingEmployee.setStatus(updatedEmployeeData.getStatus());
    existingEmployee.setBankName(updatedEmployeeData.getBankName());
    existingEmployee.setBankAccountNo(updatedEmployeeData.getBankAccountNo());
    existingEmployee.setBankIfscCode(updatedEmployeeData.getBankIfscCode());
    existingEmployee.setBranchName(updatedEmployeeData.getBranchName());
    existingEmployee.setSalary(updatedEmployeeData.getSalary());

    Employee savedEmployee = employeeRepository.save(existingEmployee);
    return ResponseEntity.ok(savedEmployee);
  }

  /**
   * Delete an employee.
   */
  @DeleteMapping("/{subadminId}/delete/{empId}")
  public ResponseEntity<?> deleteEmployee(@PathVariable int subadminId,
      @PathVariable int empId) {
    Optional<Employee> employeeOpt = employeeRepository.findById(empId);
    if (!employeeOpt.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Employee not found for ID: " + empId);
    }

    Employee employee = employeeOpt.get();
    if (employee.getSubadmin() == null || employee.getSubadmin().getId() != subadminId) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Employee does not belong to SubAdmin ID: " + subadminId);
    }

    employeeRepository.delete(employee);
    return ResponseEntity.ok("Employee deleted successfully.");
  }

  /**
   * Get full name of employee by ID.
   */
  @GetMapping("/{empId}/fullname")
  public ResponseEntity<?> getEmployeeFullName(@PathVariable int empId) {
    Optional<Employee> employeeOpt = employeeRepository.findById(empId);
    if (!employeeOpt.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Employee not found for ID: " + empId);
    }

    Employee employee = employeeOpt.get();
    String fullName = employee.getFirstName() + " " + employee.getLastName();
    return ResponseEntity.ok(fullName);
  }

  /**
   * GET: Generate salary report for an employee based on attendance dates
   * URL: GET
   * /api/subadmin/employee/{companyName}/{employeeFullName}/attendance/report?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd
   */
  @GetMapping("/employee/{companyName}/{employeeFullName}/attendance/report")
  public ResponseEntity<SalaryDTO> generateSalaryReport(
      @PathVariable String companyName,
      @PathVariable String employeeFullName,
      @RequestParam String startDate,
      @RequestParam String endDate) {

    Employee employee = empService.findByEmployeeName(employeeFullName);
    // Here, we assume the employee's company comes from its associated Subadmin's
    // registercompanyname.
    if (employee == null || employee.getSubadmin() == null ||
        !employee.getSubadmin().getRegistercompanyname().equalsIgnoreCase(companyName)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    SalaryDTO report = salaryService.generateSalaryReport(employeeFullName, startDate, endDate);
    return ResponseEntity.ok(report);
  }


  /**
     * GET: Retrieve all employees under a given subadmin.
     * URL: GET /api/employee/{subadminId}/employee/all
     */
    @GetMapping("/{subadminId}/employee/all")
    public ResponseEntity<?> getAllEmployeesForSubadmin(@PathVariable int subadminId) {
        List<Employee> employees = employeeRepository.findBySubadminId(subadminId);
        if (employees == null || employees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No employees found for SubAdmin ID: " + subadminId);
        }
        return ResponseEntity.ok(employees);
    }


}
