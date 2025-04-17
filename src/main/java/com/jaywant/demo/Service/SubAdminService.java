// package com.jaywant.demo.Service;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
// import java.util.List;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
// import com.jaywant.demo.Entity.Subadmin;
// import com.jaywant.demo.Repo.SubAdminRepo;

// @Service
// public class SubAdminService {

//   @Autowired
//   private SubAdminRepo repo;

//   // Directory where all files (images) will be stored.
//   // If this folder does not exist, it will be automatically created.
//   private final String uploadDir = "src/main/resources/static/images/profile/";

//   // Method to return a Subadmin by email.
//   public Subadmin getSubAdminByEmail(String email) {
//     List<Subadmin> list = repo.findByEmail(email);
//     if (list != null && !list.isEmpty()) {
//       return list.get(0);
//     }
//     throw new RuntimeException("Subadmin not found with email: " + email);
//   }

//   /**
//    * Update the details of an existing Subadmin, including file fields.
//    */
//   public Subadmin updateSubAdmin(int id, String name, String lastname, String email, String phoneno,
//       String registercompanyname, String status,
//       MultipartFile stampImg, MultipartFile signature, MultipartFile companylogo, String cinno, String compnayurl,
//       String address) {

//     Subadmin subAdmin = repo.findById(id)
//         .orElseThrow(() -> new RuntimeException("Subadmin not found with ID: " + id));

//     subAdmin.setName(name);
//     subAdmin.setLastname(lastname);
//     subAdmin.setEmail(email);
//     subAdmin.setPhoneno(phoneno);
//     subAdmin.setRegistercompanyname(registercompanyname);
//     subAdmin.setStatus(status);
//     subAdmin.setCinno(cinno);
//     subAdmin.setCompanyurl(compnayurl);
//     subAdmin.setAddress(address);

//     if (stampImg != null && !stampImg.isEmpty()) {
//       String stampImgFileName = saveFile(stampImg);
//       subAdmin.setStampImg(stampImgFileName);
//     }
//     if (signature != null && !signature.isEmpty()) {
//       String signatureFileName = saveFile(signature);
//       subAdmin.setSignature(signatureFileName);
//     }
//     if (companylogo != null && !companylogo.isEmpty()) {
//       String logoFileName = saveFile(companylogo);
//       subAdmin.setCompanylogo(logoFileName);
//     }
//     return repo.save(subAdmin);
//   }

//   /**
//    * Update only the image files (stampImg, signature, companylogo) for a
//    * subadmin.
//    */
//   public Subadmin updateSubAdminImages(int id, MultipartFile stampImg, MultipartFile signature,
//       MultipartFile companylogo) {
//     Subadmin subAdmin = repo.findById(id)
//         .orElseThrow(() -> new RuntimeException("Subadmin not found with ID: " + id));
//     if (stampImg != null && !stampImg.isEmpty()) {
//       String fileName = saveFile(stampImg);
//       subAdmin.setStampImg(fileName);
//     }
//     if (signature != null && !signature.isEmpty()) {
//       String fileName = saveFile(signature);
//       subAdmin.setSignature(fileName);
//     }
//     if (companylogo != null && !companylogo.isEmpty()) {
//       String fileName = saveFile(companylogo);
//       subAdmin.setCompanylogo(fileName);
//     }
//     return repo.save(subAdmin);
//   }

//   // Login method (plain text password check)
//   public Subadmin login(String email, String password) {
//     List<Subadmin> subAdmins = repo.findByEmail(email);
//     if (!subAdmins.isEmpty()) {
//       Subadmin subAdmin = subAdmins.get(0);
//       if (password.equals(subAdmin.getPassword())) {
//         return subAdmin;
//       }
//     }
//     return null;
//   }

//   // Update Subadmin password.
//   public void updatePassword(int id, String newPassword) {
//     Subadmin subAdmin = repo.findById(id)
//         .orElseThrow(() -> new RuntimeException("Subadmin not found with ID: " + id));
//     subAdmin.setPassword(newPassword);
//     repo.save(subAdmin);
//   }

//   // Get all Subadmins.
//   public List<Subadmin> getAllSubAdmins() {
//     return repo.findAll();
//   }

//   // Delete Subadmin by ID.
//   public void deleteSubAdmin(int id) {
//     repo.deleteById(id);
//   }

//   // Utility method for saving a file.
//   private String saveFile(MultipartFile file) {
//     String originalFilename = file.getOriginalFilename();
//     String fileName = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename : "file");
//     try {
//       Path uploadPath = Paths.get(uploadDir);
//       // Create the directory if it does not exist.
//       if (!Files.exists(uploadPath)) {
//         Files.createDirectories(uploadPath);
//       }
//       Path filePath = uploadPath.resolve(fileName);
//       Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//     } catch (IOException e) {
//       e.printStackTrace();
//       throw new RuntimeException("File saving failed for: " + fileName, e);
//     }
//     return fileName;
//   }
// }

package com.jaywant.demo.Service;

import java.io.IOException;
import java.nio.file.*;
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

  private final String uploadDir = "src/main/resources/static/images/profile/";

  // Get Subadmin by email
  public Subadmin getSubAdminByEmail(String email) {
    return repo.findByEmail(email)
        .stream()
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Subadmin not found with email: " + email));
  }

  // Update all fields of subadmin including image files
  public Subadmin updateSubAdmin(
      int id,
      String name,
      String lastname,
      String email,
      String phoneno,
      String registercompanyname,
      String status,
      String cinno,
      String companyurl,
      String address,
      MultipartFile stampImg,
      MultipartFile signature,
      MultipartFile companylogo) {

    Subadmin subAdmin = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Subadmin not found with ID: " + id));

    subAdmin.setName(name);
    subAdmin.setLastname(lastname);
    subAdmin.setEmail(email);
    subAdmin.setPhoneno(phoneno);
    subAdmin.setRegistercompanyname(registercompanyname);
    subAdmin.setStatus(status);
    subAdmin.setCinno(cinno);
    subAdmin.setCompanyurl(companyurl);
    subAdmin.setAddress(address);

    if (cinno != null && !cinno.isEmpty()) {
      subAdmin.setStampImg(saveFile(stampImg));
    }
    if (companyurl != null && !companyurl.isEmpty()) {
      subAdmin.setSignature(saveFile(signature));
    }
    if (address != null && !address.isEmpty()) {
      subAdmin.setCompanylogo(saveFile(companylogo));
    }

    return repo.save(subAdmin);
  }

  // Update only images
  public Subadmin updateSubAdminImages(int id, MultipartFile stampImg, MultipartFile signature,
      MultipartFile companylogo) {
    Subadmin subAdmin = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Subadmin not found with ID: " + id));

    if (stampImg != null && !stampImg.isEmpty()) {
      subAdmin.setStampImg(saveFile(stampImg));
    }
    if (signature != null && !signature.isEmpty()) {
      subAdmin.setSignature(saveFile(signature));
    }
    if (companylogo != null && !companylogo.isEmpty()) {
      subAdmin.setCompanylogo(saveFile(companylogo));
    }

    return repo.save(subAdmin);
  }

  // Login with plain-text password
  public Subadmin login(String email, String password) {
    return repo.findByEmail(email)
        .stream()
        .filter(subAdmin -> password.equals(subAdmin.getPassword()))
        .findFirst()
        .orElse(null);
  }

  // Update subadmin password
  public void updatePassword(int id, String newPassword) {
    Subadmin subAdmin = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Subadmin not found with ID: " + id));
    subAdmin.setPassword(newPassword);
    repo.save(subAdmin);
  }

  // Get all subadmins
  public List<Subadmin> getAllSubAdmins() {
    return repo.findAll();
  }

  // Delete subadmin by ID
  public void deleteSubAdmin(int id) {
    if (!repo.existsById(id)) {
      throw new RuntimeException("Subadmin not found with ID: " + id);
    }
    repo.deleteById(id);
  }

  // Save file and return filename
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
      return fileName;

    } catch (IOException e) {
      throw new RuntimeException("Failed to save file: " + fileName, e);
    }
  }

  // public Subadmin updateSubAdmin(int id, String name, String lastname, String
  // email, String phoneno,
  // String registercompanyname, String status, String cinno, String companyurl,
  // String address,
  // MultipartFile stampImg, MultipartFile signature, MultipartFile companylogo) {
  // // TODO Auto-generated method stub
  // throw new UnsupportedOperationException("Unimplemented method
  // 'updateSubAdmin'");
  // }
}
