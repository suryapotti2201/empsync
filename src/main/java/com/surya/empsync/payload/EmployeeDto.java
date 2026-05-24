package com.surya.empsync.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    @NotBlank(message = "Employee name should not be null or blank")
    @Size(min = 4, max = 50, message = "Size of Employee name must be between 4 and 50 characters")
    private String name;

    @NotBlank(message = "Phone no should not be null or blank")
    private String phoneNumber;

    @PastOrPresent(message = "Date of Joining can't be future date")
    private LocalDate dateOfJoining;

    @NotBlank(message = "Role should not be null or blank")
    private String role;

    @NotNull(message = "Salary should not be null or blank")
    @Positive(message = "Salary should be positive")
    private Long salary;

    private Long leavesTaken;

    private Long halfDaysTaken;
}
