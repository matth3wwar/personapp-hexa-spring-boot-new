package co.edu.javeriana.as.personapp.controller;

import java.util.List;
import java.util.Collections;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import co.edu.javeriana.as.personapp.adapter.TablasRelacionalesMaria;
import co.edu.javeriana.as.personapp.model.request.EstudiosTablaRequest;
import co.edu.javeriana.as.personapp.model.request.PersonaTablaRequest;
import co.edu.javeriana.as.personapp.model.request.ProfesionTablaRequest;
import co.edu.javeriana.as.personapp.model.request.TelefonoTablaRequest;

@RestController
@RequestMapping("/api/v1/tablas")
public class TablasRelacionalesController {

	private final TablasRelacionalesMaria tablas;

	public TablasRelacionalesController(TablasRelacionalesMaria tablas) {
		this.tablas = tablas;
	}

	@PostMapping("/persona")
	public ResponseEntity<PersonaTablaRequest> crearPersona(@RequestBody PersonaTablaRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tablas.crearPersona(request));
	}

	@GetMapping("/persona")
	public List<PersonaTablaRequest> listarPersonas() {
		return tablas.listarPersonas();
	}

	@GetMapping("/persona/count")
	public long contarPersonas() {
		return tablas.contarPersonas();
	}

	@GetMapping("/persona/{cc}")
	public PersonaTablaRequest buscarPersona(@PathVariable Integer cc) {
		return tablas.buscarPersona(cc);
	}

	@PutMapping("/persona/{cc}")
	public PersonaTablaRequest actualizarPersona(@PathVariable Integer cc,
			@RequestBody PersonaTablaRequest request) {
		return tablas.actualizarPersona(cc, request);
	}

	@DeleteMapping("/persona/{cc}")
	public ResponseEntity<Void> eliminarPersona(@PathVariable Integer cc) {
		tablas.eliminarPersona(cc);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/profesion")
	public ResponseEntity<ProfesionTablaRequest> crearProfesion(@RequestBody ProfesionTablaRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tablas.crearProfesion(request));
	}

	@GetMapping("/profesion")
	public List<ProfesionTablaRequest> listarProfesiones() {
		return tablas.listarProfesiones();
	}

	@GetMapping("/profesion/count")
	public long contarProfesiones() {
		return tablas.contarProfesiones();
	}

	@GetMapping("/profesion/{id}")
	public ProfesionTablaRequest buscarProfesion(@PathVariable Integer id) {
		return tablas.buscarProfesion(id);
	}

	@PutMapping("/profesion/{id}")
	public ProfesionTablaRequest actualizarProfesion(@PathVariable Integer id,
			@RequestBody ProfesionTablaRequest request) {
		return tablas.actualizarProfesion(id, request);
	}

	@DeleteMapping("/profesion/{id}")
	public ResponseEntity<Void> eliminarProfesion(@PathVariable Integer id) {
		tablas.eliminarProfesion(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/estudios")
	public ResponseEntity<EstudiosTablaRequest> crearEstudio(@RequestBody EstudiosTablaRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tablas.crearEstudio(request));
	}

	@GetMapping("/estudios")
	public List<EstudiosTablaRequest> listarEstudios() {
		return tablas.listarEstudios();
	}

	@GetMapping("/estudios/count")
	public long contarEstudios() {
		return tablas.contarEstudios();
	}

	@GetMapping("/estudios/{id_prof}/{cc_per}")
	public EstudiosTablaRequest buscarEstudio(@PathVariable Integer id_prof, @PathVariable Integer cc_per) {
		return tablas.buscarEstudio(id_prof, cc_per);
	}

	@PutMapping("/estudios/{id_prof}/{cc_per}")
	public EstudiosTablaRequest actualizarEstudio(@PathVariable Integer id_prof, @PathVariable Integer cc_per,
			@RequestBody EstudiosTablaRequest request) {
		return tablas.actualizarEstudio(id_prof, cc_per, request);
	}

	@DeleteMapping("/estudios/{id_prof}/{cc_per}")
	public ResponseEntity<Void> eliminarEstudio(@PathVariable Integer id_prof, @PathVariable Integer cc_per) {
		tablas.eliminarEstudio(id_prof, cc_per);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/telefono")
	public ResponseEntity<TelefonoTablaRequest> crearTelefono(@RequestBody TelefonoTablaRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tablas.crearTelefono(request));
	}

	@GetMapping("/telefono")
	public List<TelefonoTablaRequest> listarTelefonos() {
		return tablas.listarTelefonos();
	}

	@GetMapping("/telefono/count")
	public long contarTelefonos() {
		return tablas.contarTelefonos();
	}

	@GetMapping("/telefono/{num}")
	public TelefonoTablaRequest buscarTelefono(@PathVariable String num) {
		return tablas.buscarTelefono(num);
	}

	@PutMapping("/telefono/{num}")
	public TelefonoTablaRequest actualizarTelefono(@PathVariable String num,
			@RequestBody TelefonoTablaRequest request) {
		return tablas.actualizarTelefono(num, request);
	}

	@DeleteMapping("/telefono/{num}")
	public ResponseEntity<Void> eliminarTelefono(@PathVariable String num) {
		tablas.eliminarTelefono(num);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> manejarIntegridadReferencial(
			DataIntegrityViolationException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(Collections.singletonMap("error", "The operation violates a table relationship"));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, String>> manejarErrorDeSolicitud(ResponseStatusException exception) {
		return ResponseEntity.status(exception.getStatus())
				.body(Collections.singletonMap("error", exception.getReason()));
	}
}
