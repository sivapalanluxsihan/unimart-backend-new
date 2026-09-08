package lk.ac.kln.unimart.order.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lk.ac.kln.unimart.auth.entity.User;
import lk.ac.kln.unimart.listing.entity.Listing;

/**
 * Maps to the {@code orders} table (V2 migration). Full order-creation /
 * payment-webhook flows belong to the orders/payments lab; this minimal
 * mapping exists so Guide 07's review feature — which requires a completed
 * order — has something real to reference. See Guide 01 entity catalogue.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    protected Order() {
        // JPA
    }

    public Order(Listing listing, User buyer, BigDecimal totalAmount, String paymentMethod) {
        this.listing = listing;
        this.buyer = buyer;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = OrderStatus.POSTED;
    }

    public Long getId() {
        return id;
    }

    public Listing getListing() {
        return listing;
    }

    public User getBuyer() {
        return buyer;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
