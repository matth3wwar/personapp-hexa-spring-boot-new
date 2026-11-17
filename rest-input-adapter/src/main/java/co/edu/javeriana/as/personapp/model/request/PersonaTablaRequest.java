package co.edu.javeriana.as.personapp.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonaTablaRequest {
	private Integer cc;
	private String nombre;
	private String apellido;
	private String genero;
	private Integer edad;
}
