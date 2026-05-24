package com.surya.empsync.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreSalaryDto {
    @NotNull(message = "Amount should not be null or blank")
    @Positive(message = "Amount should be positive")
    private Long amount;
    @NotNull(message = "date should not be null or blank")
    private LocalDate date;
}
