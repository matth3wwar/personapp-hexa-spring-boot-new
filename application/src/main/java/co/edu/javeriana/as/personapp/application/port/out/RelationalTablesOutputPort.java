package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

@Port
public interface RelationalTablesOutputPort {

	Person savePerson(Person person);
	List<Person> findAllPeople();
	Person findPersonById(Integer cc);
	void deletePerson(Integer cc);
	long countPeople();
	boolean hasPhonesForPerson(Integer cc);
	boolean hasStudiesForPerson(Integer cc);

	Profession saveProfession(Profession profession);
	List<Profession> findAllProfessions();
	Profession findProfessionById(Integer id);
	void deleteProfession(Integer id);
	long countProfessions();
	boolean hasStudiesForProfession(Integer id);

	Study saveStudy(Study study);
	List<Study> findAllStudies();
	Study findStudyById(Integer idProf, Integer ccPer);
	void deleteStudy(Integer idProf, Integer ccPer);
	long countStudies();

	Phone savePhone(Phone phone);
	List<Phone> findAllPhones();
	Phone findPhoneByNumber(String num);
	void deletePhone(String num);
	long countPhones();
}
