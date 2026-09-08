package lk.ac.kln.unimart.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import lk.ac.kln.unimart.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrder_Id(Long orderId);

    // Backed by idx_review_reviewee (reviewee_id, created_at); see V2 migration.
    Page<Review> findByOrder_Listing_IdOrderByCreatedAtDesc(Long listingId, Pageable pageable);
}
