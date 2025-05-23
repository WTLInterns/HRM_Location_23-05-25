package com.jaywant.demo.Service;

import com.jaywant.demo.Entity.Attendance;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.AttendanceRepo;
import com.jaywant.demo.Repo.EmployeeRepo;
import com.jaywant.demo.Repo.SubAdminRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

  @Value("${google.maps.api.key:AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w}")
  private String apiKey;

  @Autowired
  private EmployeeRepo employeeRepo;

  @Autowired
  private SubAdminRepo subAdminRepo;

  @Autowired
  private AttendanceRepo attendanceRepo;
  
  @Autowired
  private SimpMessagingTemplate messagingTemplate;

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
  
  /**
   * Update an employee's current location
   */
  public Employee updateLocation(int empId, String latitude, String longitude) {
    Optional<Employee> opt = employeeRepo.findById(empId);
    if (!opt.isPresent()) {
      throw new RuntimeException("Employee not found with id: " + empId);
    }
    
    Employee emp = opt.get();
    
    // Store the previous location as last location
    if (emp.getLatitude() != null && emp.getLongitude() != null) {
      emp.setLastLatitude(emp.getLatitude());
      emp.setLastLongitude(emp.getLongitude());
    }
    
    // Update current location
    emp.setLatitude(latitude);
    emp.setLongitude(longitude);
    
    Employee updatedEmployee = employeeRepo.save(emp);
    
    // Send the updated location via WebSocket to subscribers
    if (updatedEmployee.getSubadmin() != null) {
      String destination = "/topic/location/" + updatedEmployee.getSubadmin().getId();
      Map<String, Object> locationData = new HashMap<>();
      locationData.put("empId", updatedEmployee.getEmpId());
      locationData.put("fullName", updatedEmployee.getFullName());
      locationData.put("latitude", updatedEmployee.getLatitude());
      locationData.put("longitude", updatedEmployee.getLongitude());
      locationData.put("lastLatitude", updatedEmployee.getLastLatitude());
      locationData.put("lastLongitude", updatedEmployee.getLastLongitude());
      
      messagingTemplate.convertAndSend(destination, locationData);
    }
    
    return updatedEmployee;
  }
  
  /**
   * Get an employee's current location
   */
  public Map<String, Object> getEmployeeLocation(int empId) {
    Optional<Employee> opt = employeeRepo.findById(empId);
    if (!opt.isPresent()) {
      throw new RuntimeException("Employee not found with id: " + empId);
    }
    
    Employee emp = opt.get();
    Map<String, Object> locationData = new HashMap<>();
    locationData.put("empId", emp.getEmpId());
    locationData.put("fullName", emp.getFullName());
    locationData.put("latitude", emp.getLatitude());
    locationData.put("longitude", emp.getLongitude());
    locationData.put("lastLatitude", emp.getLastLatitude());
    locationData.put("lastLongitude", emp.getLastLongitude());
    locationData.put("apiKey", apiKey);
    
    return locationData;
  }
  
  /**
   * Get all employee locations for a specific subadmin
   */
  public List<Map<String, Object>> getAllEmployeeLocations(int subadminId) {
    List<Employee> employees = employeeRepo.findBySubadminId(subadminId);
    List<Map<String, Object>> locations = new java.util.ArrayList<>();
    
    for (Employee emp : employees) {
      if (emp.getLatitude() != null && emp.getLongitude() != null) {
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("empId", emp.getEmpId());
        locationData.put("fullName", emp.getFullName());
        locationData.put("latitude", emp.getLatitude());
        locationData.put("longitude", emp.getLongitude());
        locationData.put("lastLatitude", emp.getLastLatitude());
        locationData.put("lastLongitude", emp.getLastLongitude());
        locations.add(locationData);
      }
    }
    
    return locations;
  }
}
