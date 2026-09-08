package lk.ac.kln.unimart.listing.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ListingRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 5000) String description,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @NotNull Long categoryId
) {
}
