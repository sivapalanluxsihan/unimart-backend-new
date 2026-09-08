package lk.ac.kln.unimart.review.mapper;

import org.springframework.stereotype.Component;

import lk.ac.kln.unimart.review.dto.ReviewResponse;
import lk.ac.kln.unimart.review.entity.Review;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getOrder().getId(),
                review.getOrder().getListing().getId(),
                review.getReviewer().getId(),
                review.getReviewer().getFullName(),
                review.getReviewee().getId(),
                review.getReviewee().getFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt());
    }
}
