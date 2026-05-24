package com.surya.empsync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attStatusId;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    private AttendanceStatusEnum attendanceStatusEnum;

    public AttendanceStatus(AttendanceStatusEnum attendanceStatusEnum) {
        this.attendanceStatusEnum = attendanceStatusEnum;
    }
}
