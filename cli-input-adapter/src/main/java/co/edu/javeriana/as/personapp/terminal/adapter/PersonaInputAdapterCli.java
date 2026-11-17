package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.ResourceConflictException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterCli {

	@Autowired
	@Qualifier("personInputPortMaria")
	private PersonInputPort personInputPortMaria;

	@Autowired
	@Qualifier("personInputPortMongo")
	private PersonInputPort personInputPortMongo;

	@Autowired
	private PersonaMapperCli personaMapperCli;

	PersonInputPort personInputPort;

	public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = personInputPortMaria;
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = personInputPortMongo;
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public PersonaModelCli crear(PersonaModelCli model) throws ResourceConflictException {
		log.info("Into crear PersonaEntity in Input Adapter");
		try {
			personInputPort.findOne(model.getCc());
			throw new ResourceConflictException("Ya existe una persona con cc " + model.getCc());
		} catch (NoExistException e) {
			// no existe: se puede crear
		}
		return personaMapperCli.fromDomainToAdapterCli(personInputPort.create(personaMapperCli.fromAdapterCliToDomain(model)));
	}

	public List<PersonaModelCli> listar() {
		log.info("Into listar PersonaEntity in Input Adapter");
		return personInputPort.findAll().stream().map(personaMapperCli::fromDomainToAdapterCli)
				.collect(Collectors.toList());
	}

	public PersonaModelCli buscar(Integer cc) throws NoExistException {
		return personaMapperCli.fromDomainToAdapterCli(personInputPort.findOne(cc));
	}

	public PersonaModelCli actualizar(Integer cc, PersonaModelCli model) throws NoExistException {
		model.setCc(cc);
		return personaMapperCli
				.fromDomainToAdapterCli(personInputPort.edit(cc, personaMapperCli.fromAdapterCliToDomain(model)));
	}

	public boolean eliminar(Integer cc) throws NoExistException {
		return Boolean.TRUE.equals(personInputPort.drop(cc));
	}

	public int contar() {
		return personInputPort.count();
	}
}
