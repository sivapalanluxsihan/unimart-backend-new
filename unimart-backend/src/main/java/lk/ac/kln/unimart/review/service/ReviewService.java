package lk.ac.kln.unimart.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lk.ac.kln.unimart.auth.entity.Role;
import lk.ac.kln.unimart.common.exception.ConflictException;
import lk.ac.kln.unimart.common.exception.ForbiddenException;
import lk.ac.kln.unimart.common.exception.ResourceNotFoundException;
import lk.ac.kln.unimart.order.entity.Order;
import lk.ac.kln.unimart.order.entity.OrderStatus;
import lk.ac.kln.unimart.order.repository.OrderRepository;
import lk.ac.kln.unimart.review.dto.ReviewCreateRequest;
import lk.ac.kln.unimart.review.dto.ReviewResponse;
import lk.ac.kln.unimart.review.dto.ReviewUpdateRequest;
import lk.ac.kln.unimart.review.entity.Review;
import lk.ac.kln.unimart.review.mapper.ReviewMapper;
import lk.ac.kln.unimart.review.repository.ReviewRepository;
import lk.ac.kln.unimart.security.CurrentUser;

/**
 * Owns review use-case logic: a review may only be created for a completed
 * order, by that order's buyer, once. See Guide 07 Part E.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviews;
    private final OrderRepository orders;
    private final ReviewMapper mapper;
    private final CurrentUser currentUser;

    public ReviewService(ReviewRepository reviews, OrderRepository orders,
                          ReviewMapper mapper, CurrentUser currentUser) {
        this.reviews = reviews;
        this.orders = orders;
        this.mapper = mapper;
        this.currentUser = currentUser;
    }

    @Transactional
    public ReviewResponse create(ReviewCreateRequest request, String email) {
        Order order = orders.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new ConflictException("Only completed orders can be reviewed");
        }
        if (!order.getBuyer().getUniversityEmail().equalsIgnoreCase(email)) {
            throw new ForbiddenException("Only the buyer can review this order");
        }
        if (reviews.existsByOrder_Id(order.getId())) {
            throw new ConflictException("This order already has a review");
        }

        Review review = new Review(
                order,
                order.getBuyer(),
                order.getListing().getSeller(),
                request.rating(),
                normalize(request.comment()));

        return mapper.toResponse(reviews.save(review));
    }

    @Transactional
    public ReviewResponse update(Long id, ReviewUpdateRequest request, String email) {
        Review review = requireOwnedReview(id, email);
        review.setRating(request.rating());
        review.setComment(normalize(request.comment()));
        return mapper.toResponse(review);
    }

    @Transactional
    public void delete(Long id, String email) {
        Review review = requireOwnedReview(id, email);
        reviews.delete(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> listForListing(Long listingId, Pageable pageable) {
        return reviews.findByOrder_Listing_IdOrderByCreatedAtDesc(listingId, pageable)
                .map(mapper::toResponse);
    }

    private Review requireOwnedReview(Long id, String email) {
        Review review = reviews.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        boolean isAuthor = review.getReviewer().getUniversityEmail().equalsIgnoreCase(email);
        boolean isAdmin = Role.ADMIN.name().equals(currentUser.roleOrNull());

        if (!isAuthor && !isAdmin) {
            throw new ForbiddenException("Only the review author or an admin can modify this review");
        }
        return review;
    }

    private String normalize(String comment) {
        return comment == null ? null : comment.trim();
    }
}
