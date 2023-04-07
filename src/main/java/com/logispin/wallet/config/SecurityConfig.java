package com.logispin.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public UserDetailsService userDetailsService() {
    var uds = new InMemoryUserDetailsManager();

    var u1 =
        User.withUsername("readuser")
            .password(passwordEncoder().encode("12345"))
            .authorities("read")
            .build();

    var u2 =
        User.withUsername("user")
            .password(passwordEncoder().encode("12345"))
            .authorities("write", "read")
            .build();

    uds.createUser(u1);
    uds.createUser(u2);

    return uds;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.httpBasic()
        .and()
        .authorizeHttpRequests()
        .requestMatchers("/h2-console/**")
        .permitAll()
        .requestMatchers(HttpMethod.GET, "/wallets/**")
        .hasAuthority("read")
        .requestMatchers(HttpMethod.PUT, "/wallets/**")
        .hasAuthority("write")
        .requestMatchers(HttpMethod.POST, "/wallets")
        .hasAuthority("write")
        .requestMatchers("/actuator/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable() // DON'T DO THIS IN READ-WORLD APPS
        .build();
  }
}
