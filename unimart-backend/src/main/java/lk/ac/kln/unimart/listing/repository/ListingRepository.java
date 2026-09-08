package lk.ac.kln.unimart.listing.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lk.ac.kln.unimart.listing.entity.Listing;
import lk.ac.kln.unimart.listing.entity.ListingStatus;

public interface ListingRepository extends JpaRepository<Listing, Long>,
        JpaSpecificationExecutor<Listing> {

    // Backed by idx_listing_search (status, category_id, created_at); see
    // Guide 01 section 4 and V1 migration.
    Page<Listing> findByStatusAndCategory_Id(ListingStatus status, Long categoryId, Pageable pageable);

    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    // Guide 07 Part D: GET /listings/{id} and ownership checks must not
    // surface archived listings as if they still existed.
    Optional<Listing> findByIdAndStatusNot(Long id, ListingStatus status);
}
