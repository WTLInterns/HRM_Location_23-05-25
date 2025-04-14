package com.jaywant.demo.Repo;

import com.jaywant.demo.Entity.Attendance;
import com.jaywant.demo.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long> {

    List<Attendance> findByEmployee(Employee employee);
}
