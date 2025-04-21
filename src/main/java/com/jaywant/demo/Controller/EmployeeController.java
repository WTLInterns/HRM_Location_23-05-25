package com.jaywant.demo.Controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.jaywant.demo.DTO.SalaryDTO;
import com.jaywant.demo.Entity.Attendance;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.AttendanceRepo;
import com.jaywant.demo.Repo.EmployeeRepo;
import com.jaywant.demo.Repo.SubAdminRepo;
import com.jaywant.demo.Service.AttendanceService;
import com.jaywant.demo.Service.EmployeeService;
import com.jaywant.demo.Service.SalaryService;
import com.jaywant.demo.Service.SubAdminService;

@RestController
@RequestMapping("/api/employee")
// @CrossOrigin(origins = "*")
public class EmployeeController {

  @Autowired
  private EmployeeRepo employeeRepository;

  @Autowired
  private AttendanceRepo attendanceRepository;

  @Autowired
  private AttendanceService attendanceService;

  @Autowired
  private EmployeeService empService;

  @Autowired
  private SalaryService salaryService;

  @Autowired
  private SubAdminRepo subAdminRepo;

  @Autowired
  private SubAdminService subAdminService;

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

  // @PostMapping("/{subAdminId}/{fullName}/attendance/add")
  // public List<Attendance> addAttendances(@PathVariable int subAdminId,
  // @PathVariable String fullName,
  // @RequestBody List<Attendance> attendances) {
  // return this.attendanceService.addAttendance(subAdminId, fullName,
  // attendances);
  // }

  /**
   * Update Employee by fullName (with optional new images).
   */
  @PutMapping(value = "/update-employee/{subadminId}/{fullName:.+}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> updateEmployeeByFullName(
      @PathVariable int subadminId,
      @PathVariable String fullName,

      // scalar fields
      @RequestParam String firstName,
      @RequestParam String lastName,
      @RequestParam String email,
      @RequestParam Long phone,
      @RequestParam String aadharNo,
      @RequestParam String panCard,
      @RequestParam String education,
      @RequestParam String bloodGroup,
      @RequestParam String jobRole,
      @RequestParam String gender,
      @RequestParam String address,
      @RequestParam String birthDate,
      @RequestParam String joiningDate,
      @RequestParam String status,
      @RequestParam String bankName,
      @RequestParam String bankAccountNo,
      @RequestParam String bankIfscCode,
      @RequestParam String branchName,
      @RequestParam Long salary,

      // image parts
      @RequestPart(required = false) MultipartFile empimg,
      @RequestPart(required = false) MultipartFile adharimg,
      @RequestPart(required = false) MultipartFile panimg) {
    // 1) find the employee by subadmin + fullName
    Employee existing = employeeRepository.findBySubadminIdAndFullName(subadminId, fullName);
    if (existing == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("No employee named '" + fullName + "' under SubAdmin " + subadminId);
    }

    try {
      // 2) delegate to your service using the empId you just found
      Employee updated = subAdminService.updateEmployee(
          subadminId,
          existing.getEmpId(), // <-- use the real ID
          firstName, lastName, email, phone,
          aadharNo, panCard, education, bloodGroup,
          jobRole, gender, address,
          birthDate, joiningDate, status,
          bankName, bankAccountNo, bankIfscCode,
          branchName, salary,
          empimg, adharimg, panimg);
      return ResponseEntity.ok(updated);

    } catch (RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error updating employee: " + ex.getMessage());
    }
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
    // Check if the employee exists and its associated subadmin's company name
    // matches.
    if (employee == null || employee.getSubadmin() == null ||
        !employee.getSubadmin().getRegistercompanyname().equalsIgnoreCase(companyName)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    SalaryDTO report = salaryService.generateSalaryReport(employee, startDate, endDate);
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

  // @PutMapping("/{subAdminId}/{fullName}/attendance/update/bulk")
  // public ResponseEntity<?> updateBulkAttendance(@PathVariable int subAdminId,
  // @PathVariable String fullName,
  // @RequestBody List<Attendance> attendances) {
  // try {
  // List<Attendance> updated = attendanceService.updateAttendance(subAdminId,
  // fullName, attendances);
  // return ResponseEntity.ok(updated);
  // } catch (RuntimeException e) {
  // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  // }
  // }

  // @PostMapping("/{subAdminId}/{fullName}/attendance/add/bulk")
  // public ResponseEntity<?> addAttendances(@PathVariable int subAdminId,
  // @PathVariable String fullName,
  // @RequestBody List<Attendance> attendances) {
  // try {
  // List<Attendance> saved = attendanceService.addAttendance(subAdminId,
  // fullName, attendances);
  // return ResponseEntity.ok(saved);
  // } catch (RuntimeException e) {
  // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  // }
  // }

  /**
   * Bulk update attendance endpoint.
   * URL: POST /api/employee/{subAdminId}/{fullName}/attendance/update/bulk
   */
  @PutMapping("/{subAdminId}/{fullName}/attendance/update/bulk")
  public ResponseEntity<?> updateBulkAttendance(
      @PathVariable int subAdminId,
      @PathVariable String fullName,
      @RequestBody List<Attendance> attendances) {
    try {
      List<Attendance> updated = attendanceService.updateAttendance(subAdminId, fullName, attendances);
      return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  /**
   * Bulk add attendance endpoint.
   * URL: POST /api/employee/{subAdminId}/{fullName}/attendance/add/bulk
   */
  @PostMapping("/{subAdminId}/{fullName}/attendance/add/bulk")
  public ResponseEntity<?> addAttendances(
      @PathVariable int subAdminId,
      @PathVariable String fullName,
      @RequestBody List<Attendance> attendances) {
    try {
      List<Attendance> saved = attendanceService.addAttendance(subAdminId, fullName, attendances);
      return ResponseEntity.ok(saved);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/bulk/{fullName}/{date}")
  public List<Attendance> getAttendancesBullk(@PathVariable String fullName, @PathVariable String date) {

    return this.attendanceService.getAttendanceByBul(fullName, date);

  }

}
