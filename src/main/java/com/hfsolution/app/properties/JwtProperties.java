package com.hfsolution.app.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Configuration
@ConfigurationProperties(prefix = "application.security.jwt")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtProperties {
    String secretKey;
    long expiration;
    RefreshToken refreshToken;
    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RefreshToken {
        long expiration;
    }

}
