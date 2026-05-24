package com.surya.empsync.controller;

import com.surya.empsync.payload.PreSalaryDto;
import com.surya.empsync.payload.PreSalaryResponse;
import com.surya.empsync.service.PreSalaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/preSalary")
public class PreSalaryController {

    @Autowired
    private PreSalaryService preSalaryService;

    @PostMapping("/{employeeId}")
    public ResponseEntity<PreSalaryDto> addPreSalary(@Valid @RequestBody PreSalaryDto preSalaryDto,
            @PathVariable("employeeId") Long employeeId) {
        PreSalaryDto dto = preSalaryService.addPreSalary(preSalaryDto, employeeId);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<PreSalaryResponse> getEmployeeBasedPreSalary(@PathVariable("employeeId") Long employeeId) {
        PreSalaryResponse preSalaryResponse = preSalaryService.getEmployeeBasedPreSalary(employeeId);
        return new ResponseEntity<>(preSalaryResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PreSalaryResponse> getAllPreSalary() {
        PreSalaryResponse preSalaryResponse = preSalaryService.getAllPreSalary();
        return new ResponseEntity<>(preSalaryResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{employeeId}/{date}")
    public ResponseEntity<PreSalaryDto> getDeletePreSalary(@PathVariable("employeeId") Long employeeId,
            @PathVariable("date") String date) {
        PreSalaryDto preSalaryDto = preSalaryService.getDeletePreSalary(employeeId, date);
        return new ResponseEntity<>(preSalaryDto, HttpStatus.OK);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<PreSalaryDto> updatePreSalary(@Valid @RequestBody PreSalaryDto preSalaryDto,
            @PathVariable("employeeId") Long employeeId){
        PreSalaryDto updatedPreSalaryDto = preSalaryService.updatePreSalary(employeeId, preSalaryDto);
        return new ResponseEntity<>(updatedPreSalaryDto, HttpStatus.OK);
    }
}
