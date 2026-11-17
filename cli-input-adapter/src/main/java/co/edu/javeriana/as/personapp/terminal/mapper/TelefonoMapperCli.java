package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Mapper
public class TelefonoMapperCli {

	public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
		Integer owner = phone.getOwner() == null ? null : phone.getOwner().getIdentification();
		return new TelefonoModelCli(phone.getNumber(), phone.getCompany(), owner);
	}

	public Phone fromAdapterCliToDomain(TelefonoModelCli model) {
		Person owner = new Person();
		owner.setIdentification(model.getDuenio());
		Phone phone = new Phone();
		phone.setNumber(model.getNum());
		phone.setCompany(model.getOper());
		phone.setOwner(owner);
		return phone;
	}
}
