package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.in.RelationalTablesInputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.ResourceConflictException;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Adapter
public class TelefonoInputAdapterCli {

	@Autowired
	private RelationalTablesInputPort tables;

	@Autowired
	private TelefonoMapperCli mapper;

	public TelefonoModelCli crear(TelefonoModelCli model) throws NoExistException, ResourceConflictException {
		return mapper.fromDomainToAdapterCli(tables.createPhone(mapper.fromAdapterCliToDomain(model)));
	}

	public List<TelefonoModelCli> listar() {
		return tables.findAllPhones().stream().map(mapper::fromDomainToAdapterCli).collect(Collectors.toList());
	}

	public TelefonoModelCli buscar(String num) throws NoExistException {
		return mapper.fromDomainToAdapterCli(tables.findPhone(num));
	}

	public TelefonoModelCli actualizar(String num, TelefonoModelCli model) throws NoExistException {
		model.setNum(num);
		return mapper.fromDomainToAdapterCli(tables.updatePhone(num, mapper.fromAdapterCliToDomain(model)));
	}

	public void eliminar(String num) throws NoExistException {
		tables.deletePhone(num);
	}

	public long contar() {
		return tables.countPhones();
	}
}
