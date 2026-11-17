package co.edu.javeriana.as.personapp.model.request;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EstudiosTablaRequest {
	private Integer id_prof;
	private Integer cc_per;
	private LocalDate fecha;
	private String univer;
}
