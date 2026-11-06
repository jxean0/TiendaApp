package com.TiendaApp.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TiendaApp.demo.dto.CategoryRequestDTO;
import com.TiendaApp.demo.dto.CategoryResponseDTO;
import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.enums.RoleEnum;
import com.TiendaApp.demo.security.RequiresRole;
import com.TiendaApp.demo.service.CategoriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;

    @GetMapping
    @RequiresRole({RoleEnum.ADMIN, RoleEnum.CLIENTE, RoleEnum.VENDEDOR})
    public ResponseEntity<HttpGlobalResponseDTO<List<CategoryResponseDTO>>> getAllCategories() {
        try {
            HttpGlobalResponseDTO<List<CategoryResponseDTO>> response = categoriesService.getAllCategories();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping
    @RequiresRole({RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<CategoryResponseDTO>> createCategory(@RequestBody CategoryRequestDTO request) {
        try {
            HttpGlobalResponseDTO<CategoryResponseDTO> response = categoriesService.createCategory(request);
            if (response.getMessage().contains("ya está en uso")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @RequiresRole({RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<Void>> deleteCategory(@PathVariable Long id) {
        try {
            HttpGlobalResponseDTO<Void> response = categoriesService.deleteCategory(id);
            if (response.getMessage().contains("tiene productos asociados")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}