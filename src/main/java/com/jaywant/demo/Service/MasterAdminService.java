package com.jaywant.demo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jaywant.demo.Entity.MasterAdmin;
import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.MasterAdminRepo;
import com.jaywant.demo.Repo.SubAdminRepo;

@Service
public class MasterAdminService {

  @Autowired
  private MasterAdminRepo masterRepo;

  @Autowired
  private SubAdminRepo subAdminRepo;

  // The directory where files will be stored
  private final String uploadDir = "src/main/resources/static/images/profile/";

  // ----- MasterAdmin Endpoints -----

  public MasterAdmin registerMasterAdmin(MasterAdmin masterAdmin, MultipartFile profileImg) {
    if (profileImg != null && !profileImg.isEmpty()) {
      masterAdmin.setProfileImg(saveFile(profileImg));
    }
    return masterRepo.save(masterAdmin);
  }

  public MasterAdmin login(String email, String password) {
    MasterAdmin admin = masterRepo.findByEmail(email);
    return (admin != null && admin.getPassword().equals(password)) ? admin : null;
  }

  public MasterAdmin updateMasterAdmin(MasterAdmin updatedAdmin, MultipartFile profileImg) {
    MasterAdmin existing = masterRepo.findById(updatedAdmin.getId())
        .orElseThrow(() -> new RuntimeException("Master Admin not found"));

    existing.setName(updatedAdmin.getName());
    existing.setEmail(updatedAdmin.getEmail());
    existing.setMobileno(updatedAdmin.getMobileno());
    existing.setRoll(updatedAdmin.getRoll());

    if (!updatedAdmin.getPassword().equals(existing.getPassword())) {
      existing.setPassword(updatedAdmin.getPassword());
    }

    if (profileImg != null && !profileImg.isEmpty()) {
      existing.setProfileImg(saveFile(profileImg));
    }

    return masterRepo.save(existing);
  }

  public void deleteMasterAdmin(Long id) {
    if (!masterRepo.existsById(id)) {
      throw new RuntimeException("Master Admin with ID " + id + " not found");
    }
    masterRepo.deleteById(id);
  }

  public void updatePassword(Long id, String newPassword) {
    MasterAdmin admin = masterRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Master Admin not found"));
    admin.setPassword(newPassword);
    masterRepo.save(admin);
  }

  public MasterAdmin findByEmail(String email) {
    return masterRepo.findByEmail(email);
  }

  // ----- Utility method for file saving -----

  private String saveFile(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    String newFilename = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename : "file");

    try {
      Path dirPath = Paths.get(uploadDir);
      if (!Files.exists(dirPath)) {
        Files.createDirectories(dirPath);
      }

      Path filePath = dirPath.resolve(newFilename);
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
      return newFilename;
    } catch (IOException e) {
      throw new RuntimeException("Could not save file: " + newFilename, e);
    }
  }

  // ----- Subadmin Endpoints -----
  // Updated to accept multipart file uploads for stampImg, signature and
  // companylogo

  public Subadmin createSubAdmin(Subadmin subAdmin, Long masterAdminId,
      MultipartFile stampImg, MultipartFile signature, MultipartFile companylogo) {

    MasterAdmin masterAdmin = masterRepo.findById(masterAdminId)
        .orElseThrow(() -> new RuntimeException("Master Admin not found with ID: " + masterAdminId));

    subAdmin.setMasterAdmin(masterAdmin);

    if (stampImg != null && !stampImg.isEmpty()) {
      subAdmin.setStampImg(saveFile(stampImg));
    }
    if (signature != null && !signature.isEmpty()) {
      subAdmin.setSignature(saveFile(signature));
    }
    if (companylogo != null && !companylogo.isEmpty()) {
      subAdmin.setCompanylogo(saveFile(companylogo));
    }

    return subAdminRepo.save(subAdmin);
  }
}
