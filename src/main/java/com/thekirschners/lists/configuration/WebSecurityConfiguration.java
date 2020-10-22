package com.thekirschners.lists.configuration;

import com.auth0.jwk.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .cors().and()
                .formLogin().disable()
                .httpBasic().disable()

                .authorizeRequests().anyRequest().authenticated().and()
                .oauth2ResourceServer()
                .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(new Converter<Jwt, AbstractAuthenticationToken>() {
                    @Override
                    public AbstractAuthenticationToken convert(Jwt source) {
                        return new UsernamePasswordAuthenticationToken(
                                source.getSubject(),
                                null,
                                List.of()
                        );
                    }
                }));
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
        configuration.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return token -> {
            DecodedJWT jwt = JWT.decode(token);
            JwkProvider provider = new UrlJwkProvider("https://dev-pjs46xuy.eu.auth0.com");
            try {
                Jwk jwk = provider.get(jwt.getKeyId());
                Algorithm algorithm = null;
                algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
                algorithm.verify(jwt);
            } catch (JwkException e) {
                throw new IllegalStateException(e);
            }

            return Jwt.withTokenValue(token)
                    .audience(jwt.getAudience())
                    .issuer(jwt.getIssuer())
                    .subject(jwt.getSubject())
                    .issuedAt(jwt.getIssuedAt().toInstant())
                    .expiresAt(jwt.getExpiresAt().toInstant())
                    .header("x", "y")
                    .build();
        };
    }
}
