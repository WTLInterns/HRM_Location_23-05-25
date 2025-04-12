package com.jaywant.demo.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
public class MasterAdmin {

    
   @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String email;
    private String password;
    private String roll = "MASTER_ADMIN";
    private String profileImg;
    private long mobileno;

    @OneToMany(mappedBy = "masterAdmin", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Subadmin> subadmin;
}

