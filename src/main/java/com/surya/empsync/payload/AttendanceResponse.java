package com.surya.empsync.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private List<AttendanceDto> content;
    private Long NoOfPresentDays;
    private Long NoOfAbsentDays;
    private Long NoOfHalfDays;
}
