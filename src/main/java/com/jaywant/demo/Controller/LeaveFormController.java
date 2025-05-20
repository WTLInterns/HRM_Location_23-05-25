package com.jaywant.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jaywant.demo.Entity.LeaveForm;
import com.jaywant.demo.Service.LeaveFormService;

@RestController
@RequestMapping("/api/leaveform")
public class LeaveFormController {

    @Autowired
    private LeaveFormService leaveFormService;

    /** Create a new leave form for an employee */
    @PostMapping("/{subadminId}/{employeeFullName}")
    public ResponseEntity<LeaveForm> createLeave(
            @PathVariable int subadminId,
            @PathVariable String employeeFullName,
            @RequestBody LeaveForm leaveForm) {

        LeaveForm created = leaveFormService
                .createLeaveForm(subadminId, employeeFullName, leaveForm);
        return ResponseEntity.ok(created);
    }

    /** Retrieve all leave forms for an employee under a subadmin */
    @GetMapping("/{subadminId}/{employeeFullName}")
    public ResponseEntity<List<LeaveForm>> getLeavesByEmployee(
            @PathVariable int subadminId,
            @PathVariable String employeeFullName) {

        List<LeaveForm> leaves = leaveFormService
                .getBySubadminAndEmployeeName(subadminId, employeeFullName);
        return ResponseEntity.ok(leaves);
    }

    /** Retrieve a specific leave form by leaveId */
    @GetMapping("/{subadminId}/{employeeFullName}/{leaveId}")
    public ResponseEntity<LeaveForm> getLeave(
            @PathVariable int subadminId,
            @PathVariable String employeeFullName,
            @PathVariable int leaveId) {

        LeaveForm leave = leaveFormService
                .getLeaveFormById(subadminId, employeeFullName, leaveId);
        return ResponseEntity.ok(leave);
    }

    /** Update a specific leave form */
    @PutMapping("/{subadminId}/{employeeFullName}/{leaveId}")
    public ResponseEntity<LeaveForm> updateLeave(
            @PathVariable int subadminId,
            @PathVariable String employeeFullName,
            @PathVariable int leaveId,
            @RequestBody LeaveForm leaveForm) {

        LeaveForm updated = leaveFormService
                .updateLeaveForm(subadminId, employeeFullName, leaveId, leaveForm);
        return ResponseEntity.ok(updated);
    }

    /** Delete a specific leave form */
    @DeleteMapping("/{subadminId}/{employeeFullName}/{leaveId}")
    public ResponseEntity<Void> deleteLeave(
            @PathVariable int subadminId,
            @PathVariable String employeeFullName,
            @PathVariable int leaveId) {

        leaveFormService.deleteLeaveForm(subadminId, employeeFullName, leaveId);
        return ResponseEntity.noContent().build();
    }

    /** Retrieve all leave forms for all employees under a specific subadmin */
    @GetMapping("/{subadminId}/all")
    public ResponseEntity<List<LeaveForm>> getLeavesBySubadmin(
            @PathVariable int subadminId) {
        List<LeaveForm> all = leaveFormService.getAllLeavesBySubadmin(subadminId);
        return ResponseEntity.ok(all);
    }
}
