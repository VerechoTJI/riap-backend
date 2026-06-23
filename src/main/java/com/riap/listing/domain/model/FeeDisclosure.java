package com.riap.listing.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FeeDisclosure {
    private BigDecimal rent;
    private BigDecimal deposit;
    private BigDecimal managementFee;
    private String waterElectricityRules;
}
