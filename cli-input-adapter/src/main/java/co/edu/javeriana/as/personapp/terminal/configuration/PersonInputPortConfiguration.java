package co.edu.javeriana.as.personapp.terminal.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;

@Configuration
public class PersonInputPortConfiguration {

	@Bean
	@Qualifier("personInputPortMaria")
	public PersonInputPort personInputPortMaria(
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPort) {
		return new PersonUseCase(personOutputPort);
	}

	@Bean
	@Qualifier("personInputPortMongo")
	public PersonInputPort personInputPortMongo(
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPort) {
		return new PersonUseCase(personOutputPort);
	}
}
