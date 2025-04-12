package com.jaywant.demo.Controller;

import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.SubAdminRepo;
import com.jaywant.demo.Repo.EmployeeRepo;
// import com.jaywant.demo.DTO.EmployeeWithAttendanceDTO;
import com.jaywant.demo.Service.SubAdminPasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subadmin")
@CrossOrigin
public class SubAdminController {

  @Autowired
  private SubAdminRepo subAdminRepo;

  @Autowired
  private EmployeeRepo employeeRepo;

  @Autowired
  private SubAdminPasswordResetService passwordResetService;

  // Endpoint for SubAdmin login
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
    try {
      // Find subadmin by email
      List<Subadmin> subAdmins = subAdminRepo.findByEmail(email);

      // If no subadmin found with that email
      if (subAdmins.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
      }

      Subadmin subAdmin = subAdmins.get(0); // Assume only one subadmin with that email

      // Check if the password matches the stored password directly (no encryption
      // handling)
      if (password.equals(subAdmin.getPassword())) {
        return ResponseEntity.ok(subAdmin);
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
      }
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login.");
    }
  }

  // Endpoint for updating SubAdmin fields including status, image files, etc.
  @PutMapping("/update-fields/{id}")
  public ResponseEntity<?> updateSubAdminFields(
      @PathVariable int id,
      @RequestParam String name,
      @RequestParam String lastname,
      @RequestParam String email,
      @RequestParam String phoneno,
      @RequestParam String registercompanyname,
      @RequestParam(value = "stampImg", required = false) MultipartFile stampImg,
      @RequestParam(value = "signature", required = false) MultipartFile signature,
      @RequestParam(value = "companylogo", required = false) MultipartFile companylogo,
      @RequestParam String status) {
    try {
      Optional<Subadmin> subAdminOpt = subAdminRepo.findById(id);

      if (!subAdminOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SubAdmin with ID " + id + " not found.");
      }

      Subadmin subAdmin = subAdminOpt.get();
      subAdmin.setName(name);
      subAdmin.setLastname(lastname);
      subAdmin.setEmail(email);
      subAdmin.setPhoneno(phoneno);
      subAdmin.setRegistercompanyname(registercompanyname);
      subAdmin.setStatus(status);

      // Update files only if provided
      if (stampImg != null)
        subAdmin.setStampImg(stampImg.getOriginalFilename());
      if (signature != null)
        subAdmin.setSignature(signature.getOriginalFilename());
      if (companylogo != null)
        subAdmin.setCompanylogo(companylogo.getOriginalFilename());

      Subadmin updatedSubAdmin = subAdminRepo.save(subAdmin);

      return ResponseEntity.ok(updatedSubAdmin);
    } catch (RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  // Endpoint for updating SubAdmin status
  @PutMapping("/update-status/{id}")
  public ResponseEntity<?> updateSubAdminStatus(@PathVariable int id, @RequestParam String status) {
    try {
      Optional<Subadmin> subAdminOpt = subAdminRepo.findById(id);

      if (!subAdminOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SubAdmin with ID " + id + " not found.");
      }

      Subadmin subAdmin = subAdminOpt.get();
      subAdmin.setStatus(status);
      subAdminRepo.save(subAdmin);

      return ResponseEntity.ok("SubAdmin status updated successfully.");
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating status.");
    }
  }

  // Endpoint to send email to sub-admins
  @PostMapping("/send-email/{subadminemail}")
  public ResponseEntity<String> sendEmail(@PathVariable("subadminemail") String subadminemail) {
    // Logic for sending email, potentially using an email service
    boolean sent = true; // Assuming the email was sent successfully
    if (sent) {
      return ResponseEntity.ok("Email sent successfully to " + subadminemail);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("SubAdmin with email " + subadminemail + " not found.");
    }
  }

  // Endpoint to fetch all sub-admins by company name
  @GetMapping("/by-company/{registercompanyname:.+}")
  public ResponseEntity<?> getSubAdminsByCompanyName(@PathVariable("registercompanyname") String companyName) {
    try {
      List<Subadmin> subAdmins = subAdminRepo.findByRegistercompanyname(companyName);

      if (subAdmins.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No sub-admins found for company: " + companyName);
      }

      return ResponseEntity.ok(subAdmins);
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while fetching sub-admins.");
    }
  }

  // Endpoint for forgot password request (send OTP)
  @PostMapping("/forgot-password/request")
  public ResponseEntity<String> requestForgotPassword(@RequestParam String email) {
    try {
      passwordResetService.sendResetOTP(email);
      return ResponseEntity.ok("OTP sent to email: " + email);
    } catch (RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  // Endpoint for verifying OTP and resetting password
  @PostMapping("/forgot-password/verify")
  public ResponseEntity<String> verifyOtpAndResetPassword(@RequestParam String email, @RequestParam String otp,
      @RequestParam String newPassword) {
    try {
      if (!passwordResetService.verifyOTP(email, otp)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
      }
      passwordResetService.resetPassword(email, newPassword);
      return ResponseEntity.ok("Password updated successfully.");
    } catch (RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  // Endpoint to retrieve sub-admin details by email
  @GetMapping("/subadmin-by-email/{email}")
  public ResponseEntity<?> getSubAdminByEmail(@PathVariable("email") String email) {
    List<Subadmin> subAdmins = subAdminRepo.findByEmail(email);
    if (subAdmins.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SubAdmin with email " + email + " not found.");
    }
    return ResponseEntity.ok(subAdmins.get(0)); // Return the first found sub-admin
  }
}
