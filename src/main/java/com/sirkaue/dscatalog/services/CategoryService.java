package com.sirkaue.dscatalog.services;

import com.sirkaue.dscatalog.dto.CategoryDto;
import com.sirkaue.dscatalog.entities.Category;
import com.sirkaue.dscatalog.repositories.CategoryRepository;
import com.sirkaue.dscatalog.services.exceptions.DatabaseException;
import com.sirkaue.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDto> findAllPaged(Pageable pageable) {
        Page<Category> list = repository.findAll(pageable);
        Page<CategoryDto> listDto = list.map(x -> new CategoryDto(x));
        return listDto;
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new CategoryDto(entity);
    }

    @Transactional
    public CategoryDto insert(CategoryDto dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new CategoryDto(entity);
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        try {
            Category entity = repository.getReferenceById(id);
            entity.setName(dto.getName());
            entity = repository.save(entity);
            return new CategoryDto(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(String.format("ID %s not found", id));
        }
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new DatabaseException("Integrity violation");
        }
        repository.deleteById(id);
    }
}
