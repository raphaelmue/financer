package org.financer.server.application.service;

import org.financer.server.domain.model.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    /**
     * Returns the authenticated user object, if exists.
     *
     * @return user object
     */
    public UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (UserEntity) authentication.getPrincipal();
        }
        return null;
    }

    public long getUserId() {
        return getAuthenticatedUser().getId();
    }

}
