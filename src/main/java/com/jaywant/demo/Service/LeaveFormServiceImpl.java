package com.jaywant.demo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Entity.LeaveForm;
import com.jaywant.demo.Entity.Subadmin;
import com.jaywant.demo.Repo.EmployeeRepo;
import com.jaywant.demo.Repo.LeaveFormRepository;
import com.jaywant.demo.Repo.SubAdminRepo;

@Service
public class LeaveFormServiceImpl implements LeaveFormService {

  @Autowired
  private LeaveFormRepository leaveRepo;

  @Autowired
  private SubAdminRepo subadminRepo;

  @Autowired
  private EmployeeRepo employeeRepo;

  @Override
  public LeaveForm createLeaveForm(int subadminId, String employeeFullName, LeaveForm leaveForm) {
    Subadmin subadmin = subadminRepo.findById(subadminId)
        .orElseThrow(() -> new RuntimeException("Subadmin not found"));

    Employee employee = employeeRepo
        .findBySubadminAndFirstNameAndLastNameIgnoreCase(
            subadmin,
            employeeFullName.split(" ", 2)[0],
            employeeFullName.split(" ", 2)[1]);
    if (employee == null) {
      throw new RuntimeException("Employee not found");
    }

    leaveForm.setSubadmin(subadmin);
    leaveForm.setEmployee(employee);
    return leaveRepo.save(leaveForm);
  }

  @Override
  public List<LeaveForm> getBySubadminAndEmployeeName(int subadminId, String fullName) {
    return leaveRepo.findBySubadmin_IdAndEmployee_FullNameIgnoreCase(subadminId, fullName);
  }

  @Override
  public LeaveForm getLeaveFormById(int subadminId, String employeeFullName, int leaveId) {
    LeaveForm leave = leaveRepo.findById(leaveId)
        .orElseThrow(() -> new RuntimeException("LeaveForm not found"));

    // verify it’s the right subadmin & employee
    if (leave.getSubadmin().getId() != subadminId ||
        !leave.getEmployee().getFullName().equalsIgnoreCase(employeeFullName)) {
      throw new RuntimeException("Unauthorized access");
    }
    return leave;
  }

  @Override
  public LeaveForm updateLeaveForm(int subadminId, String employeeFullName, int leaveId, LeaveForm updatedForm) {
    LeaveForm existing = getLeaveFormById(subadminId, employeeFullName, leaveId);
    existing.setReason(updatedForm.getReason());
    existing.setFromDate(updatedForm.getFromDate());
    existing.setToDate(updatedForm.getToDate());
    existing.setStatus(updatedForm.getStatus());
    // subadmin & employee remain unchanged
    return leaveRepo.save(existing);
  }

  @Override
  public void deleteLeaveForm(int subadminId, String employeeFullName, int leaveId) {
    LeaveForm existing = getLeaveFormById(subadminId, employeeFullName, leaveId);
    leaveRepo.delete(existing);
  }
}
