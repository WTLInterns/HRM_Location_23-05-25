package com.jaywant.demo.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaywant.demo.Entity.LeaveForm;

@Repository
public interface LeaveFormRepository extends JpaRepository<LeaveForm, Integer> {

  /**
   * Finds all leave forms for a given subadmin id and employee full name
   * (case‑insensitive).
   */
  List<LeaveForm> findBySubadmin_IdAndEmployee_FullNameIgnoreCase(int subadminId, String fullName);
}
