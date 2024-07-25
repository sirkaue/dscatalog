package com.sirkaue.dscatalog.services;

import com.sirkaue.dscatalog.dto.RoleDto;
import com.sirkaue.dscatalog.dto.UserDto;
import com.sirkaue.dscatalog.entities.Role;
import com.sirkaue.dscatalog.entities.User;
import com.sirkaue.dscatalog.repositories.RoleRepository;
import com.sirkaue.dscatalog.repositories.UserRepository;
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
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<UserDto> findAllPaged(Pageable pageable) {
        Page<User> list = repository.findAll(pageable);
        return list.map(entity -> new UserDto(entity));
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new UserDto(entity);
    }

    @Transactional
    public UserDto insert(UserDto dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new UserDto(entity);
    }

    @Transactional
    public UserDto update(Long id, UserDto dto) {
        try {
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new UserDto(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(String.format("ID %s not found", id));
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Id not found" + id);
        }
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(String.format("Id %s not found", id));
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(String.format("Unable to delete resource with ID %s. " +
                    "The resource is associated with other entities", id));
        }
    }

    private void copyDtoToEntity(UserDto dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());


        entity.getRoles().clear();
        for (RoleDto roleDto : dto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDto.getId());
            entity.getRoles().add(role);
        }
    }
}
