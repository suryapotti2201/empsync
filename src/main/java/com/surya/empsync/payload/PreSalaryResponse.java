package com.surya.empsync.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreSalaryResponse {
    private List<PreSalaryDto> content;
    private Long totalAmount;
}
