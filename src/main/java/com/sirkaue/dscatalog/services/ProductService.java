package com.sirkaue.dscatalog.services;

import com.sirkaue.dscatalog.dto.CategoryDto;
import com.sirkaue.dscatalog.dto.ProductDto;
import com.sirkaue.dscatalog.entities.Category;
import com.sirkaue.dscatalog.entities.Product;
import com.sirkaue.dscatalog.repositories.CategoryRepository;
import com.sirkaue.dscatalog.repositories.ProductRepository;
import com.sirkaue.dscatalog.services.exceptions.DatabaseException;
import com.sirkaue.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPaged(Pageable pageable) {
        Page<Product> list = repository.findAll(pageable);
        return list.map(entity -> new ProductDto(entity));
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDto(entity, entity.getCategories());
    }

    @Transactional
    public ProductDto insert(ProductDto dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new ProductDto(entity);
    }

    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        try {
            Product entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new ProductDto(entity, entity.getCategories());
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(String.format("ID '%d' not found", id));
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("ID '%d' not found", id));
        }
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(String.format("Id %d not found", id));
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(String.format("Unable to delete resource with ID '%d'. " +
                    "The resource is associated with other entities", id));
        }
    }

    private void copyDtoToEntity(ProductDto dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        entity.getCategories().clear();
        for (CategoryDto categoryDto : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(categoryDto.getId());
            entity.getCategories().add(category);
        }
    }
}
