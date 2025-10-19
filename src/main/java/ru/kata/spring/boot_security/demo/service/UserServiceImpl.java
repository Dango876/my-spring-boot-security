package ru.kata.spring.boot_security.demo.service;

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

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

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
        User userInBase = getUserById(user.getId());

        String oldName = userInBase.getName();
        updateUserFields(user, userInBase, currentAuth);
        updateAuthenticationIfNeeded(oldName, userInBase, currentAuth);
    }

    private void updateUserFields(User source, User target, Authentication authentication) {
        boolean isAdmin = isAdmin(authentication);

        if (!StringUtils.isEmpty(source.getName())) {
            target.setName(source.getName());
        }
        if (!StringUtils.isEmpty(source.getFloor())) {
            target.setFloor(source.getFloor());
        }
        if (!StringUtils.isEmpty(source.getAge())) {
            target.setAge(source.getAge());
        }
        if (!StringUtils.isEmpty(source.getPassword())) {
            target.setPassword(passwordEncoder.encode(source.getPassword()));
        }
        if (!StringUtils.isEmpty(source.getRoles()) && isAdmin) {
            target.setRoles(source.getRoles());
        }
    }

    private void updateAuthenticationIfNeeded(String oldName, User target, Authentication authentication) {
        boolean isCurrentUser = oldName.equals(authentication.getName());
        boolean usernameChanged = !oldName.equals(target.getName());
        boolean passwordChanged = !StringUtils.isEmpty(target.getPassword());

        if (isCurrentUser && (usernameChanged || passwordChanged)) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(target.getName());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
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
