package com.TiendaApp.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.TiendaApp.demo.dto.CategoryRequestDTO;
import com.TiendaApp.demo.dto.CategoryResponseDTO;
import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.entity.Categories;
import com.TiendaApp.demo.repository.CategoriesRepository;
import com.TiendaApp.demo.repository.ProductsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final ProductsRepository productsRepository;

    public HttpGlobalResponseDTO<List<CategoryResponseDTO>> getAllCategories() {
        HttpGlobalResponseDTO<List<CategoryResponseDTO>> response = new HttpGlobalResponseDTO<>();
        List<CategoryResponseDTO> listDTO = new ArrayList<>();

        List<Categories> categories = categoriesRepository.findAll();
        for (Categories c : categories) {
            CategoryResponseDTO dto = new CategoryResponseDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            listDTO.add(dto);
        }

        response.setMessage("Categorías encontradas");
        response.setData(listDTO);
        return response;
    }

    public HttpGlobalResponseDTO<CategoryResponseDTO> createCategory(CategoryRequestDTO request) {
        HttpGlobalResponseDTO<CategoryResponseDTO> response = new HttpGlobalResponseDTO<>();

        Optional<Categories> existing = categoriesRepository.findByName(request.getName());
        if (existing.isPresent()) {
            response.setMessage("El nombre de la categoría ya está en uso");
            return response;
        }

        Categories category = new Categories();
        category.setName(request.getName());
        categoriesRepository.save(category);

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        response.setMessage("Categoría creada exitosamente");
        response.setData(dto);
        return response;
    }

    public HttpGlobalResponseDTO<Void> deleteCategory(Long id) {
        HttpGlobalResponseDTO<Void> response = new HttpGlobalResponseDTO<>();

        Optional<Categories> categoryOpt = categoriesRepository.findById(id);
        if (categoryOpt.isEmpty()) {
            response.setMessage("La categoría no existe");
            return response;
        }

        var products = productsRepository.findByCategoryId(id);
        if (!products.isEmpty()) {
            response.setMessage("No se puede eliminar la categoría porque tiene productos asociados");
            return response;
        }

        categoriesRepository.deleteById(id);
        response.setMessage("Categoría eliminada correctamente");
        return response;
    }
}