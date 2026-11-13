package proyecto.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import proyecto.service.ActorService;
import java.util.List;
import java.util.Base64;

class JWTUtilsTest {
    private static final String KEY = Base64.getEncoder()
        .encodeToString("0123456789abcdef0123456789abcdef".getBytes());

    @Test void tokenValidoYTokenManipulado() {
        JWTUtils jwt = new JWTUtils(mock(ActorService.class),KEY,3600000);
        var auth = new UsernamePasswordAuthenticationToken("cliente1",null,
            List.of(new SimpleGrantedAuthority("CLIENTE")));
        String token = jwt.generateToken(auth);
        assertTrue(jwt.validateToken(token));
        assertEquals("cliente1",jwt.getUsernameOfToken(token));
        assertFalse(jwt.validateToken(token.substring(0,token.length()-5)+"abcde"));
    }

    @Test void claveDebilSeRechazaAlArrancar() {
        assertThrows(IllegalArgumentException.class, () ->
            new JWTUtils(mock(ActorService.class),Base64.getEncoder().encodeToString("1234".getBytes()),3600000));
    }
}
