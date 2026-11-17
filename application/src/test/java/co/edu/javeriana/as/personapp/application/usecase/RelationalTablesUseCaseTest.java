package co.edu.javeriana.as.personapp.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.application.port.out.RelationalTablesOutputPort;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.RelationshipConflictException;
import co.edu.javeriana.as.personapp.common.exceptions.ResourceConflictException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

class RelationalTablesUseCaseTest {

	private RelationalTablesUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new RelationalTablesUseCase(new InMemoryRelationalTables());
	}

	@Test
	void supportsCrudAndEnforcesRelationalReferences() throws Exception {
		Person person = new Person(123, "Ana", "Lopez", Gender.FEMALE);
		Profession profession = new Profession(7, "Ingenieria", "Descripcion", null);

		assertEquals(person, useCase.createPerson(person));
		assertEquals(profession, useCase.createProfession(profession));
		assertEquals(1, useCase.countPeople());
		assertEquals(1, useCase.countProfessions());
		assertEquals(person, useCase.findPerson(123));
		assertEquals(profession, useCase.findProfession(7));

		Study study = new Study(person, profession, LocalDate.of(2020, 1, 1), "Universidad");
		Phone phone = new Phone("5551234", "Operador", person);
		useCase.createStudy(study);
		useCase.createPhone(phone);
		assertEquals(1, useCase.countStudies());
		assertEquals(1, useCase.countPhones());
		assertEquals(study, useCase.findStudy(7, 123));
		assertEquals(phone, useCase.findPhone("5551234"));

		assertThrows(RelationshipConflictException.class, () -> useCase.deletePerson(123));
		assertThrows(RelationshipConflictException.class, () -> useCase.deleteProfession(7));

		Person updatedPerson = new Person(123, "Ana Maria", "Lopez", Gender.FEMALE);
		assertEquals(updatedPerson, useCase.updatePerson(123, updatedPerson));
		useCase.deletePhone("5551234");
		useCase.deleteStudy(7, 123);
		useCase.deletePerson(123);
		useCase.deleteProfession(7);
		assertEquals(0, useCase.countPeople());
		assertEquals(0, useCase.countProfessions());
		assertEquals(0, useCase.countStudies());
		assertEquals(0, useCase.countPhones());
	}

	@Test
	void rejectsMissingForeignRecordsAndDuplicateKeys() throws Exception {
		Person person = new Person(123, "Ana", "Lopez", Gender.FEMALE);
		Profession profession = new Profession(7, "Ingenieria", null, null);
		useCase.createPerson(person);
		useCase.createProfession(profession);

		assertThrows(ResourceConflictException.class, () -> useCase.createPerson(person));
		assertThrows(ResourceConflictException.class, () -> useCase.createProfession(profession));
		assertThrows(NoExistException.class,
				() -> useCase.createStudy(new Study(person, new Profession(8, "No existe", null, null), null, null)));
		Person missingOwner = new Person();
		missingOwner.setIdentification(456);
		assertThrows(NoExistException.class,
				() -> useCase.createPhone(new Phone("5551234", "Operador", missingOwner)));
	}

	private static class InMemoryRelationalTables implements RelationalTablesOutputPort {

		private final Map<Integer, Person> people = new HashMap<>();
		private final Map<Integer, Profession> professions = new HashMap<>();
		private final Map<String, Study> studies = new HashMap<>();
		private final Map<String, Phone> phones = new HashMap<>();

		@Override
		public Person savePerson(Person person) {
			people.put(person.getIdentification(), person);
			return person;
		}

		@Override
		public List<Person> findAllPeople() {
			return new ArrayList<>(people.values());
		}

		@Override
		public Person findPersonById(Integer cc) {
			return people.get(cc);
		}

		@Override
		public void deletePerson(Integer cc) {
			people.remove(cc);
		}

		@Override
		public long countPeople() {
			return people.size();
		}

		@Override
		public boolean hasPhonesForPerson(Integer cc) {
			return phones.values().stream().anyMatch(phone -> cc.equals(phone.getOwner().getIdentification()));
		}

		@Override
		public boolean hasStudiesForPerson(Integer cc) {
			return studies.values().stream().anyMatch(study -> cc.equals(study.getPerson().getIdentification()));
		}

		@Override
		public Profession saveProfession(Profession profession) {
			professions.put(profession.getIdentification(), profession);
			return profession;
		}

		@Override
		public List<Profession> findAllProfessions() {
			return new ArrayList<>(professions.values());
		}

		@Override
		public Profession findProfessionById(Integer id) {
			return professions.get(id);
		}

		@Override
		public void deleteProfession(Integer id) {
			professions.remove(id);
		}

		@Override
		public long countProfessions() {
			return professions.size();
		}

		@Override
		public boolean hasStudiesForProfession(Integer id) {
			return studies.values().stream()
					.anyMatch(study -> id.equals(study.getProfession().getIdentification()));
		}

		@Override
		public Study saveStudy(Study study) {
			studies.put(studyKey(study), study);
			return study;
		}

		@Override
		public List<Study> findAllStudies() {
			return new ArrayList<>(studies.values());
		}

		@Override
		public Study findStudyById(Integer idProf, Integer ccPer) {
			return studies.get(idProf + "/" + ccPer);
		}

		@Override
		public void deleteStudy(Integer idProf, Integer ccPer) {
			studies.remove(idProf + "/" + ccPer);
		}

		@Override
		public long countStudies() {
			return studies.size();
		}

		@Override
		public Phone savePhone(Phone phone) {
			phones.put(phone.getNumber(), phone);
			return phone;
		}

		@Override
		public List<Phone> findAllPhones() {
			return new ArrayList<>(phones.values());
		}

		@Override
		public Phone findPhoneByNumber(String num) {
			return phones.get(num);
		}

		@Override
		public void deletePhone(String num) {
			phones.remove(num);
		}

		@Override
		public long countPhones() {
			return phones.size();
		}

		private String studyKey(Study study) {
			return study.getProfession().getIdentification() + "/" + study.getPerson().getIdentification();
		}
	}
}
