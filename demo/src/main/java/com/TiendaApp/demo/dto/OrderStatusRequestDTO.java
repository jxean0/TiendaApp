package com.TiendaApp.demo.dto;

import com.TiendaApp.demo.enums.OrderStatusEnum;
import lombok.Data;

@Data
public class OrderStatusRequestDTO {
    private OrderStatusEnum status;
}