package com.TiendaApp.demo.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String categoryName;
    private String sellerUsername;
}