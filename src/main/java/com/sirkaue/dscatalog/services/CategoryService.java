package com.sirkaue.dscatalog.services;

import com.sirkaue.dscatalog.dto.CategoryDto;
import com.sirkaue.dscatalog.entities.Category;
import com.sirkaue.dscatalog.repositories.CategoryRepository;
import com.sirkaue.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        List<Category> list = repository.findAll();

        // stream - converte coleção (lista) para stream.
        // map - transforma cada elemento original em outra coisa. Aplica uma função a cada elemento da lista.

        List<CategoryDto> listDto = list.stream()
                .map(x -> new CategoryDto(x)).collect(Collectors.toList());
        return listDto;
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        Optional<Category> obj = repository.findById(id);

        // método get do Opitional obtém o objeto que esta dentro do Opitional
        Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        return new CategoryDto(entity);
    }
}
