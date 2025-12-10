package dev.folomkin.springsecuritymaster.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/").anonymous()
                                .requestMatchers("/authenticated/**").authenticated()
                                .requestMatchers("/only_for_admins/**").hasRole("ADMIN")
                                .requestMatchers("/read_profile/**").hasAuthority("READ_PROFILE")
                                .anyRequest().permitAll()
                )
                .formLogin(Customizer.withDefaults())
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
//<!-        UserDetails user = User.builder()
//                .username("user")
//                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
//                .roles("ADMIN", "USER")
//                .build();
//->
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//<!-        if (jdbcUserDetailsManager.userExists(user.getUsername())) {
//            jdbcUserDetailsManager.deleteUser(user.getUsername());
//        }
//        if (jdbcUserDetailsManager.userExists(admin.getUsername())) {
//            jdbcUserDetailsManager.deleteUser(admin.getUsername());
//        }
//        jdbcUserDetailsManager.createUser(admin);
//->       jdbcUserDetailsManager.createUser(user);
//        return jdbcUserDetailsManager;
//    }



    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        return authenticationProvider;
    }

}

