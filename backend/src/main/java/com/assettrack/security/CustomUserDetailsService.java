package com.assettrack.security;

import com.assettrack.domain.User;
import com.assettrack.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementation of Spring Security's {@link UserDetailsService}.
 * Responsible for loading user-specific data during authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Locates the user based on the email address.
     *
     * @param email the email address identifying the user whose data is required.
     * @return a fully populated Spring Security UserDetails object.
     * @throws UsernameNotFoundException if the user could not be found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User domainUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Map the Domain User to Spring Security's User object.
        // Role is prefixed with "ROLE_" to comply with Spring Security authority
        // conventions.
        return new org.springframework.security.core.userdetails.User(
                domainUser.getEmail(),
                domainUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + domainUser.getRole().name())));
    }
}