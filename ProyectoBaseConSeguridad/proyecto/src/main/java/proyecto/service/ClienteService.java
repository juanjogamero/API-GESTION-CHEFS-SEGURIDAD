package proyecto.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.entity.Cliente;
import proyecto.entity.Rol;
import proyecto.respository.ClienteRepository;
import proyecto.security.JWTUtils;

@Service
public class ClienteService {
    private final ClienteRepository repository;
    private final PasswordEncoder encoder;
    private final JWTUtils jwt;
    public ClienteService(ClienteRepository repository, PasswordEncoder encoder, JWTUtils jwt) {
        this.repository = repository; this.encoder = encoder; this.jwt = jwt;
    }
    public Optional<Cliente> findByUsername(String username) { return repository.findByUsername(username); }
    public List<Cliente> findAll() { return repository.findAll(); }
    public Optional<Cliente> findById(int id) { return repository.findById(id); }
    public Cliente getClienteLogueado() { return jwt.userLogin(); }
    @Transactional
    public Cliente save(Cliente cliente) {
        cliente.setId(0); cliente.setVersion(0);
        cliente.setPassword(encoder.encode(cliente.getPassword()));
        cliente.setRol(Rol.CLIENTE); cliente.setBaneado(false);
        cliente.setServiciosContratados(new ArrayList<>());
        return repository.save(cliente);
    }
    @Transactional
    public Cliente saveBasico(Cliente cliente) { return repository.save(cliente); }
    @Transactional
    public Cliente update(Cliente data) {
        Cliente cliente = getClienteLogueado();
        if (cliente == null) return null;
        if (data.getPassword() != null && !data.getPassword().isBlank())
            cliente.setPassword(encoder.encode(data.getPassword()));
        cliente.setNombre(data.getNombre());
        cliente.setPrimerApellido(data.getPrimerApellido());
        cliente.setSegundoApellido(data.getSegundoApellido());
        cliente.setEmail(data.getEmail());
        cliente.setTelefono(data.getTelefono());
        cliente.setImagenPerfil(data.getImagenPerfil());
        cliente.setDatosMedicos(data.getDatosMedicos());
        cliente.setDireccionPostal(data.getDireccionPostal());
        return repository.save(cliente);
    }
    @Transactional
    public boolean deleteAndAnonymize() {
        Cliente cliente = getClienteLogueado();
        if (cliente == null) return false;
        cliente.setUsername("ANON_" + cliente.getId());
        cliente.setPassword(encoder.encode(UUID.randomUUID().toString()));
        cliente.setNombre("ANONIMO");
        cliente.setPrimerApellido("ANONIMO");
        cliente.setSegundoApellido(null);
        cliente.setEmail("anonimo" + cliente.getId() + "@example.invalid");
        cliente.setTelefono(null); cliente.setImagenPerfil(null);
        cliente.setDatosMedicos(null); cliente.setDireccionPostal("ANONIMO");
        cliente.setRol(null); cliente.setBaneado(true);
        return repository.save(cliente) != null;
    }
}
