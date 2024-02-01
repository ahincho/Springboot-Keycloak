package com.devstream.learning.auth.infrastructure.controllers;

import com.devstream.learning.auth.application.services.KeycloakService;
import com.devstream.learning.auth.domain.dtos.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/keycloak")
@PreAuthorize("hasRole('admin_role')")
public class KeycloakController {
    private final KeycloakService keycloakService;
    public KeycloakController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(keycloakService.findAllUsers());
    }
    @GetMapping("/{username}")
    public ResponseEntity<?> findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(keycloakService.findByUsername(username));
    }
    @PostMapping
    public ResponseEntity<?> save(@RequestBody UserRequest userRequest, UriComponentsBuilder uriComponentsBuilder) {
        String response = keycloakService.saveUser(userRequest);
        URI uri = uriComponentsBuilder.path("/api/v1/keycloak").build().toUri();
        return ResponseEntity.created(uri).body(response);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable("userId") String userId, @RequestBody UserRequest userRequest) {
        keycloakService.updateUser(userId, userRequest);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable("userId") String userId) {
        keycloakService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
