package ru.kata.spring.boot_security.demo.hiber.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ru.kata.spring.boot_security.demo.hiber.dao.UserRepository;
import ru.kata.spring.boot_security.demo.hiber.model.User;

import javax.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUserService customUserDetailsService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           SecurityUserService customUserDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(User updatedUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User currentUser = userRepository.findByName(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin && !currentUser.getId().equals(existingUser.getId())) {
            throw new AccessDeniedException("You cannot update another user's data");
        }

        Optional.ofNullable(updatedUser.getName()).filter(StringUtils::hasText).ifPresent(existingUser::setName);
        Optional.ofNullable(updatedUser.getSex()).filter(StringUtils::hasText).ifPresent(existingUser::setSex);
        Optional.ofNullable(updatedUser.getAge()).ifPresent(existingUser::setAge);

        if (StringUtils.hasText(updatedUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (isAdmin && updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            existingUser.setRoles(updatedUser.getRoles());
        }

        userRepository.save(existingUser);

        if (currentUser.getId().equals(existingUser.getId())
            && (!existingUser.getName().equals(auth.getName())
                || StringUtils.hasText(updatedUser.getPassword()))) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(existingUser.getUsername());
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.getRoles().clear();
        userRepository.delete(user);
    }
}
