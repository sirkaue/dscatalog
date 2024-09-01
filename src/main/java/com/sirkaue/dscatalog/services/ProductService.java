package com.sirkaue.dscatalog.services;

import com.sirkaue.dscatalog.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductDto> findAllPaged(Pageable pageable);

    ProductDto findById(Long id);

    ProductDto insert(ProductDto dto);

    ProductDto update(Long id, ProductDto dto);

    void delete(Long id);
}
