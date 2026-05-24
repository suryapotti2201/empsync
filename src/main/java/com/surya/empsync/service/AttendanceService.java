package com.surya.empsync.service;

import com.surya.empsync.model.AttendanceStatus;
import com.surya.empsync.model.AttendanceStatusEnum;
import com.surya.empsync.payload.AttendanceDto;
import com.surya.empsync.payload.AttendanceResponse;
import jakarta.validation.Valid;

import java.util.Map;

public interface AttendanceService {
    AttendanceDto noteAttendance(@Valid AttendanceDto attendanceDto, Long employeeId);

    AttendanceResponse getEmployeeAttendanceMonthWise(Long employeeId, Integer month, Integer year);

    AttendanceDto getEmployeeAttendance(Long employeeId, String date);

    AttendanceDto updateEmployeeAttendance(@Valid AttendanceDto attendanceDto, Long employeeId);

    Map<AttendanceStatus, Long> getEmployeeAttendanceCount(Long employeeId);

    Map<Long, AttendanceResponse> getAllEmployeesAttendanceMap();
}
