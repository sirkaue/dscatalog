package com.sirkaue.dscatalog.utils;

import com.sirkaue.dscatalog.dto.ProductDto;
import com.sirkaue.dscatalog.entities.Category;
import com.sirkaue.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {
    public static Product createProduct() {
        Product product = new Product(
                1L,
                "Phone",
                "Good Phone",
                800.0,
                "https://img.com/img.png", Instant.parse(
                "2024-06-01T12:00:00Z"));
        product.getCategories().add(new Category(2L, "Eletronics"));
        return product;
    }

    public static ProductDto createProductDto() {
        Product product = createProduct();
        return new ProductDto(product, product.getCategories());
    }
}
