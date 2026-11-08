package com.gabrielsilva.dscatalog.services;

import com.gabrielsilva.dscatalog.dto.EmailDTO;
import com.gabrielsilva.dscatalog.entities.PasswordRecover;
import com.gabrielsilva.dscatalog.entities.User;
import com.gabrielsilva.dscatalog.repositories.PasswordRecoverRepository;
import com.gabrielsilva.dscatalog.repositories.UserRepository;
import com.gabrielsilva.dscatalog.services.exceptions.ObjectNotFoundException;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private EmailService emailService;

    public void createRecoverToken(EmailDTO body) {

        User user = userRepository.findByEmail(body.getEmail());
        if (user == null) {
            throw new ObjectNotFoundException("Email não encontrado");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.getEmail());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));

        passwordRecoverRepository.save(entity);

        String text = "Acesse o link para definir uma nova senha\n\n"
                + recoverUri + token + ". Validade de " + tokenMinutes + " minutos";
        emailService.sendEmail(body.getEmail(), "Recuperação de senha", text);
    }
}
