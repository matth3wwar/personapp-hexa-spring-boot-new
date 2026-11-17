package co.edu.javeriana.as.personapp.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudiosTablaRequest;
import co.edu.javeriana.as.personapp.model.request.PersonaTablaRequest;
import co.edu.javeriana.as.personapp.model.request.ProfesionTablaRequest;
import co.edu.javeriana.as.personapp.model.request.TelefonoTablaRequest;

@Component
public class RelationalTablesRestMapper {

	public Person toDomain(PersonaTablaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request body is required");
		}
		Person person = new Person();
		person.setIdentification(request.getCc());
		person.setFirstName(request.getNombre());
		person.setLastName(request.getApellido());
		person.setGender(parseGender(request.getGenero()));
		person.setAge(request.getEdad());
		return person;
	}

	public PersonaTablaRequest toRequest(Person person) {
		PersonaTablaRequest request = new PersonaTablaRequest();
		request.setCc(person.getIdentification());
		request.setNombre(person.getFirstName());
		request.setApellido(person.getLastName());
		request.setGenero(person.getGender() == Gender.FEMALE ? "F" : "M");
		request.setEdad(person.getAge());
		return request;
	}

	public List<PersonaTablaRequest> toPersonRequests(List<Person> people) {
		return people.stream().map(this::toRequest).collect(Collectors.toList());
	}

	public Profession toDomain(ProfesionTablaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request body is required");
		}
		Profession profession = new Profession();
		profession.setIdentification(request.getId());
		profession.setName(request.getNom());
		profession.setDescription(request.getDes());
		return profession;
	}

	public ProfesionTablaRequest toRequest(Profession profession) {
		ProfesionTablaRequest request = new ProfesionTablaRequest();
		request.setId(profession.getIdentification());
		request.setNom(profession.getName());
		request.setDes(profession.getDescription());
		return request;
	}

	public List<ProfesionTablaRequest> toProfessionRequests(List<Profession> professions) {
		return professions.stream().map(this::toRequest).collect(Collectors.toList());
	}

	public Study toDomain(EstudiosTablaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request body is required");
		}
		Person person = new Person();
		person.setIdentification(request.getCc_per());
		Profession profession = new Profession();
		profession.setIdentification(request.getId_prof());
		return new Study(person, profession, request.getFecha(), request.getUniver());
	}

	public EstudiosTablaRequest toRequest(Study study) {
		EstudiosTablaRequest request = new EstudiosTablaRequest();
		request.setId_prof(study.getProfession().getIdentification());
		request.setCc_per(study.getPerson().getIdentification());
		request.setFecha(study.getGraduationDate());
		request.setUniver(study.getUniversityName());
		return request;
	}

	public List<EstudiosTablaRequest> toStudyRequests(List<Study> studies) {
		return studies.stream().map(this::toRequest).collect(Collectors.toList());
	}

	public Phone toDomain(TelefonoTablaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request body is required");
		}
		Person owner = new Person();
		owner.setIdentification(request.getDuenio());
		Phone phone = new Phone();
		phone.setNumber(request.getNum());
		phone.setCompany(request.getOper());
		phone.setOwner(owner);
		return phone;
	}

	public TelefonoTablaRequest toRequest(Phone phone) {
		TelefonoTablaRequest request = new TelefonoTablaRequest();
		request.setNum(phone.getNumber());
		request.setOper(phone.getCompany());
		request.setDuenio(phone.getOwner().getIdentification());
		return request;
	}

	public List<TelefonoTablaRequest> toPhoneRequests(List<Phone> phones) {
		return phones.stream().map(this::toRequest).collect(Collectors.toList());
	}

	private Gender parseGender(String gender) {
		if ("M".equals(gender)) {
			return Gender.MALE;
		}
		if ("F".equals(gender)) {
			return Gender.FEMALE;
		}
		throw new IllegalArgumentException("genero must be M or F");
	}
}
