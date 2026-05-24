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
@Table(uniqueConstraints = {
            @UniqueConstraint(name = "uk_employee_id_date", columnNames = {"employee_id", "date"})
        },
        indexes = {
            @Index(name = "idx_pre_salary_employee_id", columnList = "employee_id")
        }
    )
public class PreSalary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preSalaryId;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    private Long amount;
    private LocalDate date;
    private boolean active;
}
