package co.edu.javeriana.as.personapp.adapter;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import co.edu.javeriana.as.personapp.mariadb.repository.EstudiosRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.ProfesionRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.TelefonoRepositoryMaria;
import co.edu.javeriana.as.personapp.model.request.EstudiosTablaRequest;
import co.edu.javeriana.as.personapp.model.request.PersonaTablaRequest;
import co.edu.javeriana.as.personapp.model.request.ProfesionTablaRequest;
import co.edu.javeriana.as.personapp.model.request.TelefonoTablaRequest;

@Service
@Transactional
public class TablasRelacionalesMaria {

	private final PersonaRepositoryMaria personas;
	private final ProfesionRepositoryMaria profesiones;
	private final EstudiosRepositoryMaria estudios;
	private final TelefonoRepositoryMaria telefonos;

	public TablasRelacionalesMaria(PersonaRepositoryMaria personas, ProfesionRepositoryMaria profesiones,
			EstudiosRepositoryMaria estudios, TelefonoRepositoryMaria telefonos) {
		this.personas = personas;
		this.profesiones = profesiones;
		this.estudios = estudios;
		this.telefonos = telefonos;
	}

	public PersonaTablaRequest crearPersona(PersonaTablaRequest request) {
		validatePersona(request);
		if (personas.existsById(request.getCc())) {
			throw conflict("persona already exists: " + request.getCc());
		}
		PersonaEntity entity = new PersonaEntity();
		entity.setCc(request.getCc());
		return map(personas.save(update(entity, request)));
	}

	public List<PersonaTablaRequest> listarPersonas() {
		return personas.findAll().stream().map(this::map).collect(Collectors.toList());
	}

	public PersonaTablaRequest buscarPersona(Integer cc) {
		return map(personas.findById(cc).orElseThrow(() -> notFound("persona", cc)));
	}

	public PersonaTablaRequest actualizarPersona(Integer cc, PersonaTablaRequest request) {
		validatePersona(request);
		if (!cc.equals(request.getCc())) {
			throw badRequest("cc in the request must match the path");
		}
		PersonaEntity entity = personas.findById(cc).orElseThrow(() -> notFound("persona", cc));
		return map(personas.save(update(entity, request)));
	}

	public void eliminarPersona(Integer cc) {
		PersonaEntity entity = personas.findById(cc).orElseThrow(() -> notFound("persona", cc));
		if (telefonos.countByDuenioCc(cc) > 0 || estudios.countByCcPer(cc) > 0) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"persona is referenced by telefono or estudios");
		}
		personas.delete(entity);
	}

	public long contarPersonas() {
		return personas.count();
	}

	public ProfesionTablaRequest crearProfesion(ProfesionTablaRequest request) {
		validateProfesion(request);
		if (profesiones.existsById(request.getId())) {
			throw conflict("profesion already exists: " + request.getId());
		}
		ProfesionEntity entity = new ProfesionEntity();
		entity.setId(request.getId());
		return map(profesiones.save(update(entity, request)));
	}

	public List<ProfesionTablaRequest> listarProfesiones() {
		return profesiones.findAll().stream().map(this::map).collect(Collectors.toList());
	}

	public ProfesionTablaRequest buscarProfesion(Integer id) {
		return map(profesiones.findById(id).orElseThrow(() -> notFound("profesion", id)));
	}

	public ProfesionTablaRequest actualizarProfesion(Integer id, ProfesionTablaRequest request) {
		validateProfesion(request);
		if (!id.equals(request.getId())) {
			throw badRequest("id in the request must match the path");
		}
		ProfesionEntity entity = profesiones.findById(id).orElseThrow(() -> notFound("profesion", id));
		return map(profesiones.save(update(entity, request)));
	}

	public void eliminarProfesion(Integer id) {
		ProfesionEntity entity = profesiones.findById(id).orElseThrow(() -> notFound("profesion", id));
		if (estudios.countByIdProf(id) > 0) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "profesion is referenced by estudios");
		}
		profesiones.delete(entity);
	}

	public long contarProfesiones() {
		return profesiones.count();
	}

	public EstudiosTablaRequest crearEstudio(EstudiosTablaRequest request) {
		validateEstudio(request);
		EstudiosEntityPK primaryKey = key(request.getId_prof(), request.getCc_per());
		if (estudios.existsById(primaryKey)) {
			throw conflict("estudios already exists: " + request.getId_prof() + "/" + request.getCc_per());
		}
		EstudiosEntity entity = new EstudiosEntity();
		entity.setEstudiosPK(key(request.getId_prof(), request.getCc_per()));
		setStudyRelations(entity, request);
		return map(estudios.save(update(entity, request)));
	}

	public List<EstudiosTablaRequest> listarEstudios() {
		return estudios.findAll().stream().map(this::map).collect(Collectors.toList());
	}

	public EstudiosTablaRequest buscarEstudio(Integer id_prof, Integer cc_per) {
		return map(estudios.findById(key(id_prof, cc_per))
				.orElseThrow(() -> notFound("estudios", id_prof + "/" + cc_per)));
	}

	public EstudiosTablaRequest actualizarEstudio(Integer id_prof, Integer cc_per, EstudiosTablaRequest request) {
		validateEstudio(request);
		if (!id_prof.equals(request.getId_prof()) || !cc_per.equals(request.getCc_per())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"id_prof and cc_per in the request must match the path");
		}
		EstudiosEntity entity = estudios.findById(key(id_prof, cc_per))
				.orElseThrow(() -> notFound("estudios", id_prof + "/" + cc_per));
		setStudyRelations(entity, request);
		return map(estudios.save(update(entity, request)));
	}

	public void eliminarEstudio(Integer id_prof, Integer cc_per) {
		EstudiosEntity entity = estudios.findById(key(id_prof, cc_per))
				.orElseThrow(() -> notFound("estudios", id_prof + "/" + cc_per));
		estudios.delete(entity);
	}

	public long contarEstudios() {
		return estudios.count();
	}

	public TelefonoTablaRequest crearTelefono(TelefonoTablaRequest request) {
		validateTelefono(request);
		if (telefonos.existsById(request.getNum())) {
			throw conflict("telefono already exists: " + request.getNum());
		}
		TelefonoEntity entity = new TelefonoEntity();
		entity.setNum(request.getNum());
		return map(telefonos.save(update(entity, request)));
	}

	public List<TelefonoTablaRequest> listarTelefonos() {
		return telefonos.findAll().stream().map(this::map).collect(Collectors.toList());
	}

	public TelefonoTablaRequest buscarTelefono(String num) {
		return map(telefonos.findById(num).orElseThrow(() -> notFound("telefono", num)));
	}

	public TelefonoTablaRequest actualizarTelefono(String num, TelefonoTablaRequest request) {
		validateTelefono(request);
		if (!num.equals(request.getNum())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "num in the request must match the path");
		}
		TelefonoEntity entity = telefonos.findById(num).orElseThrow(() -> notFound("telefono", num));
		return map(telefonos.save(update(entity, request)));
	}

	public void eliminarTelefono(String num) {
		TelefonoEntity entity = telefonos.findById(num).orElseThrow(() -> notFound("telefono", num));
		telefonos.delete(entity);
	}

	public long contarTelefonos() {
		return telefonos.count();
	}

	private PersonaEntity update(PersonaEntity entity, PersonaTablaRequest request) {
		entity.setNombre(request.getNombre());
		entity.setApellido(request.getApellido());
		entity.setGenero(request.getGenero().charAt(0));
		entity.setEdad(request.getEdad());
		return entity;
	}

	private ProfesionEntity update(ProfesionEntity entity, ProfesionTablaRequest request) {
		entity.setNom(request.getNom());
		entity.setDes(request.getDes());
		return entity;
	}

	private EstudiosEntity update(EstudiosEntity entity, EstudiosTablaRequest request) {
		entity.setFecha(request.getFecha() == null ? null : Date.valueOf(request.getFecha()));
		entity.setUniver(request.getUniver());
		return entity;
	}

	private TelefonoEntity update(TelefonoEntity entity, TelefonoTablaRequest request) {
		entity.setOper(request.getOper());
		entity.setDuenio(personas.findById(request.getDuenio())
				.orElseThrow(() -> notFound("persona", request.getDuenio())));
		return entity;
	}

	private void setStudyRelations(EstudiosEntity entity, EstudiosTablaRequest request) {
		entity.setPersona(personas.findById(request.getCc_per())
				.orElseThrow(() -> notFound("persona", request.getCc_per())));
		entity.setProfesion(profesiones.findById(request.getId_prof())
				.orElseThrow(() -> notFound("profesion", request.getId_prof())));
	}

	private EstudiosEntityPK key(Integer id_prof, Integer cc_per) {
		return new EstudiosEntityPK(id_prof, cc_per);
	}

	private PersonaTablaRequest map(PersonaEntity entity) {
		PersonaTablaRequest request = new PersonaTablaRequest();
		request.setCc(entity.getCc());
		request.setNombre(entity.getNombre());
		request.setApellido(entity.getApellido());
		request.setGenero(entity.getGenero().toString());
		request.setEdad(entity.getEdad());
		return request;
	}

	private ProfesionTablaRequest map(ProfesionEntity entity) {
		ProfesionTablaRequest request = new ProfesionTablaRequest();
		request.setId(entity.getId());
		request.setNom(entity.getNom());
		request.setDes(entity.getDes());
		return request;
	}

	private EstudiosTablaRequest map(EstudiosEntity entity) {
		EstudiosTablaRequest request = new EstudiosTablaRequest();
		request.setId_prof(entity.getEstudiosPK().getIdProf());
		request.setCc_per(entity.getEstudiosPK().getCcPer());
		request.setFecha(entity.getFecha() == null ? null : new Date(entity.getFecha().getTime()).toLocalDate());
		request.setUniver(entity.getUniver());
		return request;
	}

	private TelefonoTablaRequest map(TelefonoEntity entity) {
		TelefonoTablaRequest request = new TelefonoTablaRequest();
		request.setNum(entity.getNum());
		request.setOper(entity.getOper());
		request.setDuenio(entity.getDuenio().getCc());
		return request;
	}

	private void validatePersona(PersonaTablaRequest request) {
		if (request == null || request.getCc() == null || isBlank(request.getNombre())
				|| isBlank(request.getApellido()) || isBlank(request.getGenero())) {
			throw badRequest("cc, nombre, apellido and genero are required");
		}
		if (!"M".equals(request.getGenero()) && !"F".equals(request.getGenero())) {
			throw badRequest("genero must be M or F");
		}
	}

	private void validateProfesion(ProfesionTablaRequest request) {
		if (request == null || request.getId() == null || isBlank(request.getNom())) {
			throw badRequest("id and nom are required");
		}
	}

	private void validateEstudio(EstudiosTablaRequest request) {
		if (request == null || request.getId_prof() == null || request.getCc_per() == null) {
			throw badRequest("id_prof and cc_per are required");
		}
	}

	private void validateTelefono(TelefonoTablaRequest request) {
		if (request == null || isBlank(request.getNum()) || isBlank(request.getOper()) || request.getDuenio() == null) {
			throw badRequest("num, oper and duenio are required");
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private ResponseStatusException badRequest(String message) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
	}

	private ResponseStatusException conflict(String message) {
		return new ResponseStatusException(HttpStatus.CONFLICT, message);
	}

	private ResponseStatusException notFound(String table, Object key) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, table + " not found: " + key);
	}
}
