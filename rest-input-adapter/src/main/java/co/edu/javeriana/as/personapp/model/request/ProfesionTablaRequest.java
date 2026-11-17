package co.edu.javeriana.as.personapp.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfesionTablaRequest {
	private Integer id;
	private String nom;
	private String des;
}
