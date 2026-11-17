package co.edu.javeriana.as.personapp.mariadb.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;

public interface EstudiosRepositoryMaria extends JpaRepository<EstudiosEntity, EstudiosEntityPK> {

	@Query("SELECT COUNT(e) FROM EstudiosEntity e WHERE e.estudiosEntityPK.ccPer = :ccPer")
	long countByCcPer(@Param("ccPer") Integer ccPer);

	@Query("SELECT COUNT(e) FROM EstudiosEntity e WHERE e.estudiosEntityPK.idProf = :idProf")
	long countByIdProf(@Param("idProf") Integer idProf);
}
