package lk.ac.kln.unimart.listing.mapper;

import org.springframework.stereotype.Component;

import lk.ac.kln.unimart.listing.dto.ListingResponse;
import lk.ac.kln.unimart.listing.entity.Listing;

/**
 * Keeps entity-to-DTO mapping in one place so controllers never serialize
 * JPA entities directly (Guide 03 responsibility rules; Guide 07 Part C).
 */
@Component
public class ListingMapper {

    public ListingResponse toResponse(Listing listing) {
        return new ListingResponse(
                listing.getId(),
                listing.getSeller().getId(),
                listing.getSeller().getFullName(),
                listing.getCategory().getId(),
                listing.getCategory().getName(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getPrice(),
                listing.getStatus(),
                listing.getCreatedAt(),
                listing.getUpdatedAt());
    }
}
