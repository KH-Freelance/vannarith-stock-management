package com.hfsolution.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import com.hfsolution.app.filter.JwtAuthenticationFilter;
import static com.hfsolution.feature.user.enums.Permission.ADMIN_CREATE;
import static com.hfsolution.feature.user.enums.Permission.ADMIN_DELETE;
import static com.hfsolution.feature.user.enums.Permission.ADMIN_READ;
import static com.hfsolution.feature.user.enums.Permission.ADMIN_UPDATE;
import static com.hfsolution.feature.user.enums.Permission.MANAGER_CREATE;
import static com.hfsolution.feature.user.enums.Permission.MANAGER_DELETE;
import static com.hfsolution.feature.user.enums.Permission.MANAGER_READ;
import static com.hfsolution.feature.user.enums.Permission.MANAGER_UPDATE;
import static com.hfsolution.feature.user.enums.Role.ADMIN;
import static com.hfsolution.feature.user.enums.Role.MANAGER;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {


    private static final String[] WHITE_LIST_URL = {
        "/auth/**",
        "/v2/api-docs",
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui/**",
        "/webjars/**",
        "/swagger-ui.html"};
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exceptionHandling ->
                exceptionHandling
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        throw new AccessDeniedException("Access Denied");
                    })
            )
                // .exceptionHandling(exception -> exception.accessDeniedHandler(new CustomAccessDeniedException())
                // .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                
                                
                                // Endpoint for management and admin
                                .requestMatchers("/user/**").hasAnyRole(MANAGER.name(),ADMIN.name())
                                // .requestMatchers("/users/change-role/**").hasAnyRole(ADMIN.name())

                                // .requestMatchers(DELETE,"/users/**").hasAnyAuthority(ADMIN_DELETE.name(),MANAGER_DELETE.name())
                                // .requestMatchers(POST,"/users/**").hasAnyAuthority(ADMIN_CREATE.name(),MANAGER_CREATE.name())
                                // .requestMatchers(PUT, "/users/**").hasAnyAuthority(ADMIN_UPDATE.name(),MANAGER_UPDATE.name())
                                // .requestMatchers(GET,"/users/**").hasAnyAuthority(ADMIN_READ.name())
                                
                                // Endpoint for admin
                                // .requestMatchers(PUT,"/users/change-role/**").hasAnyAuthority(ADMIN_UPDATE.name())

                                // Any Endpoint
                                .anyRequest()
                                .authenticated()
                )
                
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
        ;

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));  // Allow all origins, or specify specific ones
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
