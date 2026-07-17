package com.cho2hand.marketplace.dto.listing;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CreateListingRequest(@NotNull @Positive Long categoryId, @NotNull @Positive Long conditionId, @NotNull @Positive Long locationId,
    @NotBlank @Size(max=180) String title, @NotBlank @Size(max=5000) String description, @NotNull @DecimalMin("0") @Digits(integer=15,fraction=0) BigDecimal priceAmount) { }
