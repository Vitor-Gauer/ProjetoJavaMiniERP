package ProjetoJava.DonodoNegocio.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            // Libera recursos estáticos (Bootstrap, CSS, Imagens)
                            .requestMatchers("/webjars/**", "/css/**", "/js/**", "/assets/**", "/livereload.js").permitAll()

                            // Liberar páginas específicas
                            .requestMatchers("/", "/cadastro").permitAll()

                            // Qualquer outra requisição exige login
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .permitAll()
                    )

                    .logout(LogoutConfigurer::permitAll);

            return http.build();
        } catch (Exception e) {
            throw new BeanCreationException("Error creating SecurityFilterChain", e);
        }
    }
}