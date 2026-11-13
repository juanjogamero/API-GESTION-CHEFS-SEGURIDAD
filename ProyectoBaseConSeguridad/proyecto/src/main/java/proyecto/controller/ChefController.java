package proyecto.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import proyecto.dto.ChefPublicResponse;
import proyecto.entity.Chef;
import proyecto.service.ChefService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/chef")
@Tag(name = "Chefs", description = "Registro, perfiles y disponibilidad")
public class ChefController {
    private final ChefService chefs;
    public ChefController(ChefService chefs) { this.chefs=chefs; }

    @GetMapping("/{id}")
    public ChefPublicResponse findById(@PathVariable int id) {
        Chef chef = chefs.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chef no encontrado"));
        if (!chef.isActivo() || chef.isBaneado())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chef no disponible");
        return ChefPublicResponse.from(chef);
    }

    @GetMapping
    public List<ChefPublicResponse> findAll() { return chefs.getListChefActivos().stream().map(ChefPublicResponse::from).toList(); }
    @GetMapping("/miPerfil")
    public Chef getChefLogueado() { return chefs.getChefLogueado(); }
    @GetMapping("/listadoActivos")
    public List<ChefPublicResponse> getListChefActivos() { return findAll(); }
    @PostMapping
    public ResponseEntity<String> saveChef(@Valid @RequestBody Chef chef) {
        Chef saved = chefs.save(chef);
        return ResponseEntity.status(HttpStatus.CREATED).body("Chef creado con ID: " + saved.getId());
    }
    @PutMapping
    public ResponseEntity<String> updateChef(@RequestBody Chef updated) {
        return chefs.update(updated) == null
            ? ResponseEntity.notFound().build()
            : ResponseEntity.ok("Perfil actualizado");
    }
    @DeleteMapping
    public ResponseEntity<String> deleteChef() {
        return chefs.deleteAndAnonymize()
            ? ResponseEntity.ok("Perfil desactivado y datos personales anonimizados")
            : ResponseEntity.notFound().build();
    }
    @PutMapping("/activar")
    public ResponseEntity<String> activarChef() {
        chefs.activarseParaServicio();
        return ResponseEntity.ok("Chef activado");
    }
    @PutMapping("/desactivar")
    public ResponseEntity<String> desactivarChef() {
        chefs.desactivarseParaServicio();
        return ResponseEntity.ok("Chef desactivado");
    }
}
