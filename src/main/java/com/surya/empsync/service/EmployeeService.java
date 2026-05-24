package com.surya.empsync.service;

import com.surya.empsync.model.Employee;
import com.surya.empsync.payload.EmployeeDto;
import com.surya.empsync.payload.EmployeeResponse;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
    EmployeeDto getEmployee(Long employeeId);

    EmployeeResponse getAllEmployees(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto deleteEmployee(Long employeeId);

    EmployeeDto updateEmployee(EmployeeDto employeeDto, Long employeeId);

    Map<Long, Employee> getAllEmployees();
}
