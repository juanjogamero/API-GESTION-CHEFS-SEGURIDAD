package proyecto.service;

import java.util.Optional;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import proyecto.entity.Actor;
import proyecto.respository.ActorRepository;

@Service
public class ActorService implements UserDetailsService {
    private final ActorRepository actorRepository;
    public ActorService(ActorRepository actorRepository) { this.actorRepository = actorRepository; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Actor actor = findByUsername(username).orElseThrow(() ->
            new UsernameNotFoundException("Cuenta no encontrada"));
        if (actor.getRol() == null || actor.getPassword() == null) {
            throw new UsernameNotFoundException("Cuenta inactiva");
        }
        return new User(actor.getUsername(), actor.getPassword(), !actor.isBaneado(),
            true, true, true,
            Set.of(new SimpleGrantedAuthority(actor.getRol().name())));
    }

    public Optional<Actor> findByUsername(String username) { return actorRepository.findByUsername(username); }
    public Optional<Actor> findById(int id) { return actorRepository.findById(id); }
    public Actor saveBasico(Actor actor) { return actorRepository.save(actor); }
}
