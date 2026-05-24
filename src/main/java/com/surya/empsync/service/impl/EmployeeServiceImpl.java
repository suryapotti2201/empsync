package com.surya.empsync.service.impl;

import com.surya.empsync.constants.SystemConstants;
import com.surya.empsync.exception.EmpSyncException;
import com.surya.empsync.exception.ResourceNotFoundException;
import com.surya.empsync.model.*;
import com.surya.empsync.payload.EmployeeDto;
import com.surya.empsync.payload.EmployeeResponse;
import com.surya.empsync.repository.EmpRoleRepository;
import com.surya.empsync.repository.EmployeeRepository;
import com.surya.empsync.service.AttendanceService;
import com.surya.empsync.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AttendanceService  attendanceService;

    @Autowired
    private EmpRoleRepository empRoleRepository;

    @Override
    public EmployeeDto getEmployee(Long employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if(employee.isPresent()) {
            EmployeeDto employeeDto = modelMapper.map(employee.get(), EmployeeDto.class);
            Map<AttendanceStatus, Long> count = attendanceService.getEmployeeAttendanceCount(employeeId);
            for(Map.Entry<AttendanceStatus, Long> entry : count.entrySet()){
                switch (entry.getKey().getAttendanceStatusEnum()){
                    case AttendanceStatusEnum.LEAVE:
                        employeeDto.setLeavesTaken(entry.getValue());
                        break;
                    case AttendanceStatusEnum.PRESENT:
                        break;
                    case AttendanceStatusEnum.HALF_DAY:
                        employeeDto.setHalfDaysTaken(entry.getValue());
                        break;
                }
            }
            return employeeDto;
        }else {
            throw new ResourceNotFoundException("Employee", "employeeId", employeeId);
        }
    }

    @Override
    public EmployeeResponse getAllEmployees(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase(SystemConstants.ASC)? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Employee> employeePage = employeeRepository.findAll(pageDetails);
        List<Employee> employeeList = employeePage.getContent();
        if(employeeList.isEmpty()) {
            throw new EmpSyncException("No employees found");
        }
        List<EmployeeDto> employeeDtoList = employeeList.stream().map(employee -> {
                    EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
                    Map<AttendanceStatus, Long> count = attendanceService.getEmployeeAttendanceCount(
                            employee.getEmployeeId());
                    for(Map.Entry<AttendanceStatus, Long> entry : count.entrySet()){
                        switch (entry.getKey().getAttendanceStatusEnum()){
                            case AttendanceStatusEnum.LEAVE:
                                employeeDto.setLeavesTaken(entry.getValue());
                                break;
                            case AttendanceStatusEnum.PRESENT:
                                break;
                            case AttendanceStatusEnum.HALF_DAY:
                                employeeDto.setHalfDaysTaken(entry.getValue());
                                break;
                        }
                    }
                    return employeeDto;
                }).collect(Collectors.toList());
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setContent(employeeDtoList);
        employeeResponse.setPageNumber(employeePage.getNumber());
        employeeResponse.setPageSize(employeePage.getSize());
        employeeResponse.setTotalElements(employeePage.getTotalElements());
        employeeResponse.setTotalPages(employeePage.getTotalPages());
        employeeResponse.setLastPage(employeePage.isLast());
        return employeeResponse;
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        if(employeeRepository.existsByName(employee.getName())){
            throw new EmpSyncException("Employee with the name " + employee.getName() + " already exists !!!");
        }
        EmpRole empRole = switch (employeeDto.getRole()) {
            case "cleaner" -> empRoleRepository.findByEmployeeRole(EmployeeRole.CLEANER).orElseThrow(
                    () -> new ResourceNotFoundException("Employee role", "employeeRole", employeeDto.getRole())
            );
            case "packer" -> empRoleRepository.findByEmployeeRole(EmployeeRole.PACKER).orElseThrow(
                    () -> new ResourceNotFoundException("Employee role", "employeeRole", employeeDto.getRole())
            );
            case "sales" -> empRoleRepository.findByEmployeeRole(EmployeeRole.SALES).orElseThrow(
                    () -> new ResourceNotFoundException("Employee role", "employeeRole", employeeDto.getRole())
            );
            default -> throw new EmpSyncException("Invalid role");
        };
        employee.setRole(empRole);
        employee.setActive(true);
        employee = employeeRepository.save(employee);
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @Override
    public EmployeeDto deleteEmployee(Long employeeId) {
        Optional<Employee> employee = employeeRepository.findByEmployeeIdAndActive(employeeId, true);
        if(employee.isPresent()) {
            Employee employeeToDelete = employee.get();
            employeeToDelete.setActive(false);
            Employee updatedEmployee = employeeRepository.save(employeeToDelete);
            return modelMapper.map(updatedEmployee, EmployeeDto.class);
        }else {
            throw new ResourceNotFoundException("Employee", "employeeId", employeeId);
        }
    }

    @Override
    public EmployeeDto updateEmployee(EmployeeDto employeeDto, Long employeeId) {
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        Employee employeeFromDb = employeeRepository.findByEmployeeIdAndActive(employeeId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        employeeFromDb.setSalary(employee.getSalary());
        employeeFromDb.setPhoneNumber(employee.getPhoneNumber());
        EmpRole empRole = switch (employeeDto.getRole()) {
            case "cleaner" -> empRoleRepository.findByEmployeeRole(EmployeeRole.CLEANER).orElseThrow(
                    () -> new ResourceNotFoundException("Employee role", "employeeRole", employeeDto.getRole())
            );
            case "packer" -> empRoleRepository.findByEmployeeRole(EmployeeRole.PACKER).orElseThrow(
                    () -> new ResourceNotFoundException("Employee role", "employeeRole", employeeDto.getRole())
            );
            case "sales" -> empRoleRepository.findByEmployeeRole(EmployeeRole.SALES).orElseThrow(
                    () -> new ResourceNotFoundException("Employee role", "employeeRole", employeeDto.getRole())
            );
            default -> throw new EmpSyncException("Invalid role");
        };
        employeeFromDb.setRole(empRole);
        Employee updatedEmployee = employeeRepository.save(employeeFromDb);
        return modelMapper.map(updatedEmployee, EmployeeDto.class);
    }

    @Override
    public Map<Long, Employee> getAllEmployees() {
        return employeeRepository.findByActive(true).stream().collect(Collectors.toMap(
                Employee::getEmployeeId, employee -> employee));
    }
}