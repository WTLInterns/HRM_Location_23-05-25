package com.jaywant.demo.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
public class Certificate {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String joiningLetter;
  private String offerLetter;
  private String terminationLetter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id")
  @JsonBackReference("emp-cert")
  private Employee employee;

  // getters & setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getJoiningLetter() {
    return joiningLetter;
  }

  public void setJoiningLetter(String joiningLetter) {
    this.joiningLetter = joiningLetter;
  }

  public String getOfferLetter() {
    return offerLetter;
  }

  public void setOfferLetter(String offerLetter) {
    this.offerLetter = offerLetter;
  }

  public String getTerminationLetter() {
    return terminationLetter;
  }

  public void setTerminationLetter(String terminationLetter) {
    this.terminationLetter = terminationLetter;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }
}
