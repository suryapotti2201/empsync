package com.surya.empsync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
            @Index(name = "idx_attendance_employee_id", columnList = "employee_id")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_date_employee_id", columnNames = {"employee_id", "date"})
        }
    )
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "att_status_id")
    private AttendanceStatus status;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
