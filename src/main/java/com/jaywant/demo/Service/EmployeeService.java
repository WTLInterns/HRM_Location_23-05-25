package com.jaywant.demo.Service;

import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Repo.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

  @Autowired
  private EmployeeRepo employeeRepo;

  /**
   * Finds an employee by full name (concatenated firstName and lastName).
   */
  public Employee findByEmployeeName(String fullName) {
    return employeeRepo.findByFullName(fullName);
  }
}
