package com.gabrielsilva.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.gabrielsilva.dscatalog.services.validation.UserInsertValid;
import jakarta.validation.ConstraintValidator;
import org.springframework.beans.factory.annotation.Autowired;

import com.gabrielsilva.dscatalog.dto.UserInsertDTO;
import com.gabrielsilva.dscatalog.entities.User;
import com.gabrielsilva.dscatalog.repositories.UserRepository;
import com.gabrielsilva.dscatalog.controllers.handlers.FieldMessage;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, jakarta.validation.ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        User user = repository.findByEmail(dto.getEmail());
        if ( user != null) {
            list.add(new FieldMessage("email", "Email já existe"));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}