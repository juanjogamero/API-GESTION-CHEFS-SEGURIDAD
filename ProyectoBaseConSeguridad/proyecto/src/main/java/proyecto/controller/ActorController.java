package proyecto.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import proyecto.entity.Actor;
import proyecto.entity.ActorLogin;
import proyecto.entity.Chef;
import proyecto.entity.Rol;
import proyecto.security.JWTUtils;
import proyecto.service.ActorService;
import proyecto.service.ChefService;

@RestController
public class ActorController {
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwt;
    private final ActorService actors;
    private final ChefService chefs;

    public ActorController(AuthenticationManager authenticationManager, JWTUtils jwt,
                           ActorService actors, ChefService chefs) {
        this.authenticationManager = authenticationManager; this.jwt=jwt;
        this.actors=actors; this.chefs=chefs;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@Valid @RequestBody ActorLogin login) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
        return ResponseEntity.ok(Map.of("token", jwt.generateToken(auth)));
    }

    @PutMapping("/banear/{actorId}")
    public ResponseEntity<String> banear(@PathVariable int actorId) {
        Actor actor = actors.findById(actorId).orElse(null);
        if (actor == null || actor.getRol() == Rol.ADMIN)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada o no modificable");
        actor.setBaneado(true);
        if (actor instanceof Chef chef) chef.setActivo(false);
        actors.saveBasico(actor);
        return ResponseEntity.ok("Cuenta bloqueada");
    }

    @PutMapping("/desbanear/{actorId}")
    public ResponseEntity<String> desbanear(@PathVariable int actorId) {
        Actor actor = actors.findById(actorId).orElse(null);
        if (actor == null || actor.getRol() == Rol.ADMIN)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada o no modificable");
        actor.setBaneado(false);
        actors.saveBasico(actor);
        // Un chef desbaneado no se activa automáticamente: debe activarse expresamente.
        return ResponseEntity.ok("Cuenta desbloqueada");
    }
}
