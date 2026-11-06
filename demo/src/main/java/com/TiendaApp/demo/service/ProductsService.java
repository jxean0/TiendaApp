package com.TiendaApp.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.dto.ProductRequestDTO;
import com.TiendaApp.demo.dto.ProductResponseDTO;
import com.TiendaApp.demo.entity.Categories;
import com.TiendaApp.demo.entity.Products;
import com.TiendaApp.demo.entity.Users;
import com.TiendaApp.demo.repository.CategoriesRepository;
import com.TiendaApp.demo.repository.ProductsRepository;
import com.TiendaApp.demo.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;
    private final CategoriesRepository categoriesRepository;
    private final UsersRepository usersRepository;

    private ProductResponseDTO mapToDTO(Products p) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        dto.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : null);
        dto.setSellerUsername(p.getSeller() != null ? p.getSeller().getUsername() : null);
        return dto;
    }

    public HttpGlobalResponseDTO<List<ProductResponseDTO>> getCatalog(Long categoryId) {
        HttpGlobalResponseDTO<List<ProductResponseDTO>> response = new HttpGlobalResponseDTO<>();
        List<Products> products;

        if (categoryId != null) {
            products = productsRepository.findByCategoryId(categoryId);
        } else {
            products = productsRepository.findAll();
        }

        List<ProductResponseDTO> listDTO = new ArrayList<>();
        for (Products p : products) {
            listDTO.add(mapToDTO(p));
        }

        response.setMessage("Productos encontrados");
        response.setData(listDTO);
        return response;
    }

    public HttpGlobalResponseDTO<ProductResponseDTO> getProductById(Long id) {
        HttpGlobalResponseDTO<ProductResponseDTO> response = new HttpGlobalResponseDTO<>();
        Optional<Products> pOpt = productsRepository.findById(id);

        if (pOpt.isEmpty()) {
            response.setMessage("El producto no existe");
            return response;
        }

        response.setMessage("Producto encontrado");
        response.setData(mapToDTO(pOpt.get()));
        return response;
    }

    public HttpGlobalResponseDTO<List<ProductResponseDTO>> getProductsBySeller(Long sellerId) {
        HttpGlobalResponseDTO<List<ProductResponseDTO>> response = new HttpGlobalResponseDTO<>();
        List<Products> products = productsRepository.findBySellerId(sellerId);

        List<ProductResponseDTO> listDTO = new ArrayList<>();
        for (Products p : products) {
            listDTO.add(mapToDTO(p));
        }

        response.setMessage("Productos del vendedor encontrados");
        response.setData(listDTO);
        return response;
    }

    public HttpGlobalResponseDTO<ProductResponseDTO> createProduct(ProductRequestDTO request, Long sellerId) {
        HttpGlobalResponseDTO<ProductResponseDTO> response = new HttpGlobalResponseDTO<>();

        Optional<Categories> catOpt = categoriesRepository.findById(request.getCategoryId());
        if (catOpt.isEmpty()) {
            response.setMessage("La categoría especificada no existe");
            return response;
        }

        Optional<Users> sellerOpt = usersRepository.findById(sellerId);
        if (sellerOpt.isEmpty()) {
            response.setMessage("El vendedor no existe");
            return response;
        }

        Products p = new Products();
        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());
        p.setStock(request.getStock());
        p.setCategory(catOpt.get());
        p.setSeller(sellerOpt.get());

        productsRepository.save(p);

        response.setMessage("Producto creado exitosamente");
        response.setData(mapToDTO(p));
        return response;
    }

    public HttpGlobalResponseDTO<ProductResponseDTO> updateProduct(Long id, ProductRequestDTO request, Long userId, Long rolId) {
        HttpGlobalResponseDTO<ProductResponseDTO> response = new HttpGlobalResponseDTO<>();

        Optional<Products> pOpt = productsRepository.findById(id);
        if (pOpt.isEmpty()) {
            response.setMessage("El producto no existe");
            return response;
        }

        Products p = pOpt.get();

        if (rolId == 3L && !p.getSeller().getId().equals(userId)) {
            response.setMessage("No tienes permiso para editar un producto que no te pertenece");
            return response;
        }

        Optional<Categories> catOpt = categoriesRepository.findById(request.getCategoryId());
        if (catOpt.isEmpty()) {
            response.setMessage("La categoría especificada no existe");
            return response;
        }

        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());
        p.setStock(request.getStock());
        p.setCategory(catOpt.get());

        productsRepository.save(p);

        response.setMessage("Producto actualizado correctamente");
        response.setData(mapToDTO(p));
        return response;
    }

    public HttpGlobalResponseDTO<Void> deleteProduct(Long id, Long userId, Long rolId) {
        HttpGlobalResponseDTO<Void> response = new HttpGlobalResponseDTO<>();

        Optional<Products> pOpt = productsRepository.findById(id);
        if (pOpt.isEmpty()) {
            response.setMessage("El producto no existe");
            return response;
        }

        Products p = pOpt.get();

        if (rolId == 3L && !p.getSeller().getId().equals(userId)) {
            response.setMessage("No tienes permiso para eliminar un producto que no te pertenece");
            return response;
        }

        productsRepository.deleteById(id);
        response.setMessage("Producto eliminado correctamente");
        return response;
    }
}