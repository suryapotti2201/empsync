package com.surya.empsync.controller;

import com.surya.empsync.constants.SystemConstants;
import com.surya.empsync.payload.EmployeeDto;
import com.surya.empsync.payload.EmployeeResponse;
import com.surya.empsync.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable("employeeId") Long employeeId) {
        EmployeeDto employeeDto = employeeService.getEmployee(employeeId);
        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping
    public ResponseEntity<EmployeeResponse> getAllEmployees(
            @RequestParam(value = "pageNumber", defaultValue = SystemConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = SystemConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = SystemConstants.SORT_EMPLOYEE_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = SystemConstants.SORT_DIR) String sortOrder) {
        EmployeeResponse employeeDtos = employeeService.getAllEmployees(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(employeeDtos);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto newEmployeeDto = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(newEmployeeDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> deleteEmployee(@PathVariable("employeeId") Long employeeId) {
        EmployeeDto deleteEmployee = employeeService.deleteEmployee(employeeId);
        return new ResponseEntity<>(deleteEmployee, HttpStatus.OK);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> updateEmployee(@Valid @RequestBody EmployeeDto employeeDto,
                                                      @PathVariable("employeeId") Long employeeId) {
        EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(employeeDto, employeeId);
        return new ResponseEntity<>(updatedEmployeeDto, HttpStatus.OK);
    }
}