package com.jaywant.demo.Controller;

import com.jaywant.demo.Entity.Resume;
import com.jaywant.demo.Service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload/{empId}")
    public ResponseEntity<?> uploadResume(
            @PathVariable int empId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobRole") String jobRole) {
        try {
            Resume resume = resumeService.uploadResume(file, empId, jobRole);
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading resume: " + e.getMessage());
        }
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getEmployeeResumes(@PathVariable int empId) {
        try {
            List<Resume> resumes = resumeService.getEmployeeResumes(empId);
            return ResponseEntity.ok(resumes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching resumes: " + e.getMessage());
        }
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<?> getResumeById(@PathVariable int resumeId) {
        try {
            Resume resume = resumeService.getResumeById(resumeId);
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching resume: " + e.getMessage());
        }
    }
}
