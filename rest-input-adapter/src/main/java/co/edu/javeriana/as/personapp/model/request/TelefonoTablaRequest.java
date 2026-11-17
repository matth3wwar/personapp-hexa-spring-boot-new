package co.edu.javeriana.as.personapp.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TelefonoTablaRequest {
	private String num;
	private String oper;
	private Integer duenio;
}
