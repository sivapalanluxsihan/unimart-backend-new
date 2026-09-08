package lk.ac.kln.unimart.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.ac.kln.unimart.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
