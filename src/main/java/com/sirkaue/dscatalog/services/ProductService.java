package com.sirkaue.dscatalog.services;

import com.sirkaue.dscatalog.dto.ProductDto;
import com.sirkaue.dscatalog.entities.Product;
import com.sirkaue.dscatalog.repositories.ProductRepository;
import com.sirkaue.dscatalog.services.exceptions.DatabaseException;
import com.sirkaue.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);

        // stream - converte coleção (lista) para stream.
        // map - transforma cada elemento original em outra coisa. Aplica uma função a cada elemento da lista.

        Page<ProductDto> listDto = list.map(x -> new ProductDto(x));
        return listDto;
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {
        Optional<Product> obj = repository.findById(id);

        // método get do Opitional obtém o objeto que esta dentro do Opitional
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDto(entity, entity.getCategories());
    }

    @Transactional
    public ProductDto insert(ProductDto dto) {
        Product entity = new Product();
       // entity.setName(dto.getName());
        entity = repository.save(entity);
        return new ProductDto(entity);
    }

    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        try {
            Product entity = repository.getReferenceById(id);
            //entity.setName(dto.getName());
            entity = repository.save(entity);
            return new ProductDto(entity);
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
