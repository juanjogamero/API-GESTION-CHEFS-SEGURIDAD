package proyecto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import proyecto.entity.Cliente;
import proyecto.entity.Rol;
import proyecto.respository.ActorRepository;

class ActorServiceTest {
    @Test void laCuentaBaneadaQuedaDeshabilitada() {
        ActorRepository repository=mock(ActorRepository.class);
        Cliente cliente=new Cliente();
        cliente.setUsername("cliente");
        cliente.setPassword("hash");
        cliente.setRol(Rol.CLIENTE);
        cliente.setBaneado(true);
        when(repository.findByUsername("cliente")).thenReturn(Optional.of(cliente));
        var details=new ActorService(repository).loadUserByUsername("cliente");
        assertFalse(details.isEnabled());
    }
    @Test void noAutenticaCuentasAnonimizadas() {
        ActorRepository repository=mock(ActorRepository.class);
        Cliente cliente=new Cliente();
        cliente.setUsername("ANON_1");
        cliente.setPassword("irrelevante");
        cliente.setRol(null);
        when(repository.findByUsername("ANON_1")).thenReturn(Optional.of(cliente));
        assertThrows(UsernameNotFoundException.class,() ->
            new ActorService(repository).loadUserByUsername("ANON_1"));
    }
}
