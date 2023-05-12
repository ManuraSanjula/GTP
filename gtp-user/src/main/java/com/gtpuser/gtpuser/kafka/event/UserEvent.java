package com.gtpuser.gtpuser.kafka.event;

import java.util.List;
import java.util.UUID;

public record UserEvent(
        UUID id,
        Boolean accountNonLocked,
        Boolean accountNonExpired,
        String name,
        String phoneNumber,
        Boolean phoneNumberVerify,
        String pic,
        Boolean active,
        List<String> roles,
        java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities
) {
}