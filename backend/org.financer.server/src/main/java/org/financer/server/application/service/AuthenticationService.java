package org.financer.server.application.service;

import org.financer.server.application.api.error.UnauthorizedOperationException;
import org.financer.server.domain.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    /**
     * Returns the authenticated user object, if exists.
     *
     * @return user object
     */
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public void throwIfUserHasNotRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(role)) {
                return;
            }
        }
        throw new UnauthorizedOperationException(getUserId());
    }

    public long getUserId() {
        return getAuthenticatedUser().getId();
    }

}
