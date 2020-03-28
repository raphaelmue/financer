package org.financer.server.application.service;

import org.financer.server.domain.model.user.UserEntity;
import org.financer.server.domain.repository.UserRepository;
import org.financer.shared.domain.model.value.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Email email = (Email) authentication.getPrincipal();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

}
