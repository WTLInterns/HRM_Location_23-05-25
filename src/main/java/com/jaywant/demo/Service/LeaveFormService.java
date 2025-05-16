package com.jaywant.demo.Service;

import java.util.List;

import com.jaywant.demo.Entity.LeaveForm;

public interface LeaveFormService {
  LeaveForm createLeaveForm(int subadminId, String employeeFullName, LeaveForm leaveForm);

  List<LeaveForm> getBySubadminAndEmployeeName(int subadminId, String fullName);

  LeaveForm getLeaveFormById(int subadminId, String employeeFullName, int leaveId);

  LeaveForm updateLeaveForm(int subadminId, String employeeFullName, int leaveId, LeaveForm leaveForm);

  void deleteLeaveForm(int subadminId, String employeeFullName, int leaveId);
}
