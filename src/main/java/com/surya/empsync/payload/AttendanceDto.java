package com.surya.empsync.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {

    @NotNull(message = "Date should not be null or blank")
    private String date;
    @NotNull(message = "Status should not be null or blank")
    private String status;

}
