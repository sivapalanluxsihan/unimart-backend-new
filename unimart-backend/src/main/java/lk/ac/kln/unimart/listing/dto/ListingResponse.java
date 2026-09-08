package lk.ac.kln.unimart.listing.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lk.ac.kln.unimart.listing.entity.ListingStatus;

public record ListingResponse(
        Long id,
        Long sellerId,
        String sellerName,
        Long categoryId,
        String categoryName,
        String title,
        String description,
        BigDecimal price,
        ListingStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
