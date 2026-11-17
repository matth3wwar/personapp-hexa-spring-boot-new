package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.as.personapp.application.port.out.RelationalTablesOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import co.edu.javeriana.as.personapp.mariadb.repository.EstudiosRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.ProfesionRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.TelefonoRepositoryMaria;

@Adapter("relationalTablesOutputAdapterMaria")
@Transactional
public class RelationalTablesOutputAdapterMaria implements RelationalTablesOutputPort {

	@Autowired
	private PersonaRepositoryMaria personas;

	@Autowired
	private ProfesionRepositoryMaria profesiones;

	@Autowired
	private EstudiosRepositoryMaria estudios;

	@Autowired
	private TelefonoRepositoryMaria telefonos;

	@Override
	public Person savePerson(Person person) {
		PersonaEntity entity = personas.findById(person.getIdentification()).orElseGet(PersonaEntity::new);
		entity.setCc(person.getIdentification());
		entity.setNombre(person.getFirstName());
		entity.setApellido(person.getLastName());
		entity.setGenero(person.getGender() == Gender.FEMALE ? 'F' : 'M');
		entity.setEdad(person.getAge());
		return map(personas.save(entity));
	}

	@Override
	public List<Person> findAllPeople() {
		return personas.findAll().stream().map(this::map).collect(Collectors.toList());
	}

	@Override
	public Person findPersonById(Integer cc) {
		return personas.findById(cc).map(this::map).orElse(null);
	}

	@Override
	public void deletePerson(Integer cc) {
		personas.deleteById(cc);
	}

	@Override
	public long countPeople() {
		return personas.count();
	}

	@Override
	public boolean hasPhonesForPerson(Integer cc) {
		return telefonos.countByDuenioCc(cc) > 0;
	}

	@Override
	public boolean hasStudiesForPerson(Integer cc) {
		return estudios.countByCcPer(cc) > 0;
	}

	@Override
	public Profession saveProfession(Profession profession) {
		ProfesionEntity entity = profesiones.findById(profession.getIdentification()).orElseGet(ProfesionEntity::new);
		entity.setId(profession.getIdentification());
		entity.setNom(profession.getName());
		entity.setDes(profession.getDescription());
		return map(profesiones.save(entity));
	}

	@Override
	public List<Profession> findAllProfessions() {
		return profesiones.findAll().stream().map(this::map).collect(Collectors.toList());
	}

	@Override
	public Profession findProfessionById(Integer id) {
		return profesiones.findById(id).map(this::map).orElse(null);
	}

	@Override
	public void deleteProfession(Integer id) {
		profesiones.deleteById(id);
	}

	@Override
	public long countProfessions() {
		return profesiones.count();
	}

	@Override
	public boolean hasStudiesForProfession(Integer id) {
		return estudios.countByIdProf(id) > 0;
	}

	@Override
	public Study saveStudy(Study study) {
		Integer idProf = study.getProfession().getIdentification();
		Integer ccPer = study.getPerson().getIdentification();
		EstudiosEntity entity = new EstudiosEntity(new EstudiosEntityPK(idProf, ccPer));
		entity.setFecha(study.getGraduationDate() == null ? null : Date.valueOf(study.getGraduationDate()));
		entity.setUniver(study.getUniversityName());
		entity.setPersona(personReference(ccPer));
		entity.setProfesion(professionReference(idProf));
		return map(estudios.save(entity));
	}

	@Override
	public List<Study> findAllStudies() {
		return estudios.findAll().stream().map(this::map).collect(Collectors.toList());
	}

	@Override
	public Study findStudyById(Integer idProf, Integer ccPer) {
		return estudios.findById(new EstudiosEntityPK(idProf, ccPer)).map(this::map).orElse(null);
	}

	@Override
	public void deleteStudy(Integer idProf, Integer ccPer) {
		estudios.deleteById(new EstudiosEntityPK(idProf, ccPer));
	}

	@Override
	public long countStudies() {
		return estudios.count();
	}

	@Override
	public Phone savePhone(Phone phone) {
		TelefonoEntity entity = new TelefonoEntity();
		entity.setNum(phone.getNumber());
		entity.setOper(phone.getCompany());
		entity.setDuenio(personReference(phone.getOwner().getIdentification()));
		return map(telefonos.save(entity));
	}

	@Override
	public List<Phone> findAllPhones() {
		return telefonos.findAll().stream().map(this::map).collect(Collectors.toList());
	}

	@Override
	public Phone findPhoneByNumber(String num) {
		return telefonos.findById(num).map(this::map).orElse(null);
	}

	@Override
	public void deletePhone(String num) {
		telefonos.deleteById(num);
	}

	@Override
	public long countPhones() {
		return telefonos.count();
	}

	private Person map(PersonaEntity entity) {
		Person person = new Person();
		person.setIdentification(entity.getCc());
		person.setFirstName(entity.getNombre());
		person.setLastName(entity.getApellido());
		person.setGender(entity.getGenero() == 'F' ? Gender.FEMALE : Gender.MALE);
		person.setAge(entity.getEdad());
		return person;
	}

	private Profession map(ProfesionEntity entity) {
		return new Profession(entity.getId(), entity.getNom(), entity.getDes(), null);
	}

	private Study map(EstudiosEntity entity) {
		Person person = new Person();
		person.setIdentification(entity.getEstudiosPK().getCcPer());
		Profession profession = new Profession();
		profession.setIdentification(entity.getEstudiosPK().getIdProf());
		return new Study(person, profession,
				entity.getFecha() == null ? null : new Date(entity.getFecha().getTime()).toLocalDate(),
				entity.getUniver());
	}

	private Phone map(TelefonoEntity entity) {
		Person owner = new Person();
		owner.setIdentification(entity.getDuenio().getCc());
		return new Phone(entity.getNum(), entity.getOper(), owner);
	}

	private PersonaEntity personReference(Integer cc) {
		PersonaEntity entity = new PersonaEntity();
		entity.setCc(cc);
		return entity;
	}

	private ProfesionEntity professionReference(Integer id) {
		ProfesionEntity entity = new ProfesionEntity();
		entity.setId(id);
		return entity;
	}
}
