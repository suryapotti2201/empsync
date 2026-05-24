package com.surya.empsync.repository;

import com.surya.empsync.model.AttendanceStatus;
import com.surya.empsync.model.AttendanceStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatus, Long> {
    AttendanceStatus findByAttendanceStatusEnum(AttendanceStatusEnum attendanceStatusEnum);
}
