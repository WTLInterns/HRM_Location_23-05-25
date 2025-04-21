package com.jaywant.demo.Service;

import com.jaywant.demo.Entity.Certificate;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.CertificateRepo;
import com.jaywant.demo.Repo.EmployeeRepo;
import com.jaywant.demo.Repo.SubAdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

  private final String uploadDir = "src/main/resources/static/images/profile/";

  @Autowired
  private SubAdminRepo subAdminRepo;

  @Autowired
  private EmployeeRepo employeeRepo;

  @Autowired
  private CertificateRepo certificateRepo;

  public Employee getEmployee(int subadminId, String fullName) {
    Optional<Subadmin> subadminOpt = subAdminRepo.findById(subadminId);
    if (subadminOpt.isEmpty())
      return null;

    String[] names = fullName.trim().split(" ");
    if (names.length < 2)
      return null;

    return employeeRepo.findBySubadminAndFirstNameAndLastNameIgnoreCase(subadminOpt.get(), names[0], names[1]);
  }

  public String saveFile(MultipartFile file) throws IOException {
    String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
    Path path = Paths.get(uploadDir + fileName);
    Files.createDirectories(path.getParent());
    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    return fileName;
  }

  public Certificate uploadCertificate(int subadminId, String employeeFullName, MultipartFile joiningLetter,
      MultipartFile offerLetter, MultipartFile terminationLetter) throws IOException {
    Employee employee = getEmployee(subadminId, employeeFullName);
    if (employee == null)
      return null;

    Certificate certificate = new Certificate();
    certificate.setEmployee(employee);

    if (joiningLetter != null)
      certificate.setJoiningLetter(saveFile(joiningLetter));
    if (offerLetter != null)
      certificate.setOfferLetter(saveFile(offerLetter));
    if (terminationLetter != null)
      certificate.setTerminationLetter(saveFile(terminationLetter));

    return certificateRepo.save(certificate);
  }

  public Optional<Certificate> getCertificates(int subadminId) {
    return certificateRepo.findById((long) subadminId);
  }

  public Certificate updateCertificate(Long certificateId, MultipartFile joiningLetter, MultipartFile offerLetter,
      MultipartFile terminationLetter) throws IOException {
    Optional<Certificate> certOpt = certificateRepo.findById(certificateId);
    if (certOpt.isEmpty())
      return null;

    Certificate certificate = certOpt.get();

    if (joiningLetter != null)
      certificate.setJoiningLetter(saveFile(joiningLetter));
    if (offerLetter != null)
      certificate.setOfferLetter(saveFile(offerLetter));
    if (terminationLetter != null)
      certificate.setTerminationLetter(saveFile(terminationLetter));

    return certificateRepo.save(certificate);
  }

  public void deleteCertificate(Long certificateId) {
    certificateRepo.deleteById(certificateId);
  }
}
