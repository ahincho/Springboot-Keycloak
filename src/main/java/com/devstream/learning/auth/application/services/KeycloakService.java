package com.devstream.learning.auth.application.services;

import com.devstream.learning.auth.domain.dtos.UserRequest;
import com.devstream.learning.auth.application.adapters.KeycloakProvider;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class KeycloakService {
    public List<UserRepresentation> findAllUsers() {
        return KeycloakProvider.getRealmResource().users().list();
    }
    public List<UserRepresentation> findByUsername(String username) {
        return KeycloakProvider.getRealmResource().users().searchByUsername(username, true);
    }
    public String saveUser(UserRequest userRequest) {
        UsersResource usersResource = KeycloakProvider.getUserResource();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userRequest.firstname());
        userRepresentation.setLastName(userRequest.lastname());
        userRepresentation.setEmail(userRequest.email());
        userRepresentation.setUsername(userRequest.username());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        Response response = usersResource.create(userRepresentation);
        int status = response.getStatus();
        if (status == HttpStatus.CREATED.value()) {
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(OAuth2Constants.PASSWORD);
            credentialRepresentation.setValue(userRequest.password());
            usersResource.get(userId).resetPassword(credentialRepresentation);
            RealmResource realmResource = KeycloakProvider.getRealmResource();
            List<RoleRepresentation> roleRepresentations = null;
            if (userRequest.roles() == null || userRequest.roles().isEmpty()) {
                roleRepresentations = List.of(realmResource.roles().get("student").toRepresentation());
            } else {
                roleRepresentations = realmResource.roles().list().stream()
                        .filter(role -> userRequest.roles().stream()
                                .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                        .toList();
            }
            realmResource.users().get(userId).roles().realmLevel().add(roleRepresentations);
            return "User created successfully";
        } else if (status == HttpStatus.CONFLICT.value()) {
            log.error("User already exists!");
            return "User already exists!";
        } else {
            log.error("Error when creating user!");
            return "Something went wrong, please contact the support team";
        }
    }
    public void updateUser(String userId, UserRequest userRequest) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setValue(userRequest.password());
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userRequest.firstname());
        userRepresentation.setLastName(userRequest.lastname());
        userRepresentation.setEmail(userRequest.email());
        userRepresentation.setUsername(userRequest.username());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        UserResource userResource = KeycloakProvider.getUserResource().get(userId);
        userResource.update(userRepresentation);
    }
    public void deleteUser(String userId) {
        KeycloakProvider.getUserResource().get(userId).remove();
    }
}
