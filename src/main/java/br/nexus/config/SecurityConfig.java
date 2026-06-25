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

                        // Arquivos públicos
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

                        // Telas públicas para usuários autenticados: ADMINISTRADOR e FUNCIONARIO
                        .requestMatchers(HttpMethod.GET,
                                "/",
                                "/dashboard",
                                "/produtos",
                                "/vendas",
                                "/vendas/**"
                        ).authenticated()

                        // Consulta de produtos pela API: ADMINISTRADOR e FUNCIONARIO
                        .requestMatchers(HttpMethod.GET,
                                "/api/produtos",
                                "/api/produtos/**",
                                "/api/disponibilidade/**",
                                "/api/vendas",
                                "/api/vendas/**"
                        ).authenticated()

                        // Ações administrativas em produtos
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

                        // Estoque: somente administrador
                        .requestMatchers(
                                "/estoque",
                                "/estoque/**",
                                "/api/estoque/**"
                        ).hasRole("ADMINISTRADOR")

                        // Categorias: somente administrador
                        .requestMatchers(
                                "/categorias",
                                "/categorias/**",
                                "/api/categorias/**"
                        ).hasRole("ADMINISTRADOR")

                        // Fornecedores: somente administrador
                        .requestMatchers(
                                "/fornecedores",
                                "/fornecedores/**",
                                "/api/fornecedores/**"
                        ).hasRole("ADMINISTRADOR")

                        // Promoções: somente administrador
                        .requestMatchers(
                                "/promocoes",
                                "/promocoes/**",
                                "/api/promocoes/**"
                        ).hasRole("ADMINISTRADOR")

                        // Relatórios: somente administrador
                        .requestMatchers(
                                "/relatorios",
                                "/relatorios/**",
                                "/api/relatorios/**"
                        ).hasRole("ADMINISTRADOR")

                        // Cancelamento de venda: somente administrador
                        .requestMatchers(HttpMethod.POST,
                                "/vendas/*/cancelar",
                                "/api/vendas/*/cancelar"
                        ).hasRole("ADMINISTRADOR")

                        // Demais ações de venda: usuário autenticado
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