package com.broker.orders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @EnableWebSecurity: Diz ao Spring para carregar a infraestrutura de segurança web.
 * OBJECTIVE 5.3: @EnableMethodSecurity ativa a segurança baseada em anotações nos métodos (ex: @PreAuthorize).
 * EDGE CASE EXAME: Em versões antigas (Spring Boot 2.x), você usaria @EnableGlobalMethodSecurity(prePostEnabled = true).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * ALERTA DE EXAME (A Mudança Arquitetural Crítica):
     * Na prova (versão 2V0-72.22), você pode ver menções à classe `WebSecurityConfigurerAdapter`.
     * Saiba que ela foi DEPRECIADA. O padrão moderno (Spring 5.7+) é declarar um Bean
     * do tipo SecurityFilterChain, configurando a segurança via injeção.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilitando CSRF porque nossa API é REST/Stateless.
                // O exame pode perguntar quando usar CSRF: Resposta = Em aplicações com interface Web renderizada no servidor (Thymeleaf, JSP) que usam cookies de sessão.
                .csrf(csrf -> csrf.disable())

                // OBJECTIVE 5.2: Configure Authorization
                .authorizeHttpRequests(authz -> authz
                        // Permite que qualquer um acesse as documentações ou um endpoint de "ping"
                        .requestMatchers("/api/public/**").permitAll()

                        // Exige a role ADMIN para qualquer requisição DELETE no nosso controller
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasRole("ADMIN")

                        // Exige autenticação para qualquer outra requisição
                        .anyRequest().authenticated()
                )
                // OBJECTIVE 5.2: Configure Authentication (Usando HTTP Basic para simplificar)
                .httpBasic(withDefaults());

        return http.build();
    }

    /**
     * Definindo nossos usuários em memória (No mundo real, buscaríamos do banco com um JpaUserDetailsService)
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // Criando um cliente normal
        UserDetails user = User.builder()
                .username("cliente")
                .password(encoder.encode("senha123"))
                .roles("USER") // O Spring converte internamente para "ROLE_USER"
                .build();

        // Criando um administrador
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN", "USER")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * O Spring Security EXIGE um PasswordEncoder.
     * O BCrypt é o padrão ouro da indústria e sempre aparece nas provas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}