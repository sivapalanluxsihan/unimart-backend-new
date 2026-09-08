package lk.ac.kln.unimart.review.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lk.ac.kln.unimart.auth.entity.User;
import lk.ac.kln.unimart.order.entity.Order;

/**
 * Maps to the {@code reviews} table (V2 migration: UNIQUE order_id, rating
 * 1-5 via CHECK, reviewer/reviewee FKs). See Guide 07 Part E.
 */
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    protected Review() {
        // JPA
    }

    public Review(Order order, User reviewer, User reviewee, Integer rating, String comment) {
        this.order = order;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public User getReviewer() {
        return reviewer;
    }

    public User getReviewee() {
        return reviewee;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
