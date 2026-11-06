package com.TiendaApp.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderResponseDTO {
    private Long id;
    private String clientUsername;
    private String productName;
    private Integer quantity;
    private BigDecimal total;
    private String status;
    private LocalDateTime createdAt;
}