package com.thanhhandsome.pythonmaster.dto.response.analytics;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FunnelResponse {
    long facebookApproaches;
    long googleFormApproaches;
    long totalApproaches;
    long registrations;
    long participants;
    BigDecimal approachToRegistrationRate;
    BigDecimal registrationToParticipationRate;
    BigDecimal overallParticipationRate;
    boolean deduplicated;
}
