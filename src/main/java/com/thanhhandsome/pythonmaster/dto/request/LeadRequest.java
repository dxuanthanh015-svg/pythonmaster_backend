package com.thanhhandsome.pythonmaster.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeadRequest {
    @Positive
    Long thiSinhId;

    @Size(max = 100)
    String idSetQuangCao;

    @Size(max = 50)
    String idForm;

    @Size(max = 50)
    String nenTang;
}
