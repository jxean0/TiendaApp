package com.TiendaApp.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.dto.ProductRequestDTO;
import com.TiendaApp.demo.dto.ProductResponseDTO;
import com.TiendaApp.demo.enums.RoleEnum;
import com.TiendaApp.demo.security.RequiresRole;
import com.TiendaApp.demo.service.ProductsService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsService productsService;

    @GetMapping
    public ResponseEntity<HttpGlobalResponseDTO<List<ProductResponseDTO>>> getCatalog(
            @RequestParam(required = false) Long categoryId) {
        try {
            HttpGlobalResponseDTO<List<ProductResponseDTO>> response = productsService.getCatalog(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpGlobalResponseDTO<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        try {
            HttpGlobalResponseDTO<ProductResponseDTO> response = productsService.getProductById(id);
            if (response.getMessage().contains("no existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/mine")
    @RequiresRole({RoleEnum.VENDEDOR})
    public ResponseEntity<HttpGlobalResponseDTO<List<ProductResponseDTO>>> getProductsBySeller(HttpServletRequest request) {
        try {
            Long sellerId = (Long) request.getAttribute("userId");
            HttpGlobalResponseDTO<List<ProductResponseDTO>> response = productsService.getProductsBySeller(sellerId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping
    @RequiresRole({RoleEnum.VENDEDOR})
    public ResponseEntity<HttpGlobalResponseDTO<ProductResponseDTO>> createProduct(
            @RequestBody ProductRequestDTO requestBody, HttpServletRequest request) {
        try {
            Long sellerId = (Long) request.getAttribute("userId");
            HttpGlobalResponseDTO<ProductResponseDTO> response = productsService.createProduct(requestBody, sellerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<ProductResponseDTO>> updateProduct(
            @PathVariable Long id, @RequestBody ProductRequestDTO requestBody, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long rolId = (Long) request.getAttribute("rolId");
            
            HttpGlobalResponseDTO<ProductResponseDTO> response = productsService.updateProduct(id, requestBody, userId, rolId);
            
            if (response.getMessage().contains("No tienes permiso")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (response.getMessage().contains("no existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<Void>> deleteProduct(
            @PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long rolId = (Long) request.getAttribute("rolId");
            
            HttpGlobalResponseDTO<Void> response = productsService.deleteProduct(id, userId, rolId);
            
            if (response.getMessage().contains("No tienes permiso")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (response.getMessage().contains("no existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}