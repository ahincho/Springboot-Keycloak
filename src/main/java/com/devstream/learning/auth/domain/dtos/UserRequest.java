package com.devstream.learning.auth.domain.dtos;

import lombok.Builder;
import java.util.Set;

@Builder
public record UserRequest(String username, String email, String firstname, String lastname, String password, Set<String> roles) {

}
