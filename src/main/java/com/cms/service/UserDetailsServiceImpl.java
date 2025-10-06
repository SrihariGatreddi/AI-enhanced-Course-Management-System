package com.cms.service;

import com.cms.model.User;
import com.cms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This is the main method Spring Security calls when a user tries to log in.
     * Its only job is to find the user in the database and return their details.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Step 1: Find the user in your database by their email.
        // The result is an "Optional", which is like a box that might contain a User, or might be empty.
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Step 2: Check if the user was actually found.
        if (!optionalUser.isPresent()) {
            // If the Optional box is empty, it means no user with that email exists.
            // We must throw this specific exception to tell Spring Security the login failed.
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Step 3: If we are here, the user was found. Let's get them out of the Optional box.
        User user = optionalUser.get();

        // Step 4: Convert the user's role (like "ROLE_STUDENT") into a format Spring Security understands.
        List<GrantedAuthority> authorities = getAuthorities(user.getRole());

        // Step 5: Create and return a standard "UserDetails" object.
        // This is the "ID card" that Spring Security uses. It needs the email, the
        // HASHED password from your database, and the list of roles (authorities).
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * A helper method to convert a simple role string into a List of GrantedAuthority objects.
     */
    private List<GrantedAuthority> getAuthorities(String role) {
        // Create an empty list to hold the user's roles.
        List<GrantedAuthority> authorities = new ArrayList<>();

        // If the user has a role, create a "SimpleGrantedAuthority" object from it.
        // This is the standard object Spring Security needs for roles.
        if (role != null && !role.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        // Return the list of roles.
        return authorities;
    }
}