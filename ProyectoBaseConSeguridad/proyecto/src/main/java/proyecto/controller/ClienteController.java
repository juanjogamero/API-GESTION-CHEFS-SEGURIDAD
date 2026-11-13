package proyecto.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import proyecto.entity.Cliente;
import proyecto.service.ClienteService;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@GetMapping()
	@Operation(summary = "Obtener todos los clientes registrados en el sistema")
	public ResponseEntity<List<Cliente>> findAll() {
		return ResponseEntity.ok(clienteService.findAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener datos de un cliente concreto")
	public ResponseEntity<Cliente> findById(@PathVariable int id) {
		Optional<Cliente> cliente = clienteService.findById(id);

		if (cliente.isPresent()) {
			return ResponseEntity.ok(cliente. get());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@GetMapping("/miPerfil")
	@Operation(summary = "Obtener los datos del cliente logueado")
	public ResponseEntity<Cliente> getClienteLogueado() {
		Cliente cliente = clienteService.getClienteLogueado();
		return ResponseEntity.ok(cliente);
	}

	@PostMapping
	@Operation(summary = "Crear un nuevo cliente")
	public ResponseEntity<String> saveCliente(@Valid @RequestBody Cliente newCliente) {
		Cliente savedCliente = clienteService.save(newCliente);

		if (savedCliente != null) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Cliente creado exitosamente con ID: " + savedCliente.getId());
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el cliente");
		}
	}

	@PutMapping
	@Operation(summary = "Actualizar un cliente logueado")
	public ResponseEntity<String> updateCliente(@RequestBody Cliente updatedCliente) {
		Cliente response = clienteService.update(updatedCliente);

		if (response != null) {
			return ResponseEntity.ok("Cliente actualizado exitosamente");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
		}
	}

	@DeleteMapping
	@Operation(summary = "Eliminar un cliente logueado (Anonimización de datos)")
	public ResponseEntity<String> deleteCliente() {
		boolean result = clienteService.deleteAndAnonymize();

		if (result) {
			return ResponseEntity.ok("Cliente eliminado (datos anonimizados) exitosamente");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
		}
	}
}
