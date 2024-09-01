package com.sirkaue.dscatalog.services;

import com.sirkaue.dscatalog.dto.UserDto;
import com.sirkaue.dscatalog.dto.UserInsertDto;
import com.sirkaue.dscatalog.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Page<UserDto> findAllPaged(Pageable pageable);

    UserDto findById(Long id);

    UserDto insert(UserInsertDto dto);

    UserDto update(Long id, UserUpdateDto dto);

    void delete(Long id);
}
