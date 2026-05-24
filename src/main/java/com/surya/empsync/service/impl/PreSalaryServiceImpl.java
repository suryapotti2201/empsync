package com.surya.empsync.service.impl;

import com.surya.empsync.exception.EmpSyncException;
import com.surya.empsync.exception.ResourceNotFoundException;
import com.surya.empsync.model.Employee;
import com.surya.empsync.model.PreSalary;
import com.surya.empsync.payload.PreSalaryDto;
import com.surya.empsync.payload.PreSalaryResponse;
import com.surya.empsync.repository.EmployeeRepository;
import com.surya.empsync.repository.PreSalaryRepository;
import com.surya.empsync.service.PreSalaryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PreSalaryServiceImpl implements PreSalaryService {

    @Autowired
    private PreSalaryRepository preSalaryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public PreSalaryDto addPreSalary(PreSalaryDto preSalaryDto, Long employeeId) {
        Employee employee = employeeRepository.findByEmployeeIdAndActive(employeeId, true).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "employeeId", employeeId)
        );
        if(preSalaryRepository.existsByEmployeeAndDate(employee, preSalaryDto.getDate())){
            throw new EmpSyncException("PreSalary already exists");
        }
        List<PreSalary> preSalaries =preSalaryRepository.getPreSalaryByEmployeeIdAndDates(employeeId,
                YearMonth.now().atDay(1), YearMonth.now().atEndOfMonth());
        if(!preSalaries.isEmpty()){
            long preSalaryAmount = preSalaries.stream().mapToLong(PreSalary::getAmount).sum();
            if(preSalaryAmount > employee.getSalary()){
                throw new EmpSyncException("PreSalary amount greater than employee salary");
            }
        }
        PreSalary preSalary = modelMapper.map(preSalaryDto, PreSalary.class);
        preSalary.setEmployee(employee);
        preSalary.setActive(true);
        PreSalary newPreSalary = preSalaryRepository.save(preSalary);
        return modelMapper.map(newPreSalary, PreSalaryDto.class);
    }

    @Override
    public PreSalaryResponse getEmployeeBasedPreSalary(Long employeeId) {
        if(!employeeRepository.existsById(employeeId)){
            throw new ResourceNotFoundException("Employee", "employeeId", employeeId);
        }
        List<PreSalary> preSalaries = preSalaryRepository.getPreSalaryByEmployeeIdAndDates(employeeId,
                YearMonth.now().atDay(1), YearMonth.now().atEndOfMonth());
        if(preSalaries.isEmpty()){
            throw new EmpSyncException("No PreSalary found");
        }
        List<PreSalaryDto> preSalaryDtos = preSalaries.stream().map(preSalary -> modelMapper.map(preSalary,
                PreSalaryDto.class)).collect(Collectors.toList());
        PreSalaryResponse preSalaryResponse = new PreSalaryResponse();
        preSalaryResponse.setContent(preSalaryDtos);
        preSalaryResponse.setTotalAmount(preSalaryDtos.stream().mapToLong(PreSalaryDto::getAmount).sum());
        return preSalaryResponse;
    }

    @Override
    public PreSalaryResponse getAllPreSalary() {
        List<PreSalary> preSalaries = preSalaryRepository.getPreSalaryByDates(
                YearMonth.now().atDay(1), YearMonth.now().atEndOfMonth());
        if(preSalaries.isEmpty()){
            throw new EmpSyncException("No PreSalary found");
        }
        List<PreSalaryDto> preSalaryDtos = preSalaries.stream().map(preSalary -> modelMapper.map(preSalary,
                PreSalaryDto.class)).collect(Collectors.toList());
        PreSalaryResponse preSalaryResponse = new PreSalaryResponse();
        preSalaryResponse.setContent(preSalaryDtos);
        preSalaryResponse.setTotalAmount(preSalaryDtos.stream().mapToLong(PreSalaryDto::getAmount).sum());
        return preSalaryResponse;
    }

    @Override
    public PreSalaryDto getDeletePreSalary(Long employeeId, String date) {
        if(!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee", "employeeId", employeeId);
        }
        PreSalary preSalary = preSalaryRepository.getPreSalaryByEmployeeIdAndDate(employeeId,
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd_MM_yyyy"))).orElseThrow(
                        () -> new ResourceNotFoundException("Employee", "employeeId", employeeId)
        );
        preSalary.setActive(false);
        PreSalary deletedPreSalary = preSalaryRepository.save(preSalary);
        return modelMapper.map(deletedPreSalary, PreSalaryDto.class);
    }

    @Override
    public PreSalaryDto updatePreSalary(Long employeeId, PreSalaryDto preSalaryDto) {
        if(!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee", "employeeId", employeeId);
        }
        PreSalary preSalary = preSalaryRepository.getPreSalaryByEmployeeIdAndDate(employeeId, preSalaryDto.getDate())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        preSalary.setAmount(preSalaryDto.getAmount());
        PreSalary updatedPreSalary = preSalaryRepository.save(preSalary);
        return modelMapper.map(updatedPreSalary, PreSalaryDto.class);
    }

    @Override
    public Map<Long, Long> getPreSalary() {
        Map<Long, List<PreSalary>> preSalaryMap = preSalaryRepository.getPreSalaryForWholeMonth(
                YearMonth.now().atDay(1), YearMonth.now().atEndOfMonth(), true)
                .stream().collect(Collectors.groupingBy(preSalary -> preSalary.getEmployee().getEmployeeId()));
        Map<Long, Long> preSalaryAmountMap = new HashMap<>();
        preSalaryMap.forEach((employeeId, preSalaryList) ->
            preSalaryAmountMap.put(employeeId, preSalaryList.stream().mapToLong(PreSalary::getAmount).sum())
        );
        return preSalaryAmountMap;
    }
}
