package com.jaywant.demo.Controller;

import com.jaywant.demo.Entity.Resume;
import com.jaywant.demo.Service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/resume/{subadminId}")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload/{empId}")
    public ResponseEntity<?> uploadResume(
            @PathVariable int subadminId,
            @PathVariable int empId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobRole") String jobRole) {
        try {
            Resume resume = resumeService.uploadResume(file, empId, jobRole, subadminId);
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading resume: " + e.getMessage());
        }
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getEmployeeResumes(@PathVariable int subadminId, @PathVariable int empId) {
        try {
            List<Resume> resumes = resumeService.getEmployeeResumes(empId);
            return ResponseEntity.ok(resumes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching resumes: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getSubadminResumes(@PathVariable int subadminId) {
        try {
            List<Resume> resumes = resumeService.getSubadminResumes(subadminId);
            return ResponseEntity.ok(resumes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching resumes: " + e.getMessage());
        }
    }

    @GetMapping("/verified/{status}")
    public ResponseEntity<?> getSubadminResumesByVerification(@PathVariable int subadminId, @PathVariable boolean status) {
        try {
            List<Resume> resumes = resumeService.getSubadminResumesByVerification(subadminId, status);
            return ResponseEntity.ok(resumes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching resumes: " + e.getMessage());
        }
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<?> getResumeById(@PathVariable int subadminId, @PathVariable int resumeId) {
        try {
            Resume resume = resumeService.getResumeById(resumeId);
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching resume: " + e.getMessage());
        }
    }

    @PutMapping("/verify/{resumeId}")
    public ResponseEntity<?> verifyResume(@PathVariable int subadminId, @PathVariable int resumeId, @RequestParam("status") boolean status) {
        try {
            Resume resume = resumeService.verifyResume(resumeId, status);
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error verifying resume: " + e.getMessage());
        }
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<?> deleteResume(@PathVariable int subadminId, @PathVariable int resumeId) {
        try {
            resumeService.deleteResume(resumeId);
            return ResponseEntity.ok("Resume deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting resume: " + e.getMessage());
        }
    }
}
