package ru.timestop.video.generator.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import ru.timestop.video.generator.gateway.model.BasicAuthProperties;

import static org.springframework.security.config.Customizer.withDefaults;


/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);
    private final static String OPERATOR_ROLE = "Operator";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityWebFilterChain defaultCallbackSecurityFilterChain(ServerHttpSecurity http) {
        http.securityMatcher(new PathPatternParserServerWebExchangeMatcher("/callback", HttpMethod.GET))
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange()
                        .authenticated())
                .httpBasic(withDefaults())
                .formLogin(withDefaults());
        return http.build();
    }

    @Bean
    public SecurityWebFilterChain permitAllSecurityFilterChain(ServerHttpSecurity http) {
        http.securityMatcher(ServerWebExchangeMatchers.anyExchange())
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll());
        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(BasicAuthProperties basicAuthProperties) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String name = basicAuthProperties.getName();
        String password = basicAuthProperties.getPassword();
        String encoded_password =  encoder.encode(password);
        LOGGER.info("#### ({}/{}) -> {} ####", name, password, encoded_password); // TODO delete or change!
        UserDetails user = User.builder()
                .username(name)
                .password(encoded_password)
                .roles(OPERATOR_ROLE)
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}
