package com.TiendaApp.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.dto.OrderRequestDTO;
import com.TiendaApp.demo.dto.OrderResponseDTO;
import com.TiendaApp.demo.entity.Orders;
import com.TiendaApp.demo.entity.Products;
import com.TiendaApp.demo.entity.Users;
import com.TiendaApp.demo.enums.OrderStatusEnum;
import com.TiendaApp.demo.repository.OrdersRepository;
import com.TiendaApp.demo.repository.ProductsRepository;
import com.TiendaApp.demo.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;
    private final UsersRepository usersRepository;

    private OrderResponseDTO mapToDTO(Orders o) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(o.getId());
        dto.setClientUsername(o.getClient() != null ? o.getClient().getUsername() : null);
        dto.setProductName(o.getProduct() != null ? o.getProduct().getName() : null);
        dto.setQuantity(o.getQuantity());
        dto.setTotal(o.getTotal());
        dto.setStatus(o.getStatus());
        dto.setCreatedAt(o.getCreatedAt());
        return dto;
    }

    public HttpGlobalResponseDTO<OrderResponseDTO> createOrder(OrderRequestDTO request, Long clientId) {
        HttpGlobalResponseDTO<OrderResponseDTO> response = new HttpGlobalResponseDTO<>();

        Optional<Users> clientOpt = usersRepository.findById(clientId);
        Optional<Products> productOpt = productsRepository.findById(request.getProductId());

        if (clientOpt.isEmpty() || productOpt.isEmpty()) {
            response.setMessage("Cliente o producto no encontrado");
            return response;
        }

        Products product = productOpt.get();

        if (product.getStock() < request.getQuantity()) {
            response.setMessage("Stock insuficiente para realizar el pedido");
            return response;
        }

        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        product.setStock(product.getStock() - request.getQuantity());
        productsRepository.save(product);

        Orders order = new Orders();
        order.setClient(clientOpt.get());
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setTotal(total);
        order.setStatus(OrderStatusEnum.PENDIENTE.name());
        order.setCreatedAt(LocalDateTime.now());

        ordersRepository.save(order);

        response.setMessage("Pedido creado exitosamente");
        response.setData(mapToDTO(order));
        return response;
    }

    public HttpGlobalResponseDTO<List<OrderResponseDTO>> getOrdersByClient(Long clientId) {
        HttpGlobalResponseDTO<List<OrderResponseDTO>> response = new HttpGlobalResponseDTO<>();
        List<Orders> orders = ordersRepository.findByClientId(clientId);

        List<OrderResponseDTO> listDTO = new ArrayList<>();
        for (Orders o : orders) {
            listDTO.add(mapToDTO(o));
        }

        response.setMessage("Pedidos del cliente encontrados");
        response.setData(listDTO);
        return response;
    }

    public HttpGlobalResponseDTO<List<OrderResponseDTO>> getSalesBySeller(Long sellerId) {
        HttpGlobalResponseDTO<List<OrderResponseDTO>> response = new HttpGlobalResponseDTO<>();
        List<Orders> orders = ordersRepository.findByProductSellerId(sellerId);

        List<OrderResponseDTO> listDTO = new ArrayList<>();
        for (Orders o : orders) {
            listDTO.add(mapToDTO(o));
        }

        response.setMessage("Ventas encontradas");
        response.setData(listDTO);
        return response;
    }

    public HttpGlobalResponseDTO<List<OrderResponseDTO>> getAllOrders() {
        HttpGlobalResponseDTO<List<OrderResponseDTO>> response = new HttpGlobalResponseDTO<>();
        List<Orders> orders = ordersRepository.findAll();

        List<OrderResponseDTO> listDTO = new ArrayList<>();
        for (Orders o : orders) {
            listDTO.add(mapToDTO(o));
        }

        response.setMessage("Todos los pedidos encontrados");
        response.setData(listDTO);
        return response;
    }

    public HttpGlobalResponseDTO<OrderResponseDTO> updateOrderStatus(Long orderId, OrderStatusEnum newStatus, Long userId, Long rolId) {
        HttpGlobalResponseDTO<OrderResponseDTO> response = new HttpGlobalResponseDTO<>();

        Optional<Orders> orderOpt = ordersRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            response.setMessage("El pedido no existe");
            return response;
        }

        Orders order = orderOpt.get();

        if (rolId == 3L && !order.getProduct().getSeller().getId().equals(userId)) {
            response.setMessage("No tienes permiso para modificar un pedido que no corresponde a tus productos");
            return response;
        }

        order.setStatus(newStatus.name());
        ordersRepository.save(order);

        response.setMessage("Estado del pedido actualizado correctamente");
        response.setData(mapToDTO(order));
        return response;
    }

    public HttpGlobalResponseDTO<OrderResponseDTO> cancelOrder(Long orderId, Long clientId) {
        HttpGlobalResponseDTO<OrderResponseDTO> response = new HttpGlobalResponseDTO<>();

        Optional<Orders> orderOpt = ordersRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            response.setMessage("El pedido no existe");
            return response;
        }

        Orders order = orderOpt.get();

        if (!order.getClient().getId().equals(clientId)) {
            response.setMessage("No tienes permiso para cancelar un pedido ajeno");
            return response;
        }

        if (!order.getStatus().equals(OrderStatusEnum.PENDIENTE.name())) {
            response.setMessage("El pedido solo se puede cancelar si se encuentra en estado PENDIENTE");
            return response;
        }

        Products product = order.getProduct();
        product.setStock(product.getStock() + order.getQuantity());
        productsRepository.save(product);

        order.setStatus(OrderStatusEnum.CANCELADO.name());
        ordersRepository.save(order);

        response.setMessage("Pedido cancelado exitosamente y stock devuelto");
        response.setData(mapToDTO(order));
        return response;
    }
}