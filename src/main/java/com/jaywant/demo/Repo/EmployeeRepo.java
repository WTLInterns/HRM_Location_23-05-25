package com.jaywant.demo.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jaywant.demo.Entity.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

    Employee findByEmail(String email);

    Employee findById(int empId);

    Employee findByFirstName(String firstName);

    @Query("SELECT e FROM AddEmployee e WHERE lower(e.firstName) = lower(:firstName) AND lower(e.lastName) = lower(:lastName)")
    Employee findByFirstNameAndLastName(@Param("firstName") String firstName,
            @Param("lastName") String lastName);

    @Query("SELECT e FROM AddEmployee e WHERE CONCAT(lower(e.firstName), ' ', lower(e.lastName)) = lower(:fullName)")
    Employee findByFullName(@Param("fullName") String fullName);

    List<Employee> findByCompany(String company);
}
