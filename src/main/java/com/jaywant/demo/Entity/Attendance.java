package com.jaywant.demo.Entity;

import java.time.Duration;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Attendance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String date;
  private String status;

  @Column(length = 500)
  private String reason;

  @Column
  private String workingHours;

  @Column
  private String breakDuration;

  @Column
  private LocalTime punchInTime;

  @Column
  private LocalTime punchOutTime;

  @Column
  private LocalTime lunchInTime; // Added lunch-in time
  @Column
  private LocalTime lunchOutTime; // Added lunch-out time

  @ManyToOne
  @JoinColumn(name = "employee_id")
  @JsonBackReference
  private Employee employee;

  // ==== GETTERS AND SETTERS ====
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public LocalTime getPunchInTime() {
    return punchInTime;
  }

  public void setPunchInTime(LocalTime punchInTime) {
    this.punchInTime = punchInTime;
  }

  public LocalTime getPunchOutTime() {
    return punchOutTime;
  }

  public void setPunchOutTime(LocalTime punchOutTime) {
    this.punchOutTime = punchOutTime;
  }

  public LocalTime getLunchInTime() {
    return lunchInTime;
  }

  public void setLunchInTime(LocalTime lunchInTime) {
    this.lunchInTime = lunchInTime;
  }

  public LocalTime getLunchOutTime() {
    return lunchOutTime;
  }

  public void setLunchOutTime(LocalTime lunchOutTime) {
    this.lunchOutTime = lunchOutTime;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public String getWorkingHours() {
    return workingHours;
  }

  public void setWorkingHours(String workingHours) {
    this.workingHours = workingHours;
  }

  public String getBreakDuration() {
    return breakDuration;
  }

  public void setBreakDuration(String breakDuration) {
    this.breakDuration = breakDuration;
  }

  public void calculateDurations() {
    if (punchInTime != null && punchOutTime != null) {
      Duration total = Duration.between(punchInTime, punchOutTime);
      Duration lunch = Duration.ZERO;

      if (lunchInTime != null && lunchOutTime != null) {
        lunch = Duration.between(lunchInTime, lunchOutTime);
        long lh = lunch.toHours();
        long lm = lunch.toMinutesPart();
        this.breakDuration = lh + "h " + lm + "m";
      }

      Duration actual = total.minus(lunch);
      long h = actual.toHours();
      long m = actual.toMinutesPart();
      this.workingHours = h + "h " + m + "m";
    }
  }
}
