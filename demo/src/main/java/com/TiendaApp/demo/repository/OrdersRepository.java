package com.TiendaApp.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TiendaApp.demo.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByClientId(Long clientId);

    List<Orders> findByProductSellerId(Long sellerId);
}