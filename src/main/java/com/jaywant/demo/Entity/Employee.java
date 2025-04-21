package com.jaywant.demo.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int empId;

  private String firstName;
  private String lastName;
  private String email;
  private Long phone;
  private String aadharNo;
  private String panCard;
  private String education;
  private String bloodGroup;
  private String jobRole;
  private String gender;
  private String address;
  private String birthDate;
  private String joiningDate;
  private String status;
  private String bankName;
  private String bankAccountNo;
  private String bankIfscCode;
  private String branchName;
  private Long salary;
  private String empimg;

  public String getEmpimg() {
    return empimg;
  }

  public void setEmpimg(String empimg) {
    this.empimg = empimg;
  }

  public String getAdharimg() {
    return adharimg;
  }

  public void setAdharimg(String adharimg) {
    this.adharimg = adharimg;
  }

  public String getPanimg() {
    return panimg;
  }

  public void setPanimg(String panimg) {
    this.panimg = panimg;
  }

  private String adharimg;
  private String panimg;

  private String role = "EMPLOYEE";

  @ManyToOne
  @JoinColumn(name = "subadmin_id")
  // @JsonBackReference
  @JsonIgnoreProperties({ "employees" })
  private Subadmin subadmin;

  @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<Attendance> attendance;

  @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Certificate> certificates = new ArrayList<>();

  public List<Certificate> getCertificates() {
    return certificates;
  }

  public void setCertificates(List<Certificate> certificates) {
    this.certificates = certificates;
  }

  public int getEmpId() {
    return empId;
  }

  public void setEmpId(int empId) {
    this.empId = empId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getPhone() {
    return phone;
  }

  public void setPhone(Long phone) {
    this.phone = phone;
  }

  public String getAadharNo() {
    return aadharNo;
  }

  public void setAadharNo(String aadharNo) {
    this.aadharNo = aadharNo;
  }

  public String getPanCard() {
    return panCard;
  }

  public void setPanCard(String panCard) {
    this.panCard = panCard;
  }

  public String getEducation() {
    return education;
  }

  public void setEducation(String education) {
    this.education = education;
  }

  public String getBloodGroup() {
    return bloodGroup;
  }

  public void setBloodGroup(String bloodGroup) {
    this.bloodGroup = bloodGroup;
  }

  public String getJobRole() {
    return jobRole;
  }

  public void setJobRole(String jobRole) {
    this.jobRole = jobRole;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public String getJoiningDate() {
    return joiningDate;
  }

  public void setJoiningDate(String joiningDate) {
    this.joiningDate = joiningDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getBankAccountNo() {
    return bankAccountNo;
  }

  public void setBankAccountNo(String bankAccountNo) {
    this.bankAccountNo = bankAccountNo;
  }

  public String getBankIfscCode() {
    return bankIfscCode;
  }

  public void setBankIfscCode(String bankIfscCode) {
    this.bankIfscCode = bankIfscCode;
  }

  public String getBranchName() {
    return branchName;
  }

  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }

  public Long getSalary() {
    return salary;
  }

  public void setSalary(Long salary) {
    this.salary = salary;
  }

  // public String getPassword() {
  // return password;
  // }

  // public void setPassword(String password) {
  // this.password = password;
  // }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Subadmin getSubadmin() {
    return subadmin;
  }

  public void setSubadmin(Subadmin subadmin) {
    this.subadmin = subadmin;
  }

  public List<Attendance> getAttendance() {
    return attendance;
  }

  public void setAttendance(List<Attendance> attendance) {
    this.attendance = attendance;
  }

}
