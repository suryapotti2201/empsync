package com.surya.empsync.controller;

import com.surya.empsync.payload.AttendanceDto;
import com.surya.empsync.payload.AttendanceResponse;
import com.surya.empsync.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/{employeeId}")
    public ResponseEntity<AttendanceDto> noteAttendance(@Valid @RequestBody AttendanceDto attendanceDto,
            @PathVariable("employeeId") Long employeeId){
        AttendanceDto notedAttendance= attendanceService.noteAttendance(attendanceDto, employeeId);
        return new ResponseEntity<>(notedAttendance, HttpStatus.CREATED);
    }

    @GetMapping("/{employeeId}/{month}/{year}")
    public ResponseEntity<AttendanceResponse> getEmployeeAttendanceMonthWise(@PathVariable("employeeId") Long employeeId,
            @PathVariable("month") Integer month, @PathVariable("year") Integer year){
        AttendanceResponse attendanceResponse = attendanceService.getEmployeeAttendanceMonthWise(employeeId, month, year);
        return new ResponseEntity<>(attendanceResponse, HttpStatus.OK);
    }

    @GetMapping("/{employeeId}/{date}")
    public ResponseEntity<AttendanceDto> getEmployeeAttendance(@PathVariable("employeeId") Long employeeId,
            @PathVariable("date") String date){
        AttendanceDto attendanceDto = attendanceService.getEmployeeAttendance(employeeId, date);
        return new ResponseEntity<>(attendanceDto, HttpStatus.OK);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<AttendanceDto> updateEmployeeAttendance(@Valid @RequestBody AttendanceDto attendanceDto,
            @PathVariable("employeeId") Long employeeId){
        AttendanceDto updatedAttendanceDto = attendanceService.updateEmployeeAttendance(attendanceDto, employeeId);
        return new ResponseEntity<>(updatedAttendanceDto, HttpStatus.OK);
    }
}