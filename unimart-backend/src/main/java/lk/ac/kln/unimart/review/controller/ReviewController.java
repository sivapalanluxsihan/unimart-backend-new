package lk.ac.kln.unimart.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ac.kln.unimart.review.dto.ReviewCreateRequest;
import lk.ac.kln.unimart.review.dto.ReviewResponse;
import lk.ac.kln.unimart.review.dto.ReviewUpdateRequest;
import lk.ac.kln.unimart.review.service.ReviewService;

/**
 * Thin HTTP layer for review writes. Public review reads are served under
 * /api/v1/listings/{id}/reviews via ListingController. See Guide 07 Part B.
 */
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody ReviewCreateRequest request,
            Authentication authentication) {
        ReviewResponse created = reviewService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ReviewResponse update(@PathVariable Long id,
                                  @Valid @RequestBody ReviewUpdateRequest request,
                                  Authentication authentication) {
        return reviewService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        reviewService.delete(id, authentication.getName());
    }
}
