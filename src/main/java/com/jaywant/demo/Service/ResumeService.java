package com.jaywant.demo.Service;

import com.jaywant.demo.Entity.Resume;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Repo.ResumeRepo;
import com.jaywant.demo.Repo.EmployeeRepo;
import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.SubAdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResumeService {
    
    @Autowired
    private ResumeRepo resumeRepo;
    
    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private SubAdminRepo subadminRepo;

    private final String UPLOAD_DIR = "uploads/resumes/";

    public Resume uploadResume(MultipartFile file, int empId, String jobRole, int subadminId) throws IOException {
        Employee employee = employeeRepo.findById(empId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        Subadmin subadmin = subadminRepo.findById(subadminId)
            .orElseThrow(() -> new RuntimeException("Subadmin not found"));

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        String fileName = empId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        // Create resume entry
        Resume resume = new Resume();
        resume.setEmployee(employee);
        resume.setJobRole(jobRole);
        resume.setResumeFileName(fileName);
        resume.setResumeFilePath(filePath.toString());
        resume.setUploadDateTime(LocalDateTime.now());
        resume.setSubadmin(subadmin);
        resume.setVerified(false);

        return resumeRepo.save(resume);
    }

    public List<Resume> getEmployeeResumes(int empId) {
        return resumeRepo.findByEmployeeEmpId(empId);
    }

    public List<Resume> getSubadminResumes(int subadminId) {
        return resumeRepo.findBySubadminId(subadminId);
    }

    public List<Resume> getSubadminResumesByVerification(int subadminId, boolean isVerified) {
        return resumeRepo.findBySubadminIdAndIsVerified(subadminId, isVerified);
    }

    public Resume getResumeById(int resumeId) {
        return resumeRepo.findById(resumeId)
            .orElseThrow(() -> new RuntimeException("Resume not found"));
    }

    public Resume verifyResume(int resumeId, boolean isVerified) {
        Resume resume = getResumeById(resumeId);
        resume.setVerified(isVerified);
        return resumeRepo.save(resume);
    }
}
