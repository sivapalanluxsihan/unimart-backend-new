package lk.ac.kln.unimart.review.dto;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Long orderId,
        Long listingId,
        Long reviewerId,
        String reviewerName,
        Long revieweeId,
        String revieweeName,
        Integer rating,
        String comment,
        Instant createdAt,
        Instant updatedAt
) {
}
