package com.example.wasla.repository;

import com.example.wasla.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByClientId(Long clientId, Pageable pageable);

    @EntityGraph(attributePaths = { "client", "driver" })
    Optional<Order> findByIdAndClientId(Long orderId, Long clientId);
}
