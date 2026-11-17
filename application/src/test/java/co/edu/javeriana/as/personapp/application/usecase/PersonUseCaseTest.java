package co.edu.javeriana.as.personapp.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;

class PersonUseCaseTest {

	private static final Integer IDENTIFICATION = 123456789;

	private MemoryPersonOutputPort personOutputPort;

	private PersonUseCase personUseCase;

	@BeforeEach
	void setUp() {
		personOutputPort = new MemoryPersonOutputPort();
		personUseCase = new PersonUseCase(personOutputPort);
	}

	@Test
	void createSavesAndReturnsPerson() {
		Person person = person();

		Person result = personUseCase.create(person);

		assertEquals(person, result);
		assertEquals(person, personOutputPort.findById(IDENTIFICATION));
	}

	@Test
	void editSavesPersonWhenItExists() throws NoExistException {
		personOutputPort.save(person());
		Person updatedPerson = new Person(IDENTIFICATION, "Ana Maria", "Lopez", Gender.FEMALE);

		Person result = personUseCase.edit(IDENTIFICATION, updatedPerson);

		assertEquals(updatedPerson, result);
		assertEquals(updatedPerson, personOutputPort.findById(IDENTIFICATION));
	}

	@Test
	void editThrowsWhenPersonDoesNotExist() {
		assertThrows(NoExistException.class, () -> personUseCase.edit(IDENTIFICATION, person()));
	}

	@Test
	void dropDeletesExistingPerson() throws NoExistException {
		personOutputPort.save(person());

		assertEquals(Boolean.TRUE, personUseCase.drop(IDENTIFICATION));
		assertEquals(null, personOutputPort.findById(IDENTIFICATION));
	}

	@Test
	void dropThrowsWhenPersonDoesNotExist() {
		assertThrows(NoExistException.class, () -> personUseCase.drop(IDENTIFICATION));
	}

	@Test
	void findAllReturnsAllPeople() {
		List<Person> people = Arrays.asList(person(), new Person(2, "Juan", "Perez", Gender.MALE));
		people.forEach(personOutputPort::save);

		assertEquals(people, personUseCase.findAll());
	}

	@Test
	void findOneReturnsExistingPerson() throws NoExistException {
		Person person = person();
		personOutputPort.save(person);

		assertEquals(person, personUseCase.findOne(IDENTIFICATION));
	}

	@Test
	void findOneThrowsWhenPersonDoesNotExist() {
		assertThrows(NoExistException.class, () -> personUseCase.findOne(IDENTIFICATION));
	}

	@Test
	void countReturnsNumberOfPeople() {
		personOutputPort.save(person());

		assertEquals(1, personUseCase.count());
	}

	private Person person() {
		return new Person(IDENTIFICATION, "Ana", "Lopez", Gender.FEMALE);
	}

	private static class MemoryPersonOutputPort implements PersonOutputPort {

		private final List<Person> people = new ArrayList<>();

		@Override
		public Person save(Person person) {
			people.removeIf(existingPerson -> existingPerson.getIdentification().equals(person.getIdentification()));
			people.add(person);
			return person;
		}

		@Override
		public Boolean delete(Integer identification) {
			return people.removeIf(person -> person.getIdentification().equals(identification));
		}

		@Override
		public List<Person> find() {
			return new ArrayList<>(people);
		}

		@Override
		public Person findById(Integer identification) {
			return people.stream()
					.filter(person -> person.getIdentification().equals(identification))
					.findFirst()
					.orElse(null);
		}
	}
}
