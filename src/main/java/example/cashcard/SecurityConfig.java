package example.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  private static final String pwd = "abc123";

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(request -> request
            .requestMatchers("/cashcards/**")
            .hasRole("CARD-OWNER"))
        .csrf(csrf -> csrf.disable())
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
    User.UserBuilder userBuilder = User.builder();

    UserDetails sarah = userBuilder
        .username("sarah1")
        .password(passwordEncoder.encode(pwd))
        .roles("CARD-OWNER")
        .build();

    UserDetails hank = userBuilder
        .username("hank-non-owner")
        .password(passwordEncoder.encode(pwd))
        .roles("NON-OWNER")
        .build();

    UserDetails kumar = userBuilder
        .username("kumar2")
        .password(passwordEncoder.encode(pwd))
        .roles("CARD-OWNER")
        .build();

    return new InMemoryUserDetailsManager(sarah, hank, kumar);
  }
}
