package com.surya.empsync.service.impl;

import com.surya.empsync.exception.EmpSyncException;
import com.surya.empsync.exception.ResourceNotFoundException;
import com.surya.empsync.model.Attendance;
import com.surya.empsync.model.AttendanceStatus;
import com.surya.empsync.model.AttendanceStatusEnum;
import com.surya.empsync.model.Employee;
import com.surya.empsync.payload.AttendanceDto;
import com.surya.empsync.payload.AttendanceResponse;
import com.surya.empsync.repository.AttendanceRepository;
import com.surya.empsync.repository.AttendanceStatusRepository;
import com.surya.empsync.repository.EmployeeRepository;
import com.surya.empsync.service.AttendanceService;
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
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AttendanceStatusRepository attendanceStatusRepository;

    @Override
    public AttendanceDto noteAttendance(AttendanceDto attendanceDto, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "employeeId", employeeId)
        );
        if(!LocalDate.now().isEqual(LocalDate.parse(attendanceDto.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))){
            throw new EmpSyncException("Attendance date should be today date");
        }
        Attendance attendance = new Attendance();
        attendance.setDate(LocalDate.parse(attendanceDto.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        attendance.setEmployee(employee);
        AttendanceStatus attendanceStatus = switch (attendanceDto.getStatus()) {
            case "leave" -> attendanceStatusRepository.findByAttendanceStatusEnum(AttendanceStatusEnum.LEAVE);
            case "present" -> attendanceStatusRepository.findByAttendanceStatusEnum(AttendanceStatusEnum.PRESENT);
            case "half-day" -> attendanceStatusRepository.findByAttendanceStatusEnum(AttendanceStatusEnum.HALF_DAY);
            default -> throw new EmpSyncException("Attendance status not recognized");
        };
        attendance.setStatus(attendanceStatus);
        if(attendanceRepository.existsByEmployeeAndDate(employee, attendance.getDate())){
            throw new EmpSyncException("Attendance already exists for the given employee");
        }
        Attendance noteAttendance = attendanceRepository.save(attendance);
        return modelMapper.map(noteAttendance, AttendanceDto.class);
    }

    @Override
    public AttendanceResponse getEmployeeAttendanceMonthWise(Long employeeId, Integer month, Integer year) {
        if(!employeeRepository.existsById(employeeId)){
            throw new ResourceNotFoundException("Employee", "employeeId", employeeId);
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        List<Attendance> attendanceList;
        if(YearMonth.now().isAfter(yearMonth)){
            attendanceList = attendanceRepository.getEmployeeAttendance(employeeId, LocalDate.now().withDayOfMonth(1),
                    yearMonth.atEndOfMonth());
        }else if (YearMonth.now().isAfter(yearMonth)){
            throw new EmpSyncException("Can't fetch attendance for future month");
        }else {
            attendanceList = attendanceRepository.getEmployeeAttendance(employeeId, LocalDate.now().withDayOfMonth(1),
                    LocalDate.now());
        }
        if(attendanceList.isEmpty()){
            throw new EmpSyncException("Attendance not found");
        }
        AttendanceResponse attendanceResponse = new AttendanceResponse();
        attendanceResponse.setContent(
                attendanceList.stream().map(attendance -> modelMapper.map(attendance, AttendanceDto.class))
                        .collect(Collectors.toList()));
        Map<AttendanceStatus, Long> counts = attendanceList.stream().collect(Collectors.groupingBy(Attendance::getStatus,
                Collectors.counting()));
        for(Map.Entry<AttendanceStatus, Long> entry : counts.entrySet()){
            switch (entry.getKey().getAttendanceStatusEnum()){
                case AttendanceStatusEnum.LEAVE:
                    attendanceResponse.setNoOfAbsentDays(entry.getValue());
                    break;
                case AttendanceStatusEnum.PRESENT:
                    attendanceResponse.setNoOfPresentDays(entry.getValue());
                    break;
                case AttendanceStatusEnum.HALF_DAY:
                    attendanceResponse.setNoOfHalfDays(entry.getValue());
                    break;
            }
        }
        return attendanceResponse;
    }

    @Override
    public AttendanceDto getEmployeeAttendance(Long employeeId, String date) {
        if(!employeeRepository.existsById(employeeId)){
            throw new ResourceNotFoundException("Employee", "employeeId", employeeId);
        }
        if(LocalDate.now().isBefore(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")))){
            throw new EmpSyncException("Attendance date cannot be future date");
        }
        Attendance attendance = attendanceRepository.getEmployeeAttendance(employeeId, LocalDate.parse(date,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"))).orElseThrow(
                () -> new ResourceNotFoundException("Attendance", "Date", date)
        );
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setDate(attendance.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        attendanceDto.setStatus(attendance.getStatus().toString());
        return attendanceDto;
    }

    @Override
    public AttendanceDto updateEmployeeAttendance(AttendanceDto attendanceDto, Long employeeId) {
        if(!employeeRepository.existsById(employeeId)){
            throw new ResourceNotFoundException("Employee", "employeeId", employeeId);
        }
        if(LocalDate.now().isBefore(LocalDate.parse(attendanceDto.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))){
            throw new EmpSyncException("Attendance date cannot be future date");
        }
        Attendance attendanceDb = attendanceRepository.getEmployeeAttendance(employeeId, LocalDate.parse(attendanceDto.getDate(),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"))).orElseThrow(
                () -> new ResourceNotFoundException("Attendance", "Date", attendanceDto.getDate())
        );
        AttendanceStatus attendanceStatus = switch (attendanceDto.getStatus()) {
            case "leave" -> attendanceStatusRepository.findByAttendanceStatusEnum(AttendanceStatusEnum.LEAVE);
            case "present" -> attendanceStatusRepository.findByAttendanceStatusEnum(AttendanceStatusEnum.PRESENT);
            case "half-day" -> attendanceStatusRepository.findByAttendanceStatusEnum(AttendanceStatusEnum.HALF_DAY);
            default -> throw new EmpSyncException("Attendance status not recognized");
        };
        attendanceDb.setStatus(attendanceStatus);
        Attendance updatedAttendance = attendanceRepository.save(attendanceDb);
        return modelMapper.map(updatedAttendance, AttendanceDto.class);
    }

    @Override
    public Map<AttendanceStatus, Long> getEmployeeAttendanceCount(Long employeeId) {
        List<Attendance> attendanceList = attendanceRepository.getEmployeeAttendance(employeeId,
                LocalDate.now().withDayOfMonth(1), LocalDate.now());
        return attendanceList.stream().filter(attendance ->
                        !attendance.getStatus().getAttendanceStatusEnum().equals(AttendanceStatusEnum.PRESENT))
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));
    }

    @Override
    public Map<Long, AttendanceResponse> getAllEmployeesAttendanceMap() {
        Map<Long, List<Attendance>> attendanceMap = attendanceRepository.getAllEmployeesAttendance(
                LocalDate.now().minusMonths(1).withDayOfMonth(1),
                        YearMonth.now().minusMonths(1).atEndOfMonth()).stream()
                .collect(Collectors.groupingBy(attendance -> attendance.getEmployee().getEmployeeId()));
        Map<Long, AttendanceResponse> attendanceCountMap = new HashMap<>();
        for (Map.Entry<Long, List<Attendance>> entry : attendanceMap.entrySet()) {
            AttendanceResponse attendanceResponse = getAttendanceResponse(entry);
            attendanceCountMap.put(entry.getKey(), attendanceResponse);
        }
        return attendanceCountMap;
    }


    private static AttendanceResponse getAttendanceResponse(Map.Entry<Long, List<Attendance>> entry) {
        Long leaves = 0L;
        Long halfDay = 0L;
        Long presentDay = 0L;
        for (Attendance attendance : entry.getValue()) {
            switch (attendance.getStatus().getAttendanceStatusEnum()) {
                case AttendanceStatusEnum.LEAVE:
                    leaves++;
                    break;
                case AttendanceStatusEnum.HALF_DAY:
                    halfDay++;
                    break;
                case AttendanceStatusEnum.PRESENT:
                    presentDay++;
                    break;
            }
        }
        AttendanceResponse attendanceResponse = new AttendanceResponse();
        attendanceResponse.setNoOfHalfDays(halfDay);
        attendanceResponse.setNoOfAbsentDays(leaves);
        attendanceResponse.setNoOfPresentDays(presentDay);
        return attendanceResponse;
    }
}