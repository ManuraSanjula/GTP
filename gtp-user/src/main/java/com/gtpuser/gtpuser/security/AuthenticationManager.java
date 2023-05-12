package com.gtpuser.gtpuser.security;

import com.gtpuser.gtpuser.error.UnauthorizedException;
import com.gtpuser.gtpuser.models.User;
import com.gtpuser.gtpuser.service.UserService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final UserService userService;

    public AuthenticationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
    	User principal = (User) authentication.getPrincipal();
        return userService.getUser(principal.getId())
                .filter(user -> user.getActive())
                .switchIfEmpty(Mono.error(new UnauthorizedException("User account is disabled.")))
                .map(user -> authentication);
    }
}
