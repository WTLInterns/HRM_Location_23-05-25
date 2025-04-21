package com.jaywant.demo.Controller;

import com.jaywant.demo.Entity.Certificate;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

  @Autowired
  private CertificateService certificateService;

  // Upload certificate (joining letter, offer letter, termination letter)
  @PostMapping("/upload/{subadminId}/{employeeFullName}")
  public ResponseEntity<String> uploadCertificate(@PathVariable int subadminId,
      @PathVariable String employeeFullName,
      @RequestParam(value = "joiningLetter", required = false) MultipartFile joiningLetter,
      @RequestParam(value = "offerLetter", required = false) MultipartFile offerLetter,
      @RequestParam(value = "terminationLetter", required = false) MultipartFile terminationLetter) {

    try {
      Certificate certificate = certificateService.uploadCertificate(subadminId, employeeFullName, joiningLetter,
          offerLetter, terminationLetter);
      if (certificate == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
      }
      return ResponseEntity.ok("Certificate uploaded successfully");
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
    }
  }

  // Get certificates for a specific employee
  @GetMapping("/get/{subadminId}/{employeeFullName}")
  public ResponseEntity<List<Certificate>> getCertificates(@PathVariable int subadminId,
      @PathVariable String employeeFullName) {
    Employee employee = certificateService.getEmployee(subadminId, employeeFullName);
    if (employee == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    List<Certificate> certificates = employee.getCertificates();
    if (certificates.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    return ResponseEntity.ok(certificates);
  }

  // Update certificate details (e.g., offer letter, termination letter)
  @PutMapping("/update/{subadminId}/{employeeFullName}/{certificateId}")
  public ResponseEntity<String> updateCertificate(@PathVariable int subadminId,
      @PathVariable String employeeFullName,
      @PathVariable Long certificateId,
      @RequestParam(value = "joiningLetter", required = false) MultipartFile joiningLetter,
      @RequestParam(value = "offerLetter", required = false) MultipartFile offerLetter,
      @RequestParam(value = "terminationLetter", required = false) MultipartFile terminationLetter) {

    try {
      // Ensure the employee exists before updating the certificate
      Employee employee = certificateService.getEmployee(subadminId, employeeFullName);
      if (employee == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
      }

      // Call the service method to update the certificate
      Certificate certificate = certificateService.updateCertificate(certificateId, joiningLetter, offerLetter,
          terminationLetter);
      if (certificate == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Certificate not found");
      }
      return ResponseEntity.ok("Certificate updated successfully");
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File update failed: " + e.getMessage());
    }
  }

  // Delete a certificate by certificateId and employee details
  @DeleteMapping("/delete/{subadminId}/{employeeFullName}/{certificateId}")
  public ResponseEntity<String> deleteCertificate(@PathVariable int subadminId,
      @PathVariable String employeeFullName,
      @PathVariable Long certificateId) {
    try {
      // Ensure the employee exists before deleting the certificate
      Employee employee = certificateService.getEmployee(subadminId, employeeFullName);
      if (employee == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
      }

      // Call the service method to delete the certificate
      certificateService.deleteCertificate(certificateId);
      return ResponseEntity.ok("Certificate deleted successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Certificate not found");
    }
  }
}
