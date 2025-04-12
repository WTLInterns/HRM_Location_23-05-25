package com.jaywant.demo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.SubAdminRepo;

@Service
public class SubAdminService {

  @Autowired
  private SubAdminRepo repo;

  private final String uploadDir = "src/main/resources/upload/";

  // Method to return a Subadmin for a given email.
  public Subadmin getSubAdminByEmail(String email) {
    List<Subadmin> list = repo.findByEmail(email);
    if (list != null && !list.isEmpty()) {
      return list.get(0);
    }
    throw new RuntimeException("Subadmin not found with email: " + email);
  }

  /**
   * Update the details of an existing Subadmin, including status and file fields.
   */
  public Subadmin updateSubAdmin(int id, String name, String lastname, String email, String phoneno,
      String registercompanyname, String status,
      MultipartFile stampImg, MultipartFile signature, MultipartFile companylogo) {

    Subadmin subAdmin = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Subadmin not found with ID: " + id));

    subAdmin.setName(name);
    subAdmin.setLastname(lastname);
    subAdmin.setEmail(email);
    subAdmin.setPhoneno(phoneno);
    subAdmin.setRegistercompanyname(registercompanyname);
    subAdmin.setStatus(status);

    if (stampImg != null && !stampImg.isEmpty()) {
      String stampImgFileName = saveFile(stampImg);
      subAdmin.setStampImg(stampImgFileName);
    }

    if (signature != null && !signature.isEmpty()) {
      String signatureFileName = saveFile(signature);
      subAdmin.setSignature(signatureFileName);
    }

    if (companylogo != null && !companylogo.isEmpty()) {
      String logoFileName = saveFile(companylogo);
      subAdmin.setCompanylogo(logoFileName);
    }

    return repo.save(subAdmin);
  }

  // Login method (plain text password comparison)
  public Subadmin login(String email, String password) {
    List<Subadmin> subAdmins = repo.findByEmail(email);
    if (!subAdmins.isEmpty()) {
      Subadmin subAdmin = subAdmins.get(0);
      if (password.equals(subAdmin.getPassword())) {
        return subAdmin;
      }
    }
    return null;
  }

  // Update the password for a Subadmin.
  public void updatePassword(int id, String newPassword) {
    Subadmin subAdmin = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Subadmin not found with ID: " + id));
    subAdmin.setPassword(newPassword);
    repo.save(subAdmin);
  }

  // Get all Subadmins.
  public List<Subadmin> getAllSubAdmins() {
    return repo.findAll();
  }

  // Delete Subadmin by ID.
  public void deleteSubAdmin(int id) {
    repo.deleteById(id);
  }

  // Utility method for saving a MultipartFile.
  private String saveFile(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    String fileName = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename : "file");
    try {
      Path uploadPath = Paths.get(uploadDir);
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }
      Path filePath = uploadPath.resolve(fileName);
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("File saving failed for: " + fileName, e);
    }
    return fileName;
  }
}
