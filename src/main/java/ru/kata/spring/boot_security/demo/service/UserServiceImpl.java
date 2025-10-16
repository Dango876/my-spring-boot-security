package ru.kata.spring.boot_security.demo.service;


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
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.entity.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";


    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
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
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = currentAuth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        User userInBase = getUserById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String oldName = userInBase.getName();
        String oldPassword = userInBase.getPassword();
        boolean isCurrentUser = oldName.equals(currentAuth.getName());

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
        if (!StringUtils.isEmpty(user.getRoles()) && isAdmin) {
            userInBase.setRoles(user.getRoles());
        }

        boolean usernameChanged = !oldName.equals(userInBase.getName());
        boolean passwordChanged = !StringUtils.isEmpty(user.getPassword()) &&
                !passwordEncoder.matches(user.getPassword(), oldPassword);

        if (isCurrentUser && (usernameChanged || passwordChanged)) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userInBase.getName());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
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
        User user = getUserById(id).orElseThrow(EntityNotFoundException::new);
        user.getRoles().clear();
        userRepository.deleteById(id);
    }
}
