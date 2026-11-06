package com.TiendaApp.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TiendaApp.demo.entity.Products;

public interface ProductsRepository extends JpaRepository<Products, Long> {
    List<Products> findByCategoryId(Long categoryId);

    List<Products> findBySellerId(Long sellerId);
}