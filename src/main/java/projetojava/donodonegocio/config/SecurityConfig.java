package projetojava.donodonegocio.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import projetojava.donodonegocio.security.CustomUserDetails;
import projetojava.donodonegocio.security.JwtAuthenticationFilter;
import projetojava.donodonegocio.service.AuditService;
import projetojava.donodonegocio.service.JwtService;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final Environment env;
    private final AuditService auditService;
    private final JwtService jwtService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(Environment env, AuditService auditService, JwtService jwtService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.env = env;
        this.auditService = auditService;
        this.jwtService = jwtService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                    )
                    .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> {
                        boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");

                        if (isDev) {
                            auth.requestMatchers("/livereload.js").permitAll();
                        }

                        // Libera recursos estáticos (Bootstrap, CSS, Imagens)
                        auth.requestMatchers("/webjars/**", "/css/**", "/js/**", "/assets/**").permitAll();

                        // Liberar páginas de login e cadastro
                        auth.requestMatchers("/", "/cadastro", "/login/**", "/api/test/**").permitAll();

                        // Qualquer outra requisição exige login
                        auth.anyRequest().authenticated();
                    })
                    .sessionManagement(session -> session
                            .sessionFixation().migrateSession()
                            .maximumSessions(1)
                            .maxSessionsPreventsLogin(false)
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessHandler((request, response, authentication) -> {
                                if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails cud) {
                                    auditService.logLogout(cud);
                                }
                                // Clear JWT cookie
                                response.addHeader("Set-Cookie", "jwt=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
                                response.sendRedirect("/login");
                            })
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll()
                    )
                    .exceptionHandling(ex -> ex
                            .accessDeniedPage("/error/403")
                    );

            return http.build();
        } catch (Exception e) {
            throw new BeanCreationException("Error creating SecurityFilterChain", e);
        }
    }
}