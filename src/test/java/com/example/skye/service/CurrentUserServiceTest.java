package com.example.skye.service;

import com.example.skye.entity.UserEntity;
import com.example.skye.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_fromUserDetails() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder().username("a@b.com").password("x").roles("USER").build(),
                        "cred"));

        UserEntity entity = new UserEntity();
        entity.setUsername("a@b.com");
        when(userRepository.findByUsername("a@b.com")).thenReturn(Optional.of(entity));

        assertEquals("a@b.com", currentUserService.getCurrentUser().getUsername());
    }

    @Test
    void getCurrentUser_unknownUser_throwsUnauthorized() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("ghost@b.com", "cred"));

        when(userRepository.findByUsername("ghost@b.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> currentUserService.getCurrentUser());
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
