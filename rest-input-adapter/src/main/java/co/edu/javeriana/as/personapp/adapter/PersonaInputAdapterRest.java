package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperRest personaMapperRest;

	private String normalizeDatabase(String database) {
		if (database == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "database must be MARIA or MONGO");
		}
		String normalized = database.trim().toUpperCase();
		if ("MARIA".equals(normalized) || "MARIADB".equals(normalized)) {
			return "MARIA";
		}
		if ("MONGO".equals(normalized) || "MONGODB".equals(normalized)) {
			return "MONGO";
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid database option: " + database);
	}

	public List<PersonaResponse> historial(String database) {
		log.info("Into historial PersonaEntity in Input Adapter");
		String selectedDatabase = normalizeDatabase(database);
		return inputPort(selectedDatabase).findAll().stream()
				.map(person -> personaMapperRest.fromDomainToAdapterRest(person, databaseName(selectedDatabase)))
				.collect(Collectors.toList());
	}

	public PersonaResponse crearPersona(PersonaRequest request) {
		if (request == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
		}
		String selectedDatabase = normalizeDatabase(request.getDatabase());
		Person person = inputPort(selectedDatabase).create(personaMapperRest.fromAdapterToDomain(request));
		return personaMapperRest.fromDomainToAdapterRest(person, databaseName(selectedDatabase));
	}

	public PersonaResponse buscarPersona(String database, Integer identification) {
		String selectedDatabase = normalizeDatabase(database);
		try {
			Person person = inputPort(selectedDatabase).findOne(identification);
			return personaMapperRest.fromDomainToAdapterRest(person, databaseName(selectedDatabase));
		} catch (NoExistException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
		}
	}

	public PersonaResponse editarPersona(String database, Integer identification, PersonaRequest request) {
		String selectedDatabase = normalizeDatabase(database);
		try {
			Person person = inputPort(selectedDatabase).edit(identification,
					personaMapperRest.fromAdapterToDomain(request, identification));
			return personaMapperRest.fromDomainToAdapterRest(person, databaseName(selectedDatabase));
		} catch (NoExistException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
		}
	}

	public void eliminarPersona(String database, Integer identification) {
		String selectedDatabase = normalizeDatabase(database);
		try {
			inputPort(selectedDatabase).drop(identification);
		} catch (NoExistException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
		}
	}

	public Integer contarPersonas(String database) {
		String selectedDatabase = normalizeDatabase(database);
		return inputPort(selectedDatabase).count();
	}

	private PersonInputPort inputPort(String database) {
		PersonOutputPort outputPort = "MARIA".equals(database) ? personOutputPortMaria : personOutputPortMongo;
		return new PersonUseCase(outputPort);
	}

	private String databaseName(String database) {
		return "MARIA".equals(database) ? "MariaDB" : "MongoDB";
	}

}
