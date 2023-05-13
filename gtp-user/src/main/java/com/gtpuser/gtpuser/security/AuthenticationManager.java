package com.gtpuser.gtpuser.security;

import com.gtpuser.gtpuser.error.model.UnauthorizedException;
import com.gtpuser.gtpuser.models.User;
import com.gtpuser.gtpuser.utils.TokenManager;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private TokenManager tokenManager;

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        return tokenManager.validateTokenSignature(authToken)
                .filter(User::getActive)
                .switchIfEmpty(Mono.error(new UnauthorizedException("User account is disabled."))).map(user-> new UsernamePasswordAuthenticationToken(
                        user.getPhoneNumber(),
                        null,
                        user.getAuthorities()
                ));

    }
}