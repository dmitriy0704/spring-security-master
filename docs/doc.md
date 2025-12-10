# Краткие заметки по ходу изучения Spring Security

## Создание пользователя в памяти:

```java
package dev.folomkin.springsecuritymaster.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

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
                                .anyRequest().permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .exceptionHandling(
                        ex -> ex
                                .accessDeniedPage("/access-denied")// Страница для 403
                )
        ;
        return http.build();
    }

    @Bean
    public UserDetailsService user() {
        UserDetails user = User.builder()
                .username("user")
                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
                .roles("ADMIN", "USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}

```

## Аутентификация из БД

```java
 //-> jdbcAuthentication
    @Bean
    public JdbcUserDetailsManager users(DataSource dataSource) {

        UserDetails user = User.builder()
                .username("user")
                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password("{bcrypt}$2a$12$td0Xz.UqCHMEqady3ay9huVMG.PvmMohsRbmw0HB8bkWwg37vhpqm")
                .roles("ADMIN", "USER")
                .build();

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        if (jdbcUserDetailsManager.userExists(user.getUsername())) {
            jdbcUserDetailsManager.deleteUser(user.getUsername());
        }
        if (jdbcUserDetailsManager.userExists(admin.getUsername())) {
            jdbcUserDetailsManager.deleteUser(admin.getUsername());
        }
        jdbcUserDetailsManager.createUser(admin);
        jdbcUserDetailsManager.createUser(user);
        return jdbcUserDetailsManager;
    }
```

## Запросы для создания таблиц пользователей и админов

```sql
create table users
(
    username varchar(255) not null primary key,
    password varchar(255) not null,
    enabled  boolean      not null
);

create table authorities
(
    username  varchar(255) not null,
    authority varchar(255) not null,
    foreign key (username) references users (username),
    unique (username, authority)
);

insert into authorities (username, authority)
values ('user', 'READ_PROFILE');
```

## Запрос для DAO:

```sql
create table users (
  id                    bigserial,
  username              varchar(30) not null,
  password              varchar(80) not null,
  email                 varchar(50) unique,
  primary key (id)
);

create table roles (
  id                    serial,
  name                  varchar(50) not null,
  primary key (id)
);

CREATE TABLE users_roles (
  user_id               bigint not null,
  role_id               int not null,
  primary key (user_id, role_id),
  foreign key (user_id) references users (id),
  foreign key (role_id) references roles (id)
);

insert into roles (name)
values
('ROLE_USER'), ('ROLE_ADMIN');

insert into users (username, password, email)
values
('user', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'user@gmail.com');

insert into users_roles (user_id, role_id)
values
(1, 1),
(1, 2);
```