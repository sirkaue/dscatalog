package com.sirkaue.dscatalog.repositories;

import com.sirkaue.dscatalog.entities.Product;
import com.sirkaue.dscatalog.utils.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void findAllShouldReturnPageWhenPageableIsGiven() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> result = repository.findAll(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(20, result.getSize());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    public void findByIdShouldReturnNotEmptyOptionalProductWhenIdExists() {
        Optional<Product> product = repository.findById(existingId);
        Assertions.assertTrue(product.isPresent());
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalProductWhenIdDoesNotExist() {
        Optional<Product> product = repository.findById(nonExistingId);
        Assertions.assertTrue(product.isEmpty());
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);
        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void updateShouldPersistChangesWhenIdExists() {
        Product product = Factory.createProduct();
        product.setId(existingId);
        product = repository.save(product);

        String updatedName = "Updated product name";
        product.setName(updatedName);
        product = repository.save(product);

        Product updatedProduct = repository.findById(existingId).get();
        Assertions.assertEquals(updatedName, product.getName());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertTrue(result.isEmpty());
    }
}
