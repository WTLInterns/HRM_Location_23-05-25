package com.jaywant.demo.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
    private String password;
    private String role = "EMPLOYEE";

    @ManyToOne
    @JoinColumn(name = "subadmin_id")
    @JsonBackReference
    private Subadmin subadmin;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Attendance> attendance;
    
}
