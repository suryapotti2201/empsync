package com.surya.empsync.repository;

import com.surya.empsync.model.Employee;
import com.surya.empsync.model.PreSalary;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PreSalaryRepository extends JpaRepository<PreSalary,Long> {
    boolean existsByEmployeeAndDate(Employee employee, @NotNull(message = "date should not be null or blank") LocalDate date);

    @Query("SELECT ps FROM PreSalary ps WHERE ps.employee.id = :employeeId AND ps.date BETWEEN :startDate AND :endDate")
    List<PreSalary> getPreSalaryByEmployeeIdAndDates(Long employeeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT ps FROM PreSalary ps WHERE ps.date BETWEEN :startDate AND :endDate")
    List<PreSalary> getPreSalaryByDates(LocalDate startDate, LocalDate endDate);

    @Query("SELECT ps FROM PreSalary ps WHERE ps.employee.id = :employeeId AND ps.date = :date")
    Optional<PreSalary> getPreSalaryByEmployeeIdAndDate(Long employeeId, LocalDate date);

    @Query("SELECT ps FROM PreSalary ps WHERE ps.active = :active AND  ps.date BETWEEN :startDate AND :endDate")
    List<PreSalary> getPreSalaryForWholeMonth(LocalDate startDate, LocalDate endDate, Boolean active);
}
