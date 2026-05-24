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
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    @Column(name = "employee_name")
    private String name;
    private String phoneNumber;
    private boolean active;
    private LocalDate dateOfJoining;
    @ManyToOne
    @JoinColumn(name = "emp_role_id")
    private EmpRole role;
    private Long salary;
}
