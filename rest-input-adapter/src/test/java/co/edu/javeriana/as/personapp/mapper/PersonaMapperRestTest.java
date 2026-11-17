package co.edu.javeriana.as.personapp.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

class PersonaMapperRestTest {

	private final PersonaMapperRest mapper = new PersonaMapperRest();

	@Test
	void mapsRequestToDomain() {
		Person person = mapper.fromAdapterToDomain(
				new PersonaRequest("123", " Ana ", "Lopez", "30", "F", "MONGO"));

		assertEquals(123, person.getIdentification());
		assertEquals("Ana", person.getFirstName());
		assertEquals(Gender.FEMALE, person.getGender());
		assertEquals(30, person.getAge());
	}

	@Test
	void mapsNullAgeAndMaleGender() {
		Person person = mapper.fromAdapterToDomain(
				new PersonaRequest("123", "Ana", "Lopez", null, "MALE", "MARIA"));

		assertNull(person.getAge());
		assertEquals(Gender.MALE, person.getGender());
	}

	@Test
	void rejectsInvalidIdentificationAndAge() {
		assertThrows(IllegalArgumentException.class, () -> mapper.fromAdapterToDomain(
				new PersonaRequest("abc", "Ana", "Lopez", "30", "F", "MONGO")));
		assertThrows(IllegalArgumentException.class, () -> mapper.fromAdapterToDomain(
				new PersonaRequest("123", "Ana", "Lopez", "-1", "F", "MONGO")));
	}

	@Test
	void mapsDomainToResponseWithCanonicalDatabaseAndGender() {
		Person person = new Person(123, "Ana", "Lopez", Gender.FEMALE);

		PersonaResponse response = mapper.fromDomainToAdapterRest(person, "MongoDB");

		assertEquals("123", response.getDni());
		assertEquals("F", response.getSex());
		assertEquals("MongoDB", response.getDatabase());
		assertEquals("OK", response.getStatus());
		assertNull(response.getAge());
	}
}
