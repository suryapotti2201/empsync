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
public class EmpRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empRoleId;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    private EmployeeRole employeeRole;

    public EmpRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
    }
}
