package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

@Mapper
public class EstudioMapperCli {

	public EstudioModelCli fromDomainToAdapterCli(Study study) {
		return new EstudioModelCli(study.getProfession().getIdentification(), study.getPerson().getIdentification(),
				study.getGraduationDate(), study.getUniversityName());
	}

	public Study fromAdapterCliToDomain(EstudioModelCli model) {
		Person person = new Person();
		person.setIdentification(model.getCcPer());
		Profession profession = new Profession();
		profession.setIdentification(model.getIdProf());
		Study study = new Study();
		study.setPerson(person);
		study.setProfession(profession);
		study.setGraduationDate(model.getFecha());
		study.setUniversityName(model.getUniver());
		return study;
	}
}
