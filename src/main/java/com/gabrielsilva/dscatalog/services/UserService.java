package com.gabrielsilva.dscatalog.services;

import com.gabrielsilva.dscatalog.dto.RoleDTO;
import com.gabrielsilva.dscatalog.dto.UserDTO;
import com.gabrielsilva.dscatalog.dto.UserInsertDTO;
import com.gabrielsilva.dscatalog.entities.Role;
import com.gabrielsilva.dscatalog.entities.User;
import com.gabrielsilva.dscatalog.repositories.RoleRepository;
import com.gabrielsilva.dscatalog.repositories.UserRepository;
import com.gabrielsilva.dscatalog.services.exceptions.DatabaseException;
import com.gabrielsilva.dscatalog.services.exceptions.ObjectNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        List<User> result = repository.findAll();
        return result.stream().map(UserDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(PageRequest pageRequest) {
        Page<User> result = repository.findAll(pageRequest);
        return result.map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User cat = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Categoria não encontrada"));
        return new UserDTO(cat);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        entity = repository.save(entity);

        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);

            return new UserDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException("Categoria não encontrada");
        }
    }

    @Transactional
    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new ObjectNotFoundException("Categoria não encontrada");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity( UserDTO dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();
        for (RoleDTO roleDTO : dto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            entity.getRoles().add(role);
        }
    };
}
