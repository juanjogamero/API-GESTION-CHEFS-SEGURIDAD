package proyecto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import proyecto.entity.Admin;
import proyecto.service.AdminService;
import proyecto.service.ActorService;

@Configuration
public class AdminBootstrap {
    @Bean
    CommandLineRunner provisionAdmin(ActorService actors, AdminService admins,
                                      @Value("${APP_ADMIN_USER:}") String username,
                                      @Value("${APP_ADMIN_PASSWORD:}") String password) {
        return args -> {
            if (username.isBlank() && password.isBlank()) return;
            if (username.isBlank() || password.length() < 12)
                throw new IllegalArgumentException("APP_ADMIN_USER y APP_ADMIN_PASSWORD (12+ caracteres) son obligatorios juntos");
            if (actors.findByUsername(username).isPresent()) return;
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(password);
            admin.setNombre("Administrador");
            admin.setPrimerApellido("Local");
            admin.setEmail("admin@localhost.invalid");
            admins.save(admin);
        };
    }
}
