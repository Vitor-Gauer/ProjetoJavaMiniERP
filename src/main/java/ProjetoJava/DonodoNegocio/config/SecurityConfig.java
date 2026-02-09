package ProjetoJava.DonodoNegocio.config;

import ProjetoJava.DonodoNegocio.security.CustomUserDetailsService;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            // Libera recursos estáticos (Bootstrap, CSS, Imagens)
                            .requestMatchers("/webjars/**", "/css/**", "/js/**", "/assets/**", "/livereload.js").permitAll()

                            // Liberar páginas específicas
                            .requestMatchers("/", "/cadastro", "/login").permitAll()

                            // Qualquer outra requisição exige login
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .defaultSuccessUrl("/dashboard", true) // Redireciona para /dashboard após login
                            .permitAll()
                    )
                    .logout(LogoutConfigurer::permitAll)
                    .userDetailsService(userDetailsService) // Configura o UserDetailsService customizado
                    .exceptionHandling(ex -> ex
                            .accessDeniedPage("/error/403") // Redireciona para página de erro 403 customizada
                    );

            return http.build();
        } catch (Exception e) {
            throw new BeanCreationException("Error creating SecurityFilterChain", e);
        }
    }
}