package com.cms.service;

import com.cms.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerStudent(User user);
    User addInstructor(User user);
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    Optional<User> findById(Long id);
}

