package org.financer.server.application.configuration.security;

import org.financer.server.domain.model.user.User;
import org.financer.server.domain.repository.UserRepository;
import org.financer.shared.domain.model.value.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<User> userOptional = userRepository.findByEmail(new Email(email));
        if (userOptional.isPresent()) {
            return new AuthenticationUser(userOptional.get());
        }
        throw new UsernameNotFoundException("User with e-mail " + email + " was not found.");
    }

}
