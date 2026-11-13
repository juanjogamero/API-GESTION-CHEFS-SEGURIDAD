package proyecto.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.entity.Admin;
import proyecto.entity.Rol;
import proyecto.respository.AdminRepository;
import proyecto.security.JWTUtils;

@Service
public class AdminService {
    private final AdminRepository repository;
    private final PasswordEncoder encoder;
    private final JWTUtils jwt;
    public AdminService(AdminRepository repository, PasswordEncoder encoder, JWTUtils jwt) {
        this.repository = repository; this.encoder = encoder; this.jwt = jwt;
    }
    public Optional<Admin> findByUsername(String username) { return repository.findByUsername(username); }
    public Admin getAdminLogueado() { return jwt.userLogin(); }
    @Transactional
    public Admin save(Admin admin) {
        admin.setId(0); admin.setVersion(0);
        admin.setPassword(encoder.encode(admin.getPassword()));
        admin.setRol(Rol.ADMIN); admin.setBaneado(false);
        return repository.save(admin);
    }
    @Transactional
    public Admin update(Admin newData) {
        Admin current = getAdminLogueado();
        if (current == null) return null;
        if (newData.getPassword() != null && !newData.getPassword().isBlank())
            current.setPassword(encoder.encode(newData.getPassword()));
        current.setNombre(newData.getNombre());
        current.setPrimerApellido(newData.getPrimerApellido());
        current.setSegundoApellido(newData.getSegundoApellido());
        current.setEmail(newData.getEmail());
        current.setTelefono(newData.getTelefono());
        current.setImagenPerfil(newData.getImagenPerfil());
        return repository.save(current);
    }
    @Transactional
    public boolean deleteAndAnonymize() {
        Admin current = getAdminLogueado();
        if (current == null) return false;
        current.setUsername("ANON_" + current.getId());
        current.setPassword(encoder.encode(UUID.randomUUID().toString()));
        current.setNombre("ANONIMO");
        current.setPrimerApellido("ANONIMO");
        current.setSegundoApellido(null);
        current.setEmail("anonimo" + current.getId() + "@example.invalid");
        current.setTelefono(null);
        current.setImagenPerfil(null);
        current.setRol(null);
        current.setBaneado(true);
        repository.save(current);
        return true;
    }
}
