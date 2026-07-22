package com.cho2hand.marketplace.dto.listing;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record UpdateListingRequest(@Positive Long categoryId, @Positive Long conditionId, @Positive Long locationId,
    @Size(min=1,max=180) String title, @Size(min=1,max=5000) String description, @DecimalMin("0") @Digits(integer=15,fraction=0) BigDecimal priceAmount,
    @Size(min=1,max=255) String addressDetail) { }
