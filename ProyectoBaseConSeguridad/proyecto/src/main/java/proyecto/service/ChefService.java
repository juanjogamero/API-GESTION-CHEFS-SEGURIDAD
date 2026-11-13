package proyecto.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.entity.Chef;
import proyecto.entity.Rol;
import proyecto.respository.ChefRepository;
import proyecto.security.JWTUtils;

@Service
public class ChefService {
    private final ChefRepository repository;
    private final PasswordEncoder encoder;
    private final JWTUtils jwt;
    public ChefService(ChefRepository repository, PasswordEncoder encoder, JWTUtils jwt) {
        this.repository = repository; this.encoder = encoder; this.jwt = jwt;
    }
    public Optional<Chef> findByUsername(String username) { return repository.findByUsername(username); }
    public List<Chef> findAll() { return repository.findAll(); }
    public Chef findByServicioId(int idServicio) { return repository.findByServicioId(idServicio); }
    public Optional<Chef> findById(int id) { return repository.findById(id); }
    public Optional<Chef> findByIdForUpdate(int id) { return repository.findByIdForUpdate(id); }
    public Chef getChefLogueado() { return jwt.userLogin(); }
    public List<Chef> getListChefActivos() { return repository.getListChefActivos(); }
    @Transactional
    public Chef save(Chef chef) {
        chef.setId(0); chef.setVersion(0);
        chef.setPassword(encoder.encode(chef.getPassword()));
        chef.setRol(Rol.CHEF); chef.setBaneado(false);
        chef.setActivo(true); chef.setValoracionChef(0.0);
        chef.setServiciosOfrecidos(new ArrayList<>());
        return repository.save(chef);
    }
    @Transactional
    public Chef saveBasico(Chef chef) { return repository.save(chef); }
    @Transactional
    public Chef update(Chef data) {
        Chef chef = getChefLogueado();
        if (chef == null) return null;
        if (data.getPassword() != null && !data.getPassword().isBlank())
            chef.setPassword(encoder.encode(data.getPassword()));
        chef.setNombre(data.getNombre());
        chef.setPrimerApellido(data.getPrimerApellido());
        chef.setSegundoApellido(data.getSegundoApellido());
        chef.setEmail(data.getEmail());
        chef.setTelefono(data.getTelefono());
        chef.setImagenPerfil(data.getImagenPerfil());
        if (data.getPrecioServicio() != null && data.getPrecioServicio() >= 0)
            chef.setPrecioServicio(data.getPrecioServicio());
        return repository.save(chef);
    }
    @Transactional
    public boolean deleteAndAnonymize() {
        Chef chef = getChefLogueado();
        if (chef == null) return false;
        chef.setUsername("ANON_" + chef.getId());
        chef.setPassword(encoder.encode(UUID.randomUUID().toString()));
        chef.setNombre("ANONIMO");
        chef.setPrimerApellido("ANONIMO");
        chef.setSegundoApellido(null);
        chef.setEmail("anonimo" + chef.getId() + "@example.invalid");
        chef.setTelefono(null); chef.setImagenPerfil(null);
        chef.setRol(null); chef.setActivo(false); chef.setBaneado(true);
        return repository.save(chef) != null;
    }
    @Transactional
    public void activarseParaServicio() {
        Chef chef = getChefLogueado();
        if (chef == null) throw new IllegalStateException("Chef no autenticado");
        chef.setActivo(true);
        repository.save(chef);
    }
    @Transactional
    public void desactivarseParaServicio() {
        Chef chef = getChefLogueado();
        if (chef == null) throw new IllegalStateException("Chef no autenticado");
        chef.setActivo(false);
        repository.save(chef);
    }
}
