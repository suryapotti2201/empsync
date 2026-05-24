package com.surya.empsync.repository;

import com.surya.empsync.model.EmpRole;
import com.surya.empsync.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpRoleRepository extends JpaRepository<EmpRole, Long> {
    Optional<EmpRole> findByEmployeeRole(EmployeeRole employeeRole);
}
