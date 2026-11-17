package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.in.RelationalTablesInputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.ResourceConflictException;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

@Adapter
public class EstudioInputAdapterCli {

	@Autowired
	private RelationalTablesInputPort tables;

	@Autowired
	private EstudioMapperCli mapper;

	public EstudioModelCli crear(EstudioModelCli model) throws NoExistException, ResourceConflictException {
		return mapper.fromDomainToAdapterCli(tables.createStudy(mapper.fromAdapterCliToDomain(model)));
	}

	public List<EstudioModelCli> listar() {
		return tables.findAllStudies().stream().map(mapper::fromDomainToAdapterCli).collect(Collectors.toList());
	}

	public EstudioModelCli buscar(Integer idProf, Integer ccPer) throws NoExistException {
		return mapper.fromDomainToAdapterCli(tables.findStudy(idProf, ccPer));
	}

	public EstudioModelCli actualizar(Integer idProf, Integer ccPer, EstudioModelCli model) throws NoExistException {
		model.setIdProf(idProf);
		model.setCcPer(ccPer);
		return mapper.fromDomainToAdapterCli(tables.updateStudy(idProf, ccPer, mapper.fromAdapterCliToDomain(model)));
	}

	public void eliminar(Integer idProf, Integer ccPer) throws NoExistException {
		tables.deleteStudy(idProf, ccPer);
	}

	public long contar() {
		return tables.countStudies();
	}
}
