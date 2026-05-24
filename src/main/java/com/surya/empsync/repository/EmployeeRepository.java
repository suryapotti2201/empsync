package com.surya.empsync.repository;

import com.surya.empsync.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByName(String name);

    Optional<Employee> findByEmployeeIdAndActive(Long employeeId, boolean active);

    List<Employee> findByActive(boolean active);
}
