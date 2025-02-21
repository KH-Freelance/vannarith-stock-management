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
        "/report/**",
        "/purchase/**",
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
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                
                                .requestMatchers(GET, "/user/current-info").authenticated()
                                
                                
                                .requestMatchers(GET,"/stock/**").hasAnyAuthority("stock:READ", "*")
                                .requestMatchers(GET,"/stock/product/**").hasAnyAuthority("stock:UPDATE","stock:CREATE", "*")
                                .requestMatchers(POST,"/stock/**").hasAnyAuthority("stock:CREATE", "*")
                                .requestMatchers(DELETE,"/stock/**").hasAnyAuthority("stock:DELETE", "*")
                                .requestMatchers(PUT,"/stock/**").hasAnyAuthority("stock:UPDATE", "*")

                                .requestMatchers(GET,"/product/**").hasAnyAuthority("product:READ", "*")
                                .requestMatchers(POST,"/product/**").hasAnyAuthority("product:CREATE", "*")
                                .requestMatchers(DELETE,"/product/**").hasAnyAuthority("product:DELETE", "*")
                                .requestMatchers(PUT,"/product/**").hasAnyAuthority("product:UPDATE", "*")
                                
                                .requestMatchers(GET,"/customer/**").hasAnyAuthority("customer:READ", "*")
                                .requestMatchers(POST,"/customer/**").hasAnyAuthority("customer:CREATE", "*")
                                .requestMatchers(DELETE,"/customer/**").hasAnyAuthority("customer:DELETE", "*")
                                .requestMatchers(PUT,"/customer/**").hasAnyAuthority("customer:UPDATE", "*")

                                .requestMatchers(GET,"/user/**").hasAnyAuthority("user:READ", "*")
                                .requestMatchers(GET,"/user/role/**").hasAnyAuthority("user:CREATE","user:UPDATE", "*")
                                .requestMatchers(POST,"/user/**").hasAnyAuthority("user:CREATE", "*")
                                .requestMatchers(DELETE,"/user/**").hasAnyAuthority("user:DELETE", "*")
                                .requestMatchers(PUT,"/user/**").hasAnyAuthority("user:UPDATE", "*")

                                .requestMatchers(GET,"/purchase/return/**").hasAnyAuthority("return:READ", "*")
                                .requestMatchers(POST,"/purchase/return").hasAnyAuthority("return:CREATE", "*")
                                .requestMatchers(DELETE,"/purchase/return/**").hasAnyAuthority("return:DELETE", "*")
                                .requestMatchers(PUT,"/purchase/return/**").hasAnyAuthority("return:UPDATE", "*")

                                .requestMatchers(GET,"/purchase/**").hasAnyAuthority("sale:READ", "*")
                                .requestMatchers(GET,"/purchase/product/**").hasAnyAuthority("sale:CREATE","sale:UPDATE", "*")
                                .requestMatchers(GET,"/purchase/customer/**").hasAnyAuthority("sale:CREATE","sale:UPDATE", "*")
                                .requestMatchers(POST,"/purchase/**").hasAnyAuthority("sale:CREATE", "*")
                                .requestMatchers(DELETE,"/purchase/**").hasAnyAuthority("sale:DELETE", "*")
                                .requestMatchers(PUT,"/purchase/**").hasAnyAuthority("sale:UPDATE", "*")

                                .requestMatchers(GET,"/role/**").hasAnyAuthority("role:READ", "*")
                                .requestMatchers(POST,"/role/**").hasAnyAuthority("role:CREATE", "*")
                                .requestMatchers(DELETE,"/role/**").hasAnyAuthority("role:DELETE", "*")
                                .requestMatchers(PUT,"/role/**").hasAnyAuthority("role:UPDATE", "*")

                                .requestMatchers(GET,"/report/**").hasAnyAuthority("report:READ", "*")
                                .requestMatchers(POST,"/report/**").hasAnyAuthority("report:CREATE", "*")
                                .requestMatchers(DELETE,"/report/**").hasAnyAuthority("report:DELETE", "*")
                                .requestMatchers(PUT,"/report/**").hasAnyAuthority("report:UPDATE", "*")
                                
                                .requestMatchers(GET,"/data/**").hasAnyAuthority("data:READ", "*")
                                .requestMatchers(POST,"/data/**").hasAnyAuthority("data:CREATE", "*")
                                .requestMatchers(DELETE,"/data/**").hasAnyAuthority("data:DELETE", "*")
                                .requestMatchers(PUT,"/data/**").hasAnyAuthority("data:UPDATE", "*")

                                .requestMatchers(GET,"/data/**").hasAnyAuthority("data:READ", "*")
                                .requestMatchers(POST,"/data/**").hasAnyAuthority("data:CREATE", "*")
                                .requestMatchers(DELETE,"/data/**").hasAnyAuthority("data:DELETE", "*")
                                .requestMatchers(PUT,"/data/**").hasAnyAuthority("data:UPDATE", "*")

                                .requestMatchers(GET,"/configuration/**").hasAnyAuthority("configuration:READ", "*")
                                .requestMatchers(POST,"/configuration/**").hasAnyAuthority("configuration:CREATE", "*")
                                .requestMatchers(DELETE,"/configuration/**").hasAnyAuthority("configuration:DELETE", "*")
                                .requestMatchers(PUT,"/configuration/**").hasAnyAuthority("configuration:UPDATE", "*")

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
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
