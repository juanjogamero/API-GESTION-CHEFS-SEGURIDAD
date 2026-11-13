package proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import proyecto.entity.Admin;
import proyecto.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@GetMapping("/miPerfil")
	@Operation(summary = "Obtener datos del admin logueado")
	public ResponseEntity<Admin> getChefLogueado() {
		Admin admin = adminService.getAdminLogueado();
		return ResponseEntity.ok(admin);
	}

	@PostMapping
	@Operation(summary = "Crear un nuevo administrador")
	public ResponseEntity<String> saveAdmin(@Valid @RequestBody Admin newAdmin) {
		Admin savedAdmin = adminService.save(newAdmin);

		if (savedAdmin != null) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Administrador creado exitosamente con ID: " + savedAdmin.getId());
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el administrador");
		}
	}

	@PutMapping
	@Operation(summary = "Actualizar un administrador logueado")
	public ResponseEntity<String> updateAdmin(@RequestBody Admin updatedAdmin) {
		Admin response = adminService.update(updatedAdmin);

		if (response != null) {
			return ResponseEntity.ok("Administrador actualizado exitosamente");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrador no encontrado");
		}
	}

	@DeleteMapping
	@Operation(summary = "Eliminar un administrador logueado (Anonimización de datos)")
	public ResponseEntity<String> deleteAdmin() {
		boolean result = adminService.deleteAndAnonymize();

		if (result) {
			return ResponseEntity.ok("Administrador eliminado (datos anonimizados) exitosamente");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrador no encontrado");
		}
	}
}
