package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.entity.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User userInBase = getUserById(user.getId());

        if (!StringUtils.isEmpty(user.getName())) {
            userInBase.setName(user.getName());
        }
        if (!StringUtils.isEmpty(user.getFloor())) {
            userInBase.setFloor(user.getFloor());
        }
        if (!StringUtils.isEmpty(user.getAge())) {
            userInBase.setAge(user.getAge());
        }
        if (!StringUtils.isEmpty(user.getPassword())) {
            userInBase.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            userInBase.setRoles(user.getRoles());
        }

        userRepository.save(userInBase);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("User not found with name: " + name));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.getRoles().clear();
        userRepository.deleteById(id);
    }
}
