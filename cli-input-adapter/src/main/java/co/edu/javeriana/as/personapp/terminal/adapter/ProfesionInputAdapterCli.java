package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.in.RelationalTablesInputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.RelationshipConflictException;
import co.edu.javeriana.as.personapp.common.exceptions.ResourceConflictException;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

@Adapter
public class ProfesionInputAdapterCli {

	@Autowired
	private RelationalTablesInputPort tables;

	@Autowired
	private ProfesionMapperCli mapper;

	public ProfesionModelCli crear(ProfesionModelCli model) throws ResourceConflictException {
		return mapper.fromDomainToAdapterCli(tables.createProfession(mapper.fromAdapterCliToDomain(model)));
	}

	public List<ProfesionModelCli> listar() {
		return tables.findAllProfessions().stream().map(mapper::fromDomainToAdapterCli).collect(Collectors.toList());
	}

	public ProfesionModelCli buscar(Integer id) throws NoExistException {
		return mapper.fromDomainToAdapterCli(tables.findProfession(id));
	}

	public ProfesionModelCli actualizar(Integer id, ProfesionModelCli model) throws NoExistException {
		model.setId(id);
		return mapper.fromDomainToAdapterCli(tables.updateProfession(id, mapper.fromAdapterCliToDomain(model)));
	}

	public void eliminar(Integer id) throws NoExistException, RelationshipConflictException {
		tables.deleteProfession(id);
	}

	public long contar() {
		return tables.countProfessions();
	}
}
