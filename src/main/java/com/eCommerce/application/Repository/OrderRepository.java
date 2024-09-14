package com.eCommerce.application.Repository;

import com.eCommerce.application.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
