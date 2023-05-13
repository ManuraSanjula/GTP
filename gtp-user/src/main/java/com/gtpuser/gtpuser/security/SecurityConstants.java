package com.gtpuser.gtpuser.security;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 360000; // 10 min
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
