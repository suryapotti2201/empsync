package com.surya.empsync.service;

import com.surya.empsync.payload.PreSalaryDto;
import com.surya.empsync.payload.PreSalaryResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface PreSalaryService {
    PreSalaryDto addPreSalary(@Valid PreSalaryDto preSalaryDto, Long employeeId);

    PreSalaryResponse getEmployeeBasedPreSalary(Long employeeId);

    PreSalaryResponse getAllPreSalary();

    PreSalaryDto getDeletePreSalary(Long employeeId, String date);

    PreSalaryDto updatePreSalary(Long employeeId, @Valid PreSalaryDto preSalaryDto);

    Map<Long, Long> getPreSalary();
}
