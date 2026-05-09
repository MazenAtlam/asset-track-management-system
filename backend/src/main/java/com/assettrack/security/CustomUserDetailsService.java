package com.assettrack.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assettrack.domain.User;
import com.assettrack.repository.UserRepository;

/**
 * Spring Security {@link UserDetailsService} implementation that loads a
 * {@link User} from the database by email address.
 *
 * <p>The {@link com.assettrack.domain.Role} is mapped to a Spring
 * {@link SimpleGrantedAuthority} with a {@code "ROLE_"} prefix so that
 * {@code @PreAuthorize("hasRole('ADMIN')")} expressions work correctly.
 */
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user whose email equals the given {@code username}.
     *
     * @param username the email address (used as the Spring Security principal name)
     * @return a populated {@link UserDetails} instance
     * @throws UsernameNotFoundException if no user with that email exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with email: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
