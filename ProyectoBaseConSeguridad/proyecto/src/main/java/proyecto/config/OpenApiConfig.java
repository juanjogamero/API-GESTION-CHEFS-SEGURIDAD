package proyecto.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

@Configuration
@OpenAPIDefinition(info = @Info(
    title = "Gestión de Chefs · Seguridad REST",
    version = "1.0.0",
    description = "API educativa de contratación de servicios gastronómicos con autenticación JWT y permisos por rol."
))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Token de acceso obtenido mediante POST /login"
)
public class OpenApiConfig { }
