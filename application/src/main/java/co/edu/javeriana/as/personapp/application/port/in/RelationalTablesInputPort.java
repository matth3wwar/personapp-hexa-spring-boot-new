package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.ResourceConflictException;
import co.edu.javeriana.as.personapp.common.exceptions.RelationshipConflictException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

@Port
public interface RelationalTablesInputPort {

	Person createPerson(Person person) throws ResourceConflictException;
	List<Person> findAllPeople();
	Person findPerson(Integer cc) throws NoExistException;
	Person updatePerson(Integer cc, Person person) throws NoExistException;
	void deletePerson(Integer cc) throws NoExistException, RelationshipConflictException;
	long countPeople();

	Profession createProfession(Profession profession) throws ResourceConflictException;
	List<Profession> findAllProfessions();
	Profession findProfession(Integer id) throws NoExistException;
	Profession updateProfession(Integer id, Profession profession) throws NoExistException;
	void deleteProfession(Integer id) throws NoExistException, RelationshipConflictException;
	long countProfessions();

	Study createStudy(Study study) throws NoExistException, ResourceConflictException;
	List<Study> findAllStudies();
	Study findStudy(Integer idProf, Integer ccPer) throws NoExistException;
	Study updateStudy(Integer idProf, Integer ccPer, Study study) throws NoExistException;
	void deleteStudy(Integer idProf, Integer ccPer) throws NoExistException;
	long countStudies();

	Phone createPhone(Phone phone) throws NoExistException, ResourceConflictException;
	List<Phone> findAllPhones();
	Phone findPhone(String num) throws NoExistException;
	Phone updatePhone(String num, Phone phone) throws NoExistException;
	void deletePhone(String num) throws NoExistException;
	long countPhones();
}
