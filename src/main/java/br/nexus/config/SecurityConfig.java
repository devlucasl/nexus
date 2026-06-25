package br.nexus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/login",
                                "/cadastro",
                                "/h2-console/**",
                                "/api/auth/login"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/",
                                "/dashboard",
                                "/produtos",
                                "/vendas",
                                "/vendas/**"
                        ).authenticated()

                        .requestMatchers(HttpMethod.GET,
                                "/api/produtos",
                                "/api/produtos/**",
                                "/api/disponibilidade/**",
                                "/api/vendas",
                                "/api/vendas/**"
                        ).authenticated()

                        .requestMatchers(HttpMethod.POST,
                                "/produtos/criar",
                                "/produtos/*/editar",
                                "/produtos/*/excluir",
                                "/produtos/*/disponibilidade"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST,
                                "/api/produtos",
                                "/api/produtos/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/produtos/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/produtos/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                "/usuarios",
                                "/usuarios/**",
                                "/estoque",
                                "/estoque/**",
                                "/categorias",
                                "/categorias/**",
                                "/fornecedores",
                                "/fornecedores/**",
                                "/promocoes",
                                "/promocoes/**",
                                "/relatorios",
                                "/relatorios/**",
                                "/api/usuarios/**",
                                "/api/estoque/**",
                                "/api/categorias/**",
                                "/api/fornecedores/**",
                                "/api/promocoes/**",
                                "/api/relatorios/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST,
                                "/vendas/*/cancelar",
                                "/api/vendas/*/cancelar"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                "/vendas",
                                "/vendas/**",
                                "/api/vendas",
                                "/api/vendas/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}