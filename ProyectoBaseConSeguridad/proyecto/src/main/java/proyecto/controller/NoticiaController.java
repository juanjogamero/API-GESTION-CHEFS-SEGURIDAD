package proyecto.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import proyecto.entity.Noticia;
import proyecto.service.NoticiaService;

@RestController
@RequestMapping("/noticia")
public class NoticiaController {

    @Autowired
    private NoticiaService noticiaService;

    @GetMapping()
    @Operation(summary = "Obtener todas las noticias")
    public ResponseEntity<List<Noticia>> findAll() {
        return ResponseEntity.ok(noticiaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una noticia por su ID")
    public ResponseEntity<Noticia> findById(@PathVariable int id) {
        Optional<Noticia> noticia = noticiaService.findById(id);

        if (noticia.isPresent()) {
            return ResponseEntity.ok(noticia.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear una nueva noticia")
    public ResponseEntity<String> saveNoticia(@Valid @RequestBody Noticia newNoticia) {
        Noticia savedNoticia = noticiaService.save(newNoticia);

        if (savedNoticia != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Noticia creada exitosamente con encabezado: " + savedNoticia.getEncabezado());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la noticia");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una noticia existente")
    public ResponseEntity<String> updateNoticia(@PathVariable int id, @Valid @RequestBody Noticia updatedNoticia) {
        Noticia response = noticiaService.update(id, updatedNoticia);

        if (response != null) {
            return ResponseEntity.ok("Noticia actualizada exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Noticia no encontrada");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una noticia por su ID")
    public ResponseEntity<String> deleteNoticia(@PathVariable int id) {
        boolean result = noticiaService.deleteById(id);

        if (result) {
            return ResponseEntity.ok("Noticia eliminada exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Noticia no encontrada");
        }
    }
}
