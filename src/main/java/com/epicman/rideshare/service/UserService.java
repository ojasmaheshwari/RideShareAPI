package com.epicman.rideshare.service;

import com.epicman.rideshare.exception.ConflictException;
import com.epicman.rideshare.model.UserModel;
import com.epicman.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @CachePut(value = "users", key = "#result.username")
    public UserModel create(UserModel userModel) {
        return userRepository.save(userModel);
    }

    @Cacheable(value = "users", key = "#username")
    public UserModel findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @CachePut(value = "users", key = "#result.username")
    public UserModel registerUser(String username, String password, String role) throws ConflictException {
        if (userRepository.findByUsername(username) != null) {
            throw new ConflictException("A user with that username already exists");
        }

        UserModel userModel = new UserModel();
        userModel.setUsername(username);
        userModel.setPasswordHash(passwordEncoder.encode(password));
        userModel.setRole(role);

        return userRepository.save(userModel);
    }
}
