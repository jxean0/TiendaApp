package com.TiendaApp.demo.dto;

import lombok.Data;

@Data
public class OrderRequestDTO {
    private Long productId;
    private Integer quantity;
}