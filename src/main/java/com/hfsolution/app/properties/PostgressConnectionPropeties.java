package com.hfsolution.app.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "postgress-connection")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostgressConnectionPropeties {

    private String jdbcUrl;
    private String driverClassName;
    private String username;
    private String password;
    private String maximumPoolSize;
    private String connectionTimeout;
    private String idleTimeout;
    private String minimumIdle;
    private String maxLifetime;
    private String keepAlive;
    
}