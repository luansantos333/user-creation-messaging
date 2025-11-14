package com.userapplication.config.authenticationconfig;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.userapplication.config.customauthenticationprovider.CustomOAuth2Provider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthenticationConfig {
    private final CustomOAuth2Provider customOAuth2Provider;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public AuthenticationConfig(CustomOAuth2Provider customOAuth2Provider, JwtAuthenticationConverter jwtAuthenticationConverter) {

        this.customOAuth2Provider = customOAuth2Provider;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Profile("test")
    public SecurityFilterChain h2InMemoryDatabaseFilter(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .securityMatcher("/h2-console/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .build();

    }

    @Bean
    @Order(2)
    public SecurityFilterChain authorizationServerFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher()).with(authorizationServerConfigurer, (oauth2) -> {

            oauth2.oidc(Customizer.withDefaults());
        });
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated()).exceptionHandling(handler -> {

            handler.defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/login"), new MediaTypeRequestMatcher(MediaType.TEXT_HTML));
        });

        http.authenticationProvider(customOAuth2Provider);

        return http.build();

    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorize) -> authorize.requestMatchers(HttpMethod.POST, "/api/user").permitAll().requestMatchers(HttpMethod.POST, "/api/user/reset/token/**").permitAll().requestMatchers("/actuator/**")
                        .permitAll().
                        anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .csrf(c -> c.disable())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)).accessDeniedHandler((request, response, exception) -> {

                    response.setStatus(403);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"error\":\"Access Denied\",\"message\":\"" + exception.getMessage() + "\"}");
                }).authenticationEntryPoint((request, response, authException) -> {

                    response.setStatus(401);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + authException.getMessage() + "\"}");


                }))
                .httpBasic(Customizer.withDefaults());

        return http.build();

    }


    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public org.springframework.security.web.firewall.HttpFirewall allowSemicolonHttpFirewall() {
        org.springframework.security.web.firewall.StrictHttpFirewall firewall = new org.springframework.security.web.firewall.StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }


    @Bean
    JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {

        KeyPair keyPair = generateKeyPairRSAKeys();
        RSAKey rsakey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).privateKey((RSAPrivateKey) keyPair.getPrivate()).keyID(UUID.randomUUID().toString()).build();
        JWKSet jwkSet = new JWKSet(rsakey);

        return new ImmutableJWKSet<>(jwkSet);


    }

    private KeyPair generateKeyPairRSAKeys() throws NoSuchAlgorithmException {

        KeyPair keypar;
        try {

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keypar = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new NoSuchAlgorithmException(e.getMessage());
        }

        return keypar;


    }


}
