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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;


@Entity
public class Subadmin {
    

       @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String lastname;
    private String stampImg;
    private String signature;
    private String email;
    private String phoneno;
    private String password;
    private String registercompanyname;
    private String companylogo;
    private String role = "SUB_ADMIN";

    @ManyToOne
    @JoinColumn(name = "master_admin_id")
    @JsonBackReference
    private MasterAdmin masterAdmin;

    @OneToMany(mappedBy = "subadmin", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Employee> employee;
}
