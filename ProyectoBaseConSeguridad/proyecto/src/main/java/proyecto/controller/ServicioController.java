package proyecto.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.entity.Servicio;
import proyecto.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/servicio")
@Tag(name = "Servicios", description = "Contrataciones y valoraciones protegidas por rol")
public class ServicioController {
    private final ServicioService servicioService;
    public ServicioController(ServicioService servicioService) { this.servicioService=servicioService; }

    @PostMapping("/contratar/{chefId}")
    @Operation(summary = "Contratar un chef activo para una fecha futura")
    public ResponseEntity<String> contratarServicio(@PathVariable int chefId, @RequestBody Servicio servicio) {
        Servicio guardado = servicioService.contratarServicio(servicio, chefId);
        return guardado == null
            ? ResponseEntity.status(HttpStatus.CONFLICT).body("Chef no disponible o fecha no válida")
            : ResponseEntity.status(HttpStatus.CREATED).body("Servicio creado con ID: " + guardado.getId());
    }

    @PutMapping("/puntuar/{servicioId}/{puntuacion}")
    @Operation(summary = "Valorar con 1 a 5 estrellas un servicio propio que ya se realizó")
    public ResponseEntity<String> puntuarServicio(@PathVariable int servicioId, @PathVariable int puntuacion) {
        if (puntuacion < 1 || puntuacion > 5)
            return ResponseEntity.badRequest().body("La puntuación debe estar entre 1 y 5");
        Servicio servicio = servicioService.findById(servicioId).orElse(null);
        if (servicio == null) return ResponseEntity.notFound().build();
        return servicioService.puntuarServicio(servicio,puntuacion)
            ? ResponseEntity.ok("Servicio valorado correctamente")
            : ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Solo puedes valorar una vez un servicio propio ya realizado");
    }

    @GetMapping("/misServiciosChef")
    public List<Servicio> getAllByChefLogin() { return servicioService.getAllByChefLogin(); }
    @GetMapping("/misServiciosCliente")
    public List<Servicio> getAllByClienteLogin() { return servicioService.getAllByClienteLogin(); }
}
