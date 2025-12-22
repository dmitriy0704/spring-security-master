package dev.folomkin.springsecuritymaster.configs;

import dev.folomkin.springsecuritymaster.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/").anonymous()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/authenticated/**").authenticated()
                                .requestMatchers("/only_for_admins/**").hasRole("ADMIN")
                                .requestMatchers("/read_profile/**").hasAuthority("READ_PROFILE")
                                .anyRequest().permitAll()
                )
                .formLogin(form ->
                                form
//                                .loginPage("/login")
                                        .loginProcessingUrl("/login")
                                        .defaultSuccessUrl("/authenticated")
                                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/"))

        ;
        return http.build();
    }

//->     In Memory
//    @Bean
//    public UserDetailsService user() {
//        UserDetails user = User.builder()
//                .username("user")
//                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
//                .roles("ADMIN", "USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }

    //-> jdbcAuthentication
//    @Bean
//    public JdbcUserDetailsManager users(DataSource dataSource) {
//
//        // После создания пользователей отключаем их повторное создание
//
////        UserDetails user = User.builder()
////                .username("user")
////                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
////                .roles("USER")
////                .build();
////        UserDetails admin = User.builder()
////                .username("admin")
////                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
////                .roles("ADMIN", "USER")
////                .build();
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

    /// /        if (jdbcUserDetailsManager.userExists(user.getUsername())) {
    /// /            jdbcUserDetailsManager.deleteUser(user.getUsername());
    /// /        }
    /// /        if (jdbcUserDetailsManager.userExists(admin.getUsername())) {
    /// /            jdbcUserDetailsManager.deleteUser(admin.getUsername());
    /// /        }
    /// /        jdbcUserDetailsManager.createUser(admin);
    /// /        jdbcUserDetailsManager.createUser(user);
//        return jdbcUserDetailsManager;
//    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
     }

}
