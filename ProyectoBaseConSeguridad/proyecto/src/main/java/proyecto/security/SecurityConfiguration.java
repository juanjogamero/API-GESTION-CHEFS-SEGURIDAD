package proyecto.security;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JWTAuthenticationFilter jwtFilter;

    public SecurityConfiguration(JWTAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                     PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration conf) throws Exception {
        return conf.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider provider) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(provider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                // Registro: solo POST sobre la colección, nunca POST /chef/{id}.
                .requestMatchers(HttpMethod.POST, "/chef", "/cliente").permitAll()
                .requestMatchers(HttpMethod.GET, "/chef/miPerfil").hasAuthority("CHEF")
                .requestMatchers(HttpMethod.GET, "/cliente/miPerfil").hasAuthority("CLIENTE")
                .requestMatchers(HttpMethod.GET, "/chef", "/chef/listadoActivos", "/chef/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/noticia", "/noticia/{id}").permitAll()
                .requestMatchers(HttpMethod.PUT, "/banear/{actorId}", "/desbanear/{actorId}").hasAuthority("ADMIN")
                .requestMatchers("/admin", "/admin/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/cliente", "/cliente/{id}").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/chef", "/chef/activar", "/chef/desactivar").hasAuthority("CHEF")
                .requestMatchers(HttpMethod.DELETE, "/chef").hasAuthority("CHEF")
                .requestMatchers(HttpMethod.PUT, "/cliente").hasAuthority("CLIENTE")
                .requestMatchers(HttpMethod.DELETE, "/cliente").hasAuthority("CLIENTE")
                .requestMatchers(HttpMethod.POST, "/noticia").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/noticia/{id}").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/noticia/{id}").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/servicio/contratar/{chefId}").hasAuthority("CLIENTE")
                .requestMatchers(HttpMethod.PUT, "/servicio/puntuar/{servicioId}/{puntuacion}").hasAuthority("CLIENTE")
                .requestMatchers(HttpMethod.GET, "/servicio/misServiciosChef").hasAuthority("CHEF")
                .requestMatchers(HttpMethod.GET, "/servicio/misServiciosCliente").hasAuthority("CLIENTE")
                .anyRequest().denyAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req,res,e) -> res.sendError(401, "Autenticación requerida"))
                .accessDeniedHandler((req,res,e) -> res.sendError(403, "Permisos insuficientes")))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
            .map(String::trim).filter(s -> !s.isEmpty()).toList());
        conf.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        conf.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
        conf.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }
}
