package com.jaywant.demo.Repo;

import com.jaywant.demo.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

    /**
     * Finds an employee by the subadmin’s id and the full name (firstName + ' ' + lastName).
     * Adjust the concatenation as needed if you store or require a middle name.
     */
    @Query("SELECT e FROM Employee e WHERE e.subadmin.id = :subadminId AND CONCAT(e.firstName, ' ', e.lastName) = :fullName")
    Employee findBySubadminIdAndFullName(int subadminId, String fullName);

    Optional<Employee> findByEmail(String email);
}
