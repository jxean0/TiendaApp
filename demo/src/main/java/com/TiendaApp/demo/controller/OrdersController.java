package com.TiendaApp.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.dto.OrderRequestDTO;
import com.TiendaApp.demo.dto.OrderResponseDTO;
import com.TiendaApp.demo.dto.OrderStatusRequestDTO;
import com.TiendaApp.demo.enums.RoleEnum;
import com.TiendaApp.demo.security.RequiresRole;
import com.TiendaApp.demo.service.OrdersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping
    @RequiresRole({RoleEnum.CLIENTE})
    public ResponseEntity<HttpGlobalResponseDTO<OrderResponseDTO>> createOrder(
            @RequestBody OrderRequestDTO requestBody, HttpServletRequest request) {
        try {
            Long clientId = (Long) request.getAttribute("userId");
            HttpGlobalResponseDTO<OrderResponseDTO> response = ordersService.createOrder(requestBody, clientId);

            if (response.getMessage().contains("Stock insuficiente")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/mine")
    @RequiresRole({RoleEnum.CLIENTE})
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderResponseDTO>>> getOrdersByClient(HttpServletRequest request) {
        try {
            Long clientId = (Long) request.getAttribute("userId");
            HttpGlobalResponseDTO<List<OrderResponseDTO>> response = ordersService.getOrdersByClient(clientId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/sales")
    @RequiresRole({RoleEnum.VENDEDOR})
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderResponseDTO>>> getSalesBySeller(HttpServletRequest request) {
        try {
            Long sellerId = (Long) request.getAttribute("userId");
            HttpGlobalResponseDTO<List<OrderResponseDTO>> response = ordersService.getSalesBySeller(sellerId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    @RequiresRole({RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderResponseDTO>>> getAllOrders() {
        try {
            HttpGlobalResponseDTO<List<OrderResponseDTO>> response = ordersService.getAllOrders();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/status")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<OrderResponseDTO>> updateOrderStatus(
            @PathVariable Long id, @RequestBody OrderStatusRequestDTO requestBody, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long rolId = (Long) request.getAttribute("rolId");

            HttpGlobalResponseDTO<OrderResponseDTO> response = ordersService.updateOrderStatus(
                    id, requestBody.getStatus(), userId, rolId);

            if (response.getMessage().contains("No tienes permiso")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/cancel")
    @RequiresRole({RoleEnum.CLIENTE})
    public ResponseEntity<HttpGlobalResponseDTO<OrderResponseDTO>> cancelOrder(
            @PathVariable Long id, HttpServletRequest request) {
        try {
            Long clientId = (Long) request.getAttribute("userId");
            HttpGlobalResponseDTO<OrderResponseDTO> response = ordersService.cancelOrder(id, clientId);

            if (response.getMessage().contains("solo se puede cancelar si se encuentra en estado PENDIENTE")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}