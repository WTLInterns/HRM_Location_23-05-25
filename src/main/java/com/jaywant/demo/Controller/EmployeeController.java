package com.jaywant.demo.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaywant.demo.DTO.SalaryDTO;
import com.jaywant.demo.Entity.Attendance;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Repo.AttendanceRepo;
import com.jaywant.demo.Repo.EmployeeRepo;
import com.jaywant.demo.Repo.SubAdminRepo;
import com.jaywant.demo.Service.AttendanceService;
import com.jaywant.demo.Service.EmployeeEmailService;
import com.jaywant.demo.Service.EmployeePasswordResetService;
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
      @RequestParam String department,
      @RequestParam String password,

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
      Employee updated;
      updated = subAdminService.updateEmployee(
          subadminId,
          existing.getEmpId(), // <-- use the real ID
          firstName, lastName, email, phone,
          aadharNo, panCard, education, bloodGroup,
          jobRole, gender, address,
          birthDate, joiningDate, status,
          bankName, bankAccountNo, bankIfscCode,
          branchName, salary, password,
          empimg, adharimg, panimg, department);
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
    if (employees == null) {
      return ResponseEntity.ok(new java.util.ArrayList<>());
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
  // @PutMapping("/{subAdminId}/{fullName}/attendance/update/bulk")
  // public ResponseEntity<?> updateBulkAttendance(
  // @PathVariable int subAdminId,
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

  /**
   * Bulk add attendance endpoint.
   * URL: POST /api/employee/{subAdminId}/{fullName}/attendance/add/bulk
   */
  // @PostMapping("/{subAdminId}/{fullName}/attendance/add/bulk")
  // public ResponseEntity<?> addAttendances(
  // @PathVariable int subAdminId,
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

  @GetMapping("/bulk/{fullName}/{date}")
  public List<Attendance> getAttendancesBullk(@PathVariable String fullName, @PathVariable String date) {

    return this.attendanceService.getAttendanceByBul(fullName, date);

  }

  // // At top of class:
  // private static final List<String> REQUIRES_REASON = List.of("absent",
  // "paidleave");

  // private boolean requiresReason(String status) {
  // if (status == null) return false;
  // String normalized = status.trim().toLowerCase().replaceAll("[ _]", "");
  // return REQUIRES_REASON.contains(normalized);
  // }

  // /**
  // * Bulk-add or single-add attendance endpoint.
  // * URL: POST /api/employee/{subadminId}/{fullName}/attendance/add/bulk
  // */
  // @PostMapping("/{subadminId}/{fullName}/attendance/add/bulk")
  // public ResponseEntity<?> addOrUpdateAttendances(
  // @PathVariable int subadminId,
  // @PathVariable String fullName,
  // @RequestBody JsonNode payload) {

  // try {
  // ObjectMapper mapper = new ObjectMapper()
  // .registerModule(new JavaTimeModule())
  // .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  // List<Attendance> attendances;
  // if (payload.isArray()) {
  // attendances = Arrays.asList(mapper.treeToValue(payload, Attendance[].class));
  // } else {
  // Attendance single = mapper.treeToValue(payload, Attendance.class);
  // attendances = Collections.singletonList(single);
  // }
  // return processBatch(attendances, subadminId, fullName);

  // } catch (JsonProcessingException e) {
  // return ResponseEntity
  // .badRequest()
  // .body("Invalid Attendance JSON payload: " + e.getOriginalMessage());
  // }
  // }

  // /**
  // * Bulk-update or single-update attendance endpoint.
  // * URL: PUT /api/employee/{subadminId}/{fullName}/attendance/update/bulk
  // */
  // @PutMapping("/{subadminId}/{fullName}/attendance/update/bulk")
  // public ResponseEntity<?> updateOrAddAttendances(
  // @PathVariable int subadminId,
  // @PathVariable String fullName,
  // @RequestBody JsonNode payload) {
  // // exactly the same body as addOrUpdate; reuse it:
  // return addOrUpdateAttendances(subadminId, fullName, payload);
  // }

  // /**
  // * Shared logic: for each Attendance, if ID present or record exists for date,
  // * update; otherwise add new.
  // */
  // private ResponseEntity<?> processBatch(
  // List<Attendance> attendances,
  // int subadminId,
  // String fullName) {

  // Employee employee =
  // employeeRepository.findBySubadminIdAndFullName(subadminId, fullName);
  // if (employee == null) {
  // return ResponseEntity.badRequest()
  // .body("Employee not found: " + fullName);
  // }

  // List<Attendance> toPersist = new ArrayList<>();
  // for (Attendance incoming : attendances) {
  // // 1) If status requires reason, enforce it
  // if (requiresReason(incoming.getStatus())
  // && (incoming.getReason() == null || incoming.getReason().isBlank())) {
  // return ResponseEntity.badRequest()
  // .body("Reason required when status='" +
  // incoming.getStatus() +
  // "' for date " + incoming.getDate());
  // }

  // // 2) Find existing by ID or by date
  // Attendance entity;
  // if (incoming.getId() != null) {
  // entity = attendanceRepository.findById(incoming.getId())
  // .orElseThrow(() ->
  // new RuntimeException("Attendance not found: ID " + incoming.getId()));
  // } else {
  // entity = attendanceRepository
  // .findByEmployeeAndDate(employee, incoming.getDate())
  // .orElseGet(() -> {
  // Attendance a = new Attendance();
  // a.setEmployee(employee);
  // return a;
  // });
  // }

  // // 3) Guard against cross-employee tampering
  // if (entity.getId() != null
  // && !Objects.equals(entity.getEmployee().getEmpId(),
  // employee.getEmpId())) {
  // return ResponseEntity.badRequest()
  // .body("Attendance for date " + incoming.getDate()
  // + " does not belong to " + fullName);
  // }

  // // 4) Copy over fields: only overwrite status/reason if provided
  // entity.setDate(incoming.getDate());
  // if (incoming.getStatus() != null) {
  // entity.setStatus(incoming.getStatus());
  // }
  // if (incoming.getReason() != null) {
  // entity.setReason(incoming.getReason());
  // }

  // // 5) Always update times
  // entity.setPunchInTime(incoming.getPunchInTime());
  // entity.setLunchInTime(incoming.getLunchInTime());
  // entity.setLunchOutTime(incoming.getLunchOutTime());
  // entity.setPunchOutTime(incoming.getPunchOutTime());

  // // 6) Recalculate derived fields
  // entity.calculateDurations();

  // toPersist.add(entity);
  // }

  // // 7) Save & return the full list
  // List<Attendance> saved = attendanceRepository.saveAll(toPersist);
  // return ResponseEntity.ok(saved);
  // }

  // at top of class:
  private static final List<String> REQUIRES_REASON = List.of("absent", "paidleave");

  private boolean requiresReason(String status) {
    if (status == null)
      return false;
    String normalized = status.trim().toLowerCase().replaceAll("[ _]", "");
    return REQUIRES_REASON.contains(normalized);
  }

  /**
   * Bulk-add or single-add attendance endpoint.
   * URL: POST /api/employee/{subadminId}/{fullName}/attendance/add/bulk
   */
  @PostMapping("/{subadminId}/{fullName}/attendance/add/bulk")
  public ResponseEntity<?> addOrUpdateAttendances(
      @PathVariable int subadminId,
      @PathVariable String fullName,
      @RequestBody JsonNode payload) {

    try {
      ObjectMapper mapper = new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      List<Attendance> attendances;
      if (payload.isArray()) {
        attendances = Arrays.asList(mapper.treeToValue(payload, Attendance[].class));
      } else {
        Attendance single = mapper.treeToValue(payload, Attendance.class);
        attendances = Collections.singletonList(single);
      }
      return processBatch(attendances, subadminId, fullName);

    } catch (JsonProcessingException e) {
      return ResponseEntity
          .badRequest()
          .body("Invalid Attendance JSON payload: " + e.getOriginalMessage());
    }
  }

  /**
   * Bulk-update or single-update attendance endpoint.
   * URL: PUT /api/employee/{subadminId}/{fullName}/attendance/update/bulk
   */
  @PutMapping("/{subadminId}/{fullName}/attendance/update/bulk")
  public ResponseEntity<?> updateOrAddAttendances(
      @PathVariable int subadminId,
      @PathVariable String fullName,
      @RequestBody JsonNode payload) {
    // reuse the same logic as POST
    return addOrUpdateAttendances(subadminId, fullName, payload);
  }

  /**
   * Shared logic: for each Attendance, if ID present or record exists for date,
   * update; otherwise add new.
   */
  private ResponseEntity<?> processBatch(
      List<Attendance> attendances,
      int subadminId,
      String fullName) {

    Employee employee = employeeRepository.findBySubadminIdAndFullName(subadminId, fullName);
    if (employee == null) {
      return ResponseEntity.badRequest().body("Employee not found: " + fullName);
    }

    List<Attendance> toPersist = new ArrayList<>();
    for (Attendance incoming : attendances) {
      // enforce reason when needed
      if (requiresReason(incoming.getStatus())
          && (incoming.getReason() == null || incoming.getReason().isBlank())) {
        return ResponseEntity.badRequest()
            .body("Reason required when status='" +
                incoming.getStatus() +
                "' for date " + incoming.getDate());
      }

      // find existing or new
      Attendance entity;
      if (incoming.getId() != null) {
        entity = attendanceRepository.findById(incoming.getId())
            .orElseThrow(() -> new RuntimeException("Attendance not found: ID " + incoming.getId()));
      } else {
        entity = attendanceRepository
            .findByEmployeeAndDate(employee, incoming.getDate())
            .orElseGet(() -> {
              Attendance a = new Attendance();
              a.setEmployee(employee);
              return a;
            });
      }

      // guard cross‑employee
      if (entity.getId() != null
          && !Objects.equals(entity.getEmployee().getEmpId(), employee.getEmpId())) {
        return ResponseEntity.badRequest()
            .body("Attendance for date " + incoming.getDate()
                + " does not belong to " + fullName);
      }

      // copy fields (only overwrite status/reason if provided)
      entity.setDate(incoming.getDate());
      if (incoming.getStatus() != null) {
        entity.setStatus(incoming.getStatus());
      }
      if (incoming.getReason() != null) {
        entity.setReason(incoming.getReason());
      }

      // only overwrite times if they came in the payload
      if (incoming.getPunchInTime() != null) {
        entity.setPunchInTime(incoming.getPunchInTime());
      }
      if (incoming.getLunchInTime() != null) {
        entity.setLunchInTime(incoming.getLunchInTime());
      }
      if (incoming.getLunchOutTime() != null) {
        entity.setLunchOutTime(incoming.getLunchOutTime());
      }
      if (incoming.getPunchOutTime() != null) {
        entity.setPunchOutTime(incoming.getPunchOutTime());
      }

      if (incoming.getPunchOutTime() != null)
        entity.setPunchOutTime(incoming.getPunchOutTime());

      // ✏️ copy workType
      if (incoming.getWorkType() != null) {
        entity.setWorkType(incoming.getWorkType());
      }

      // recalc derived fields
      entity.calculateDurations();

      toPersist.add(entity);
    }

    List<Attendance> saved = attendanceRepository.saveAll(toPersist);
    return ResponseEntity.ok(saved);
  }

  @Autowired
  private EmployeePasswordResetService passwordResetService;

  @Autowired
  private EmployeeEmailService employeeEmailService;

  /**
   * Send an employee their login details via email.
   * POST /api/employee/{subadminId}/{fullName}/send-login-details
   */
  @PostMapping("/{subadminId}/{fullName}/send-login-details")
  public ResponseEntity<String> sendLoginDetails(
      @PathVariable int subadminId,
      @PathVariable String fullName) {
    // 1) find the employee
    Employee emp = employeeRepository.findBySubadminIdAndFullName(subadminId, fullName);
    if (emp == null) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body("No employee named '" + fullName + "' under SubAdmin " + subadminId);
    }

    // 2) send the email
    boolean sent = employeeEmailService.sendEmployeeCredentials(emp);
    if (!sent) {
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to send login details to " + emp.getEmail());
    }

    // 3) success
    return ResponseEntity.ok("Login details sent to " + emp.getEmail());
  }

  /**
   * GET: all attendance records for an employee
   * URL: GET /api/employee/{subadminId}/{fullName}/attendance/all
   */
  @GetMapping("/{subadminId}/{fullName}/attendance/all")
  public ResponseEntity<?> getAllAttendance(
      @PathVariable int subadminId,
      @PathVariable String fullName) {

    Employee emp = employeeRepository.findBySubadminIdAndFullName(subadminId, fullName);
    if (emp == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Employee not found: " + fullName);
    }
    List<Attendance> list = attendanceRepository.findByEmployee(emp);
    return ResponseEntity.ok(list);
  }

  /**
   * GET: single attendance record for a specific date
   * URL: GET /api/employee/{subadminId}/{fullName}/attendance/{date}
   */
  @GetMapping("/{subadminId}/{fullName}/attendance/{date}")
  public ResponseEntity<?> getAttendanceByDate(
      @PathVariable int subadminId,
      @PathVariable String fullName,
      @PathVariable String date) {

    Employee emp = employeeRepository.findBySubadminIdAndFullName(subadminId, fullName);
    if (emp == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Employee not found: " + fullName);
    }
    Optional<Attendance> attOpt = attendanceRepository.findByEmployeeAndDate(emp, date);
    return attOpt
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("No attendance record for date: " + date));
  }

  /**
   * POST /api/employee/login-employee?email=...&password=...
   * Returns the full Employee object (with nested Subadmin) on success.
   */
  @PostMapping("/login-employee")
  public ResponseEntity<?> loginEmployee(
      @RequestParam String email,
      @RequestParam String password) {

    // 1) Find by email
    Optional<Employee> optEmp = empService.findByEmail(email);
    if (optEmp.isEmpty()) {
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body("Employee not found with email: " + email);
    }
    Employee emp = optEmp.get();

    // 2) Check password
    if (!emp.getPassword().equals(password)) {
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body("Incorrect password.");
    }

    // 3) Success! Return the full Employee (includes Subadmin)
    return ResponseEntity.ok(emp);
  }

  /**
   * 1) Request a password reset OTP.
   * POST /api/employee/forgot-password/request
   * Body (application/json): { "email": "user@example.com" }
   */
  @PostMapping(value = "/forgot-password/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> requestForgotPassword(
      @RequestBody Map<String, String> body) {
    String email = body.get("email");
    if (email == null || email.isBlank()) {
      return ResponseEntity
          .badRequest()
          .body("Missing field: email");
    }
    try {
      passwordResetService.sendResetOTP(email);
      return ResponseEntity.ok("OTP sent to email: " + email);
    } catch (RuntimeException ex) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body("Failed to send OTP: " + ex.getMessage());
    }
  }

  /**
   * 2) Verify the OTP and reset the password in one call.
   * POST /api/employee/forgot-password/verify
   * Body (application/json):
   * { "email":"user@example.com", "otp":"ABC123", "newPassword":"secret" }
   */
  @PostMapping(value = "/forgot-password/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> verifyOtpAndResetPassword(
      @RequestBody Map<String, String> body) {
    String email = body.get("email");
    String otp = body.get("otp");
    String newPassword = body.get("newPassword");

    if (email == null || otp == null || newPassword == null) {
      return ResponseEntity
          .badRequest()
          .body("Required fields: email, otp, newPassword");
    }

    if (!passwordResetService.verifyOTP(email, otp)) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body("Invalid or expired OTP.");
    }

    try {
      passwordResetService.resetPassword(email, newPassword);
      return ResponseEntity.ok("Password updated successfully.");
    } catch (RuntimeException ex) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body("Error: " + ex.getMessage());
    }
  }

  private ObjectMapper jsonMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  // ==========================================================
  // Location Tracking Endpoints
  // ==========================================================

  /**
   * Get location of a specific employee by subadmin ID and employee ID
   */
  @GetMapping("/{subadminId}/employee/{empId}/location")
  public ResponseEntity<?> getEmployeeLocation(
      @PathVariable int subadminId,
      @PathVariable int empId) {

    try {
      // Verify the employee belongs to this subadmin
      Employee employee = employeeRepository.findById(empId).orElse(null);
      if (employee == null) {
        return ResponseEntity.badRequest().body("Employee not found with ID: " + empId);
      }

      if (employee.getSubadmin() == null || employee.getSubadmin().getId() != subadminId) {
        return ResponseEntity.badRequest().body("Employee does not belong to this subadmin");
      }

      Map<String, Object> locationData = empService.getEmployeeLocation(empId);
      return ResponseEntity.ok(locationData);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }

  /**
   * Get locations of all employees for a subadmin
   */
  @GetMapping("/{subadminId}/employee/locations")
  public ResponseEntity<?> getAllEmployeeLocations(@PathVariable int subadminId) {
    try {
      List<Map<String, Object>> locations = empService.getAllEmployeeLocations(subadminId);
      return ResponseEntity.ok(locations);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }

  /**
   * Update an employee's location
   */
  @PutMapping("/{subadminId}/employee/{empId}/location")
  public ResponseEntity<?> updateEmployeeLocation(
      @PathVariable int subadminId,
      @PathVariable int empId,
      @RequestBody Map<String, String> locationData) {

    try {
      // Verify the employee belongs to this subadmin
      Employee employee = employeeRepository.findById(empId).orElse(null);
      if (employee == null) {
        return ResponseEntity.badRequest().body("Employee not found with ID: " + empId);
      }

      if (employee.getSubadmin() == null || employee.getSubadmin().getId() != subadminId) {
        return ResponseEntity.badRequest().body("Employee does not belong to this subadmin");
      }

      String latitude = locationData.get("latitude");
      String longitude = locationData.get("longitude");

      if (latitude == null || longitude == null) {
        return ResponseEntity.badRequest().body("Latitude and longitude are required");
      }

      Employee updatedEmployee = empService.updateLocation(empId, latitude, longitude);
      return ResponseEntity.ok(updatedEmployee);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }

  @PostMapping("/{subadminId}/employee/{empId}/location")
  public ResponseEntity<?> postEmployeeLocation(
      @PathVariable int subadminId,
      @PathVariable int empId,
      @RequestBody Map<String, String> locationData) {

    return updateEmployeeLocation(subadminId, empId, locationData);
  }

  /**
   * Single‑record add or update endpoint.
   */
  @PostMapping("/{subadminId}/{fullName}/attendance/addnew")
  public ResponseEntity<?> addOrUpdateAttendance(
      @PathVariable int subadminId,
      @PathVariable String fullName,
      @RequestBody JsonNode payload) {

    Employee employee = employeeRepository
        .findBySubadminIdAndFullName(subadminId, fullName);
    if (employee == null) {
      return ResponseEntity.badRequest()
          .body("Employee not found: " + fullName);
    }

    // 1) Map JSON → Attendance
    Attendance att;
    try {
      att = jsonMapper().treeToValue(payload, Attendance.class);
    } catch (JsonProcessingException e) {
      return ResponseEntity.badRequest()
          .body("Invalid Attendance JSON");
    }

    // 2) Lookup existing or prepare new
    Optional<Attendance> existing = attendanceRepository.findByEmployeeAndDate(employee, att.getDate());
    Attendance entity = existing.orElseGet(() -> {
      Attendance a = new Attendance();
      a.setEmployee(employee);
      return a;
    });

    // 3) Copy only non-null status/reason
    if (att.getStatus() != null) {
      entity.setStatus(att.getStatus());
    }
    if (att.getReason() != null) {
      entity.setReason(att.getReason());
    }

    // 4) Always update the times
    entity.setPunchInTime(att.getPunchInTime());
    entity.setLunchInTime(att.getLunchInTime());
    entity.setLunchOutTime(att.getLunchOutTime());
    entity.setPunchOutTime(att.getPunchOutTime());

    // 5) Recalculate durations
    entity.calculateDurations();

    // 6) Save
    Attendance saved = attendanceRepository.save(entity);
    return ResponseEntity
        .status(existing.isPresent() ? HttpStatus.OK : HttpStatus.CREATED)
        .body(saved);
  }
}