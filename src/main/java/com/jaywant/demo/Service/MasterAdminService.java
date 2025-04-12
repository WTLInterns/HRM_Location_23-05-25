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
import com.jaywant.demo.Repo.MasterAdminRepo;

@Service
public class MasterAdminService {

  @Autowired
  private MasterAdminRepo masterRepo;

  private final String uploadDir = "src/main/resources/static/images/profile/";

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

  private String saveFile(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    String newFilename = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename : "profile");

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
}
