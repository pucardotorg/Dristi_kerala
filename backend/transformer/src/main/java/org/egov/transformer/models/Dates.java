package org.egov.transformer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dates {

    private LocalDate filingDate;

    private LocalDate registrationDate;

    private LocalDate judgementDate;

}
