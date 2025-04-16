// package com.jaywant.demo.Service;

// import com.jaywant.demo.DTO.SalaryDTO;
// import com.jaywant.demo.Entity.Attendance;
// import com.jaywant.demo.Entity.Employee;
// import com.jaywant.demo.Repo.AttendanceRepo;
// import com.jaywant.demo.Repo.EmployeeRepo;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.List;

// @Service
// public class SalaryService {

//     @Autowired
//     private EmployeeRepo employeeRepo;

//     @Autowired
//     private AttendanceRepo attendanceRepo;

//     public SalaryDTO generateSalaryReport(String employeeFullName, String startDate, String endDate) {
//         // Fetch employee using full name (here, we're using subadminId = 0 as a
//         // placeholder; adjust if needed)
//         Employee employee = employeeRepo.findBySubadminIdAndFullName(0, employeeFullName);
//         if (employee == null) {
//             throw new IllegalArgumentException("Employee not found with name: " + employeeFullName);
//         }

//         double annualCtc = employee.getSalary();
//         if (annualCtc <= 0) {
//             throw new IllegalArgumentException("Invalid salary value.");
//         }

//         // Parse the provided start and end dates to LocalDate objects
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//         LocalDate start = LocalDate.parse(startDate, formatter);
//         LocalDate end = LocalDate.parse(endDate, formatter);

//         int workingDays = start.lengthOfMonth();
//         List<Attendance> attendances = attendanceRepo.findByEmployee(employee);

//         double monthlyCtc = annualCtc / 12.0;
//         double basic = monthlyCtc * 0.50;
//         double hra = basic * 0.10;
//         double da = basic * 0.53;
//         double special = basic * 0.37;

//         double totalAllowance = hra + da + special;
//         double grossSalary = basic + totalAllowance;

//         double professionalTax = 0.0;
//         double tds = 0.0;

//         int leaveTaken = 0, weekOff = 0, holidays = 0, leaveAllowed = 0, halfDays = 0;
//         double presentDays = 0;

//         // Loop through each attendance record and only consider those whose dates fall
//         // within the range
//         for (Attendance att : attendances) {
//             // Convert the attendance date string to LocalDate
//             LocalDate attDate = LocalDate.parse(att.getDate(), formatter);
//             if (!attDate.isBefore(start) && !attDate.isAfter(end)) {
//                 switch (att.getStatus().trim()) {
//                     case "Present":
//                         presentDays++;
//                         break;
//                     case "Absent":
//                         leaveTaken++;
//                         break;
//                     case "Half-Day":
//                         halfDays++;
//                         break;
//                     case "Week Off":
//                         weekOff++;
//                         break;
//                     case "Holiday":
//                         holidays++;
//                         break;
//                     case "Paid Leave":
//                         leaveAllowed++;
//                         break;
//                 }
//             }
//         }

//         presentDays += halfDays * 0.5;
//         double payableDays = presentDays + leaveAllowed + weekOff + holidays;

//         double perDayRate = monthlyCtc / workingDays;
//         double dayBasedDeduction = (workingDays - payableDays) * perDayRate;
//         double totalDeductions = dayBasedDeduction + professionalTax + tds;
//         double netPayable = Math.max(grossSalary - totalDeductions, 0);

//         // Build the SalaryDTO with salary breakdown and employee details.
//         SalaryDTO dto = new SalaryDTO();
//         dto.setUid(String.valueOf(employee.getEmpId()));
//         dto.setFirstName(employee.getFirstName());
//         dto.setLastName(employee.getLastName());
//         dto.setEmail(employee.getEmail());
//         dto.setBankName(employee.getBankName());
//         dto.setBankAccountNo(employee.getBankAccountNo());
//         dto.setBranchName(employee.getBranchName());
//         dto.setIfscCode(employee.getBankIfscCode());
//         dto.setJobRole(employee.getJobRole());

//         dto.setWorkingDays(workingDays);
//         dto.setPayableDays(payableDays);
//         dto.setLeaveTaken(leaveTaken);
//         dto.setWeekoff(weekOff);
//         dto.setHalfDay(halfDays);
//         dto.setHoliday(holidays);
//         dto.setLeaveAllowed(leaveAllowed);

//         dto.setBasic(basic);
//         dto.setHra(hra);
//         dto.setDaAllowance(da);
//         dto.setSpecialAllowance(special);
//         dto.setTotalAllowance(totalAllowance);
//         dto.setGrossSalary(grossSalary);
//         dto.setProfessionalTax(professionalTax);
//         dto.setTds(tds);
//         dto.setAdvance(dayBasedDeduction);
//         dto.setTotalDeductions(totalDeductions);
//         dto.setNetPayable(netPayable);

//         dto.setPerDaySalary(perDayRate);
//         dto.setTotalPayout(grossSalary);

//         return dto;
//     }

//     public List<Employee> getAllEmployee() {
//         return employeeRepo.findAll();
//     }
// }

package com.jaywant.demo.Service;

import com.jaywant.demo.DTO.SalaryDTO;
import com.jaywant.demo.Entity.Attendance;
import com.jaywant.demo.Entity.Employee;
import com.jaywant.demo.Repo.AttendanceRepo;
import com.jaywant.demo.Repo.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SalaryService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private AttendanceRepo attendanceRepo;

    /**
     * Generates a salary report based on the employee details and attendance
     * between dates.
     */
    public SalaryDTO generateSalaryReport(Employee employee, String startDate, String endDate) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }

        double annualCtc = employee.getSalary();
        if (annualCtc <= 0) {
            throw new IllegalArgumentException(
                    "Invalid salary value for employee: " + employee.getFirstName() + " " + employee.getLastName());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        int workingDays = start.lengthOfMonth();

        List<Attendance> attendances = attendanceRepo.findByEmployee(employee);

        double monthlyCtc = annualCtc / 12.0;
        double basic = monthlyCtc * 0.50;
        double hra = basic * 0.10;
        double da = basic * 0.53;
        double special = basic * 0.37;
        double totalAllowance = hra + da + special;
        double grossSalary = basic + totalAllowance;

        double professionalTax = 0.0;
        double tds = 0.0;

        int leaveTaken = 0, weekOff = 0, holidays = 0, leaveAllowed = 0, halfDays = 0;
        double presentDays = 0;

        for (Attendance att : attendances) {
            LocalDate attDate = LocalDate.parse(att.getDate(), formatter);
            if (!attDate.isBefore(start) && !attDate.isAfter(end)) {
                switch (att.getStatus().trim()) {
                    case "Present":
                        presentDays++;
                        break;
                    case "Absent":
                        leaveTaken++;
                        break;
                    case "Half-Day":
                        halfDays++;
                        break;
                    case "Week Off":
                        weekOff++;
                        break;
                    case "Holiday":
                        holidays++;
                        break;
                    case "Paid Leave":
                        leaveAllowed++;
                        break;
                }
            }
        }

        presentDays += halfDays * 0.5;
        double payableDays = presentDays + leaveAllowed + weekOff + holidays;
        double perDayRate = monthlyCtc / workingDays;
        double dayBasedDeduction = (workingDays - payableDays) * perDayRate;
        double totalDeductions = dayBasedDeduction + professionalTax + tds;
        double netPayable = Math.max(grossSalary - totalDeductions, 0);

        SalaryDTO dto = new SalaryDTO();
        dto.setUid(String.valueOf(employee.getEmpId()));
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setBankName(employee.getBankName());
        dto.setBankAccountNo(employee.getBankAccountNo());
        dto.setBranchName(employee.getBranchName());
        dto.setIfscCode(employee.getBankIfscCode());
        dto.setJobRole(employee.getJobRole());
        dto.setJoiningDate(employee.getJoiningDate()); // Using the corrected field name

        dto.setWorkingDays(workingDays);
        dto.setPayableDays(payableDays);
        dto.setLeaveTaken(leaveTaken);
        dto.setWeekoff(weekOff);
        dto.setHalfDay(halfDays);
        dto.setHoliday(holidays);
        dto.setLeaveAllowed(leaveAllowed);

        dto.setBasic(basic);
        dto.setHra(hra);
        dto.setDaAllowance(da);
        dto.setSpecialAllowance(special);
        dto.setTotalAllowance(totalAllowance);
        dto.setGrossSalary(grossSalary);
        dto.setProfessionalTax(professionalTax);
        dto.setTds(tds);
        dto.setAdvance(dayBasedDeduction);
        dto.setTotalDeductions(totalDeductions);
        dto.setNetPayable(netPayable);

        dto.setPerDaySalary(perDayRate);
        dto.setTotalPayout(grossSalary);

        return dto;
    }

    public List<Employee> getAllEmployee() {
        return employeeRepo.findAll();
    }
}
