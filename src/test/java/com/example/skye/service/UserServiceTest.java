package com.example.skye.service;

import com.example.skye.dto.RegisterRequest;
import com.example.skye.entity.UserEntity;
import com.example.skye.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setPhoneNumber("+1234567890");

        when(userRepository.existsByUsername("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        UserEntity saved = new UserEntity();
        saved.setId(42L);
        when(userRepository.save(any(UserEntity.class))).thenReturn(saved);

        Map<String, Object> response = userService.registerUser(request);

        assertEquals("User registered successfully", response.get("message"));
        assertEquals(42L, response.get("userId"));
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertEquals("user@example.com", captor.getValue().getUsername());
    }

    @Test
    void registerUser_duplicateUsername_throwsConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("dup@example.com");
        when(userRepository.existsByUsername("dup@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.registerUser(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void authenticateUser_invalidCredentials_throwsUnauthorized() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.authenticateUser("user@example.com", "wrong"));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void authenticateUser_success() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user@example.com", "pass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        Authentication result = userService.authenticateUser("user@example.com", "pass");
        assertEquals("user@example.com", result.getName());
    }
}
