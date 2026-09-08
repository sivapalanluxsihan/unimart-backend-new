package lk.ac.kln.unimart.listing.service;

import org.springframework.data.jpa.domain.Specification;

import lk.ac.kln.unimart.listing.entity.Listing;
import lk.ac.kln.unimart.listing.entity.ListingStatus;

/**
 * Composable filters for GET /listings. See Guide 07 Part D ("Implement
 * GET /listings with Pageable and a Specification for q, categoryId and
 * status").
 */
public final class ListingSpecifications {

    private ListingSpecifications() {
    }

    public static Specification<Listing> withFilters(String q, Long categoryId, ListingStatus status) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like)));
            }
            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category").get("id"), categoryId));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            } else {
                // Default: never surface archived listings in the public browse view.
                predicate = cb.and(predicate, cb.notEqual(root.get("status"), ListingStatus.ARCHIVED));
            }
            return predicate;
        };
    }
}
