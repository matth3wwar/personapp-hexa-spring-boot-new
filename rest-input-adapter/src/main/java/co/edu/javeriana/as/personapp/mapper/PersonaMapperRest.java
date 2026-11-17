package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {
	
	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}
	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}
	
	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				person.getIdentification() == null ? null : person.getIdentification().toString(),
				person.getFirstName(), 
				person.getLastName(), 
				person.getAge() == null ? null : person.getAge().toString(),
				person.getGender() == Gender.MALE ? "M" : person.getGender() == Gender.FEMALE ? "F" : "OTHER",
				database,
				"OK");
	}

	public Person fromAdapterToDomain(PersonaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request body is required");
		}
		return fromAdapterToDomain(request, parseIdentification(request.getDni()));
	}

	public Person fromAdapterToDomain(PersonaRequest request, Integer identification) {
		if (request == null) {
			throw new IllegalArgumentException("Request body is required");
		}
		if (identification == null) {
			throw new IllegalArgumentException("dni is required");
		}
		if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
			throw new IllegalArgumentException("firstName is required");
		}
		if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
			throw new IllegalArgumentException("lastName is required");
		}

		Person person = new Person();
		person.setIdentification(identification);
		person.setFirstName(request.getFirstName().trim());
		person.setLastName(request.getLastName().trim());
		person.setGender(parseGender(request.getSex()));
		person.setAge(parseAge(request.getAge()));
		return person;
	}

	private Integer parseIdentification(String dni) {
		if (dni == null || dni.trim().isEmpty()) {
			throw new IllegalArgumentException("dni is required");
		}
		try {
			return Integer.valueOf(dni.trim());
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("dni must be a valid integer", exception);
		}
	}

	private Gender parseGender(String sex) {
		if (sex == null) {
			throw new IllegalArgumentException("sex is required");
		}
		switch (sex.trim().toUpperCase()) {
		case "M":
		case "MALE":
			return Gender.MALE;
		case "F":
		case "FEMALE":
			return Gender.FEMALE;
		default:
			throw new IllegalArgumentException("sex must be M or F");
		}
	}

	private Integer parseAge(String age) {
		if (age == null || age.trim().isEmpty()) {
			return null;
		}
		try {
			Integer parsedAge = Integer.valueOf(age.trim());
			if (parsedAge < 0) {
				throw new IllegalArgumentException("age cannot be negative");
			}
			return parsedAge;
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("age must be a valid integer", exception);
		}
	}
		
}
