package proyecto.security;

import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import proyecto.entity.Actor;
import proyecto.service.ActorService;

@Component
public class JWTUtils {
    private final ActorService actorService;
    private final SecretKey key;
    private final long expirationMillis;

    public JWTUtils(ActorService actorService,
                    @Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration-ms:3600000}") long expirationMillis) {
        this.actorService = actorService;
        byte[] bytes = Decoders.BASE64.decode(secret);
        if (bytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes aleatorios codificados en Base64");
        }
        if (expirationMillis < 60000) {
            throw new IllegalArgumentException("La duración mínima del JWT es de 60 segundos");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationMillis = expirationMillis;
    }

    public String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return StringUtils.hasText(header) && header.startsWith("Bearer ")
            ? header.substring(7) : null;
    }

    public boolean validateToken(String token) {
        try {
            return StringUtils.hasText(Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String generateToken(Authentication authentication) {
        Date issued = new Date();
        String authority = authentication.getAuthorities().stream()
            .findFirst().orElseThrow().getAuthority();
        return Jwts.builder()
            .subject(authentication.getName())
            .issuedAt(issued)
            .expiration(new Date(issued.getTime() + expirationMillis))
            .claim("rol", authority)
            .signWith(key, Jwts.SIG.HS256)
            .compact();
    }

    public String getUsernameOfToken(String token) {
        return Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).getPayload().getSubject();
    }

    @SuppressWarnings("unchecked")
    public <T extends Actor> T userLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !StringUtils.hasText(auth.getName())) {
            return null;
        }
        Optional<Actor> actor = actorService.findByUsername(auth.getName());
        if (actor.isEmpty() || actor.get().isBaneado()) return null;
        return (T) actor.get();
    }
}
