package cl.duoc.pedidos360.bff.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtAudienceValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Value("${pedidos360.security.issuer}")
    private String issuer;

    @Value("${pedidos360.security.audience}")
    private String audience;

    @Value("${pedidos360.security.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .cors(Customizer.withDefaults())

            .csrf(csrf ->
                csrf.disable()
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/api/public/**"
                )
                .permitAll()

                .requestMatchers(
                    "/api/**"
                )
                .hasAuthority(
                    "SCOPE_access_as_user"
                )

                .anyRequest()
                .authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(
                    Customizer.withDefaults()
                )
            );

        return http.build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {

        NimbusJwtDecoder decoder =
            NimbusJwtDecoder
                .withJwkSetUri(
                    jwkSetUri
                )
                .build();


        OAuth2TokenValidator<Jwt>
            issuerValidator =
                JwtValidators
                    .createDefaultWithIssuer(
                        issuer
                    );


        OAuth2TokenValidator<Jwt>
            audienceValidator =
                new JwtAudienceValidator(
                    audience
                );


        OAuth2TokenValidator<Jwt>
            validator =
                new DelegatingOAuth2TokenValidator<>(
                    issuerValidator,
                    audienceValidator
                );


        decoder.setJwtValidator(
            validator
        );


        return decoder;
    }


    @Bean
    public CorsConfigurationSource
        corsConfigurationSource() {

        CorsConfiguration configuration =
            new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:4200"
            )
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type"
            )
        );

        configuration.setAllowCredentials(
            true
        );


        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );


        return source;
    }
}