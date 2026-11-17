package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.in.RelationalTablesInputPort;
import co.edu.javeriana.as.personapp.application.port.out.RelationalTablesOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.ResourceConflictException;
import co.edu.javeriana.as.personapp.common.exceptions.RelationshipConflictException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

@UseCase
public class RelationalTablesUseCase implements RelationalTablesInputPort {

	private final RelationalTablesOutputPort output;

	public RelationalTablesUseCase(RelationalTablesOutputPort output) {
		this.output = output;
	}

	@Override
	public Person createPerson(Person person) throws ResourceConflictException {
		validatePerson(person);
		if (output.findPersonById(person.getIdentification()) != null) {
			throw new ResourceConflictException("persona already exists: " + person.getIdentification());
		}
		return output.savePerson(person);
	}

	@Override
	public List<Person> findAllPeople() {
		return output.findAllPeople();
	}

	@Override
	public Person findPerson(Integer cc) throws NoExistException {
		return require(output.findPersonById(cc), "persona", cc);
	}

	@Override
	public Person updatePerson(Integer cc, Person person) throws NoExistException {
		validatePerson(person);
		if (!cc.equals(person.getIdentification())) {
			throw new IllegalArgumentException("cc must match the path");
		}
		require(output.findPersonById(cc), "persona", cc);
		return output.savePerson(person);
	}

	@Override
	public void deletePerson(Integer cc) throws NoExistException, RelationshipConflictException {
		require(output.findPersonById(cc), "persona", cc);
		if (output.hasPhonesForPerson(cc) || output.hasStudiesForPerson(cc)) {
			throw new RelationshipConflictException("persona is referenced by telefono or estudios: " + cc);
		}
		output.deletePerson(cc);
	}

	@Override
	public long countPeople() {
		return output.countPeople();
	}

	@Override
	public Profession createProfession(Profession profession) throws ResourceConflictException {
		validateProfession(profession);
		if (output.findProfessionById(profession.getIdentification()) != null) {
			throw new ResourceConflictException("profesion already exists: " + profession.getIdentification());
		}
		return output.saveProfession(profession);
	}

	@Override
	public List<Profession> findAllProfessions() {
		return output.findAllProfessions();
	}

	@Override
	public Profession findProfession(Integer id) throws NoExistException {
		return require(output.findProfessionById(id), "profesion", id);
	}

	@Override
	public Profession updateProfession(Integer id, Profession profession) throws NoExistException {
		validateProfession(profession);
		if (!id.equals(profession.getIdentification())) {
			throw new IllegalArgumentException("id must match the path");
		}
		require(output.findProfessionById(id), "profesion", id);
		return output.saveProfession(profession);
	}

	@Override
	public void deleteProfession(Integer id) throws NoExistException, RelationshipConflictException {
		require(output.findProfessionById(id), "profesion", id);
		if (output.hasStudiesForProfession(id)) {
			throw new RelationshipConflictException("profesion is referenced by estudios: " + id);
		}
		output.deleteProfession(id);
	}

	@Override
	public long countProfessions() {
		return output.countProfessions();
	}

	@Override
	public Study createStudy(Study study) throws NoExistException, ResourceConflictException {
		validateStudy(study);
		Integer ccPer = study.getPerson().getIdentification();
		Integer idProf = study.getProfession().getIdentification();
		require(output.findPersonById(ccPer), "persona", ccPer);
		require(output.findProfessionById(idProf), "profesion", idProf);
		if (output.findStudyById(idProf, ccPer) != null) {
			throw new ResourceConflictException("estudios already exists: " + idProf + "/" + ccPer);
		}
		return output.saveStudy(study);
	}

	@Override
	public List<Study> findAllStudies() {
		return output.findAllStudies();
	}

	@Override
	public Study findStudy(Integer idProf, Integer ccPer) throws NoExistException {
		return require(output.findStudyById(idProf, ccPer), "estudios", idProf + "/" + ccPer);
	}

	@Override
	public Study updateStudy(Integer idProf, Integer ccPer, Study study) throws NoExistException {
		validateStudy(study);
		if (!idProf.equals(study.getProfession().getIdentification())
				|| !ccPer.equals(study.getPerson().getIdentification())) {
			throw new IllegalArgumentException("id_prof and cc_per must match the path");
		}
		require(output.findStudyById(idProf, ccPer), "estudios", idProf + "/" + ccPer);
		require(output.findPersonById(ccPer), "persona", ccPer);
		require(output.findProfessionById(idProf), "profesion", idProf);
		return output.saveStudy(study);
	}

	@Override
	public void deleteStudy(Integer idProf, Integer ccPer) throws NoExistException {
		require(output.findStudyById(idProf, ccPer), "estudios", idProf + "/" + ccPer);
		output.deleteStudy(idProf, ccPer);
	}

	@Override
	public long countStudies() {
		return output.countStudies();
	}

	@Override
	public Phone createPhone(Phone phone) throws NoExistException, ResourceConflictException {
		validatePhone(phone);
		if (output.findPhoneByNumber(phone.getNumber()) != null) {
			throw new ResourceConflictException("telefono already exists: " + phone.getNumber());
		}
		require(output.findPersonById(phone.getOwner().getIdentification()), "persona",
				phone.getOwner().getIdentification());
		return output.savePhone(phone);
	}

	@Override
	public List<Phone> findAllPhones() {
		return output.findAllPhones();
	}

	@Override
	public Phone findPhone(String num) throws NoExistException {
		return require(output.findPhoneByNumber(num), "telefono", num);
	}

	@Override
	public Phone updatePhone(String num, Phone phone) throws NoExistException {
		validatePhone(phone);
		if (!num.equals(phone.getNumber())) {
			throw new IllegalArgumentException("num must match the path");
		}
		require(output.findPhoneByNumber(num), "telefono", num);
		require(output.findPersonById(phone.getOwner().getIdentification()), "persona",
				phone.getOwner().getIdentification());
		return output.savePhone(phone);
	}

	@Override
	public void deletePhone(String num) throws NoExistException {
		require(output.findPhoneByNumber(num), "telefono", num);
		output.deletePhone(num);
	}

	@Override
	public long countPhones() {
		return output.countPhones();
	}

	private <T> T require(T value, String table, Object key) throws NoExistException {
		if (value == null) {
			throw new NoExistException(table + " not found: " + key);
		}
		return value;
	}

	private void validatePerson(Person person) {
		if (person == null || person.getIdentification() == null || isBlank(person.getFirstName())
				|| isBlank(person.getLastName()) || person.getGender() == null
				|| (person.getAge() != null && person.getAge() < 0)) {
			throw new IllegalArgumentException("cc, nombre, apellido and valid genero are required");
		}
		if (person.getGender() != co.edu.javeriana.as.personapp.domain.Gender.MALE
				&& person.getGender() != co.edu.javeriana.as.personapp.domain.Gender.FEMALE) {
			throw new IllegalArgumentException("genero must be M or F");
		}
	}

	private void validateProfession(Profession profession) {
		if (profession == null || profession.getIdentification() == null || isBlank(profession.getName())) {
			throw new IllegalArgumentException("id and nom are required");
		}
	}

	private void validateStudy(Study study) {
		if (study == null || study.getPerson() == null || study.getPerson().getIdentification() == null
				|| study.getProfession() == null || study.getProfession().getIdentification() == null) {
			throw new IllegalArgumentException("id_prof and cc_per are required");
		}
	}

	private void validatePhone(Phone phone) {
		if (phone == null || isBlank(phone.getNumber()) || isBlank(phone.getCompany()) || phone.getOwner() == null
				|| phone.getOwner().getIdentification() == null) {
			throw new IllegalArgumentException("num, oper and duenio are required");
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
