package co.edu.javeriana.as.personapp.controller;

import java.util.Collections;
import java.util.List;
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

import co.edu.javeriana.as.personapp.application.port.in.RelationalTablesInputPort;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.ResourceConflictException;
import co.edu.javeriana.as.personapp.common.exceptions.RelationshipConflictException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.RelationalTablesRestMapper;
import co.edu.javeriana.as.personapp.model.request.EstudiosTablaRequest;
import co.edu.javeriana.as.personapp.model.request.PersonaTablaRequest;
import co.edu.javeriana.as.personapp.model.request.ProfesionTablaRequest;
import co.edu.javeriana.as.personapp.model.request.TelefonoTablaRequest;

@RestController
@RequestMapping("/api/v1/tablas")
public class TablasRelacionalesController {

	private final RelationalTablesInputPort tables;
	private final RelationalTablesRestMapper mapper;

	public TablasRelacionalesController(RelationalTablesInputPort tables, RelationalTablesRestMapper mapper) {
		this.tables = tables;
		this.mapper = mapper;
	}

	@PostMapping("/persona")
	public ResponseEntity<PersonaTablaRequest> crearPersona(@RequestBody PersonaTablaRequest request)
			throws ResourceConflictException {
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRequest(tables.createPerson(mapper.toDomain(request))));
	}

	@GetMapping("/persona")
	public List<PersonaTablaRequest> listarPersonas() {
		return mapper.toPersonRequests(tables.findAllPeople());
	}

	@GetMapping("/persona/count")
	public long contarPersonas() {
		return tables.countPeople();
	}

	@GetMapping("/persona/{cc}")
	public PersonaTablaRequest buscarPersona(@PathVariable Integer cc) throws NoExistException {
		return mapper.toRequest(tables.findPerson(cc));
	}

	@PutMapping("/persona/{cc}")
	public PersonaTablaRequest actualizarPersona(@PathVariable Integer cc,
			@RequestBody PersonaTablaRequest request) throws NoExistException {
		return mapper.toRequest(tables.updatePerson(cc, mapper.toDomain(request)));
	}

	@DeleteMapping("/persona/{cc}")
	public ResponseEntity<Void> eliminarPersona(@PathVariable Integer cc)
			throws NoExistException, RelationshipConflictException {
		tables.deletePerson(cc);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/profesion")
	public ResponseEntity<ProfesionTablaRequest> crearProfesion(@RequestBody ProfesionTablaRequest request)
			throws ResourceConflictException {
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRequest(tables.createProfession(mapper.toDomain(request))));
	}

	@GetMapping("/profesion")
	public List<ProfesionTablaRequest> listarProfesiones() {
		return mapper.toProfessionRequests(tables.findAllProfessions());
	}

	@GetMapping("/profesion/count")
	public long contarProfesiones() {
		return tables.countProfessions();
	}

	@GetMapping("/profesion/{id}")
	public ProfesionTablaRequest buscarProfesion(@PathVariable Integer id) throws NoExistException {
		return mapper.toRequest(tables.findProfession(id));
	}

	@PutMapping("/profesion/{id}")
	public ProfesionTablaRequest actualizarProfesion(@PathVariable Integer id,
			@RequestBody ProfesionTablaRequest request) throws NoExistException {
		return mapper.toRequest(tables.updateProfession(id, mapper.toDomain(request)));
	}

	@DeleteMapping("/profesion/{id}")
	public ResponseEntity<Void> eliminarProfesion(@PathVariable Integer id)
			throws NoExistException, RelationshipConflictException {
		tables.deleteProfession(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/estudios")
	public ResponseEntity<EstudiosTablaRequest> crearEstudio(@RequestBody EstudiosTablaRequest request)
			throws NoExistException, ResourceConflictException {
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRequest(tables.createStudy(mapper.toDomain(request))));
	}

	@GetMapping("/estudios")
	public List<EstudiosTablaRequest> listarEstudios() {
		return mapper.toStudyRequests(tables.findAllStudies());
	}

	@GetMapping("/estudios/count")
	public long contarEstudios() {
		return tables.countStudies();
	}

	@GetMapping("/estudios/{id_prof}/{cc_per}")
	public EstudiosTablaRequest buscarEstudio(@PathVariable Integer id_prof, @PathVariable Integer cc_per)
			throws NoExistException {
		return mapper.toRequest(tables.findStudy(id_prof, cc_per));
	}

	@PutMapping("/estudios/{id_prof}/{cc_per}")
	public EstudiosTablaRequest actualizarEstudio(@PathVariable Integer id_prof, @PathVariable Integer cc_per,
			@RequestBody EstudiosTablaRequest request) throws NoExistException {
		return mapper.toRequest(tables.updateStudy(id_prof, cc_per, mapper.toDomain(request)));
	}

	@DeleteMapping("/estudios/{id_prof}/{cc_per}")
	public ResponseEntity<Void> eliminarEstudio(@PathVariable Integer id_prof, @PathVariable Integer cc_per)
			throws NoExistException {
		tables.deleteStudy(id_prof, cc_per);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/telefono")
	public ResponseEntity<TelefonoTablaRequest> crearTelefono(@RequestBody TelefonoTablaRequest request)
			throws NoExistException, ResourceConflictException {
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRequest(tables.createPhone(mapper.toDomain(request))));
	}

	@GetMapping("/telefono")
	public List<TelefonoTablaRequest> listarTelefonos() {
		return mapper.toPhoneRequests(tables.findAllPhones());
	}

	@GetMapping("/telefono/count")
	public long contarTelefonos() {
		return tables.countPhones();
	}

	@GetMapping("/telefono/{num}")
	public TelefonoTablaRequest buscarTelefono(@PathVariable String num) throws NoExistException {
		return mapper.toRequest(tables.findPhone(num));
	}

	@PutMapping("/telefono/{num}")
	public TelefonoTablaRequest actualizarTelefono(@PathVariable String num,
			@RequestBody TelefonoTablaRequest request) throws NoExistException {
		return mapper.toRequest(tables.updatePhone(num, mapper.toDomain(request)));
	}

	@DeleteMapping("/telefono/{num}")
	public ResponseEntity<Void> eliminarTelefono(@PathVariable String num) throws NoExistException {
		tables.deletePhone(num);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(NoExistException.class)
	public ResponseEntity<Map<String, String>> manejarNoEncontrado(NoExistException exception) {
		return error(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler({ RelationshipConflictException.class, ResourceConflictException.class,
			DataIntegrityViolationException.class })
	public ResponseEntity<Map<String, String>> manejarConflicto(Exception exception) {
		return error(HttpStatus.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> manejarSolicitudInvalida(IllegalArgumentException exception) {
		return error(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(Collections.singletonMap("error", message));
	}
}
