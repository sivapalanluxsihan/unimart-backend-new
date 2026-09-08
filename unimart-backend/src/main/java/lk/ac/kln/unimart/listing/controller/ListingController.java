package lk.ac.kln.unimart.listing.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ac.kln.unimart.listing.dto.ListingRequest;
import lk.ac.kln.unimart.listing.dto.ListingResponse;
import lk.ac.kln.unimart.listing.entity.ListingStatus;
import lk.ac.kln.unimart.listing.service.ListingService;
import lk.ac.kln.unimart.review.dto.ReviewResponse;
import lk.ac.kln.unimart.review.service.ReviewService;

/**
 * Thin HTTP layer only: parses input, calls the service, maps the HTTP
 * status. No repository access, no business logic here. See Guide 03
 * responsibility rules and Guide 07 Parts B/D.
 */
@RestController
@RequestMapping("/api/v1/listings")
public class ListingController {

    private final ListingService listingService;
    private final ReviewService reviewService;

    public ListingController(ListingService listingService, ReviewService reviewService) {
        this.listingService = listingService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public Page<ListingResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ListingStatus status,
            Pageable pageable) {
        return listingService.search(q, categoryId, status, pageable);
    }

    @GetMapping("/{id}")
    public ListingResponse get(@PathVariable Long id) {
        return listingService.get(id);
    }

    @PostMapping
    public ResponseEntity<ListingResponse> create(
            @Valid @RequestBody ListingRequest request,
            Authentication authentication) {
        ListingResponse created = listingService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ListingResponse update(@PathVariable Long id,
                                   @Valid @RequestBody ListingRequest request,
                                   Authentication authentication) {
        return listingService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        listingService.archive(id, authentication.getName());
    }

    @GetMapping("/{id}/reviews")
    public Page<ReviewResponse> reviews(@PathVariable Long id, Pageable pageable) {
        return reviewService.listForListing(id, pageable);
    }
}
