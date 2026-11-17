package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Lectura de consola basada siempre en lineas completas. Reintenta ante entradas invalidas.
 * Si la entrada se cierra (EOF), Scanner lanza NoSuchElementException y se propaga.
 */
public class ConsoleInput {

	private final Scanner scanner;

	public ConsoleInput(Scanner scanner) {
		this.scanner = scanner;
	}

	public String readLine(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine().trim();
	}

	public String readString(String prompt) {
		while (true) {
			String value = readLine(prompt);
			if (!value.isEmpty()) {
				return value;
			}
			System.out.println("El valor es obligatorio.");
		}
	}

	public String readOptionalString(String prompt) {
		String value = readLine(prompt + " (opcional, Enter para omitir): ");
		return value.isEmpty() ? null : value;
	}

	public int readInt(String prompt) {
		while (true) {
			String value = readLine(prompt);
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				System.out.println("Solo se permiten numeros enteros.");
			}
		}
	}

	public Integer readOptionalInt(String prompt) {
		while (true) {
			String value = readLine(prompt + " (opcional, Enter para omitir): ");
			if (value.isEmpty()) {
				return null;
			}
			try {
				int number = Integer.parseInt(value);
				if (number < 0) {
					System.out.println("Debe ser un numero positivo.");
					continue;
				}
				return number;
			} catch (NumberFormatException e) {
				System.out.println("Solo se permiten numeros enteros.");
			}
		}
	}

	public LocalDate readOptionalDate(String prompt) {
		while (true) {
			String value = readLine(prompt + " [AAAA-MM-DD] (opcional, Enter para omitir): ");
			if (value.isEmpty()) {
				return null;
			}
			try {
				return LocalDate.parse(value);
			} catch (DateTimeParseException e) {
				System.out.println("Fecha invalida, use el formato AAAA-MM-DD.");
			}
		}
	}

	/** Devuelve "MALE" o "FEMALE". */
	public String readGender(String prompt) {
		while (true) {
			String value = readLine(prompt + " [M/F]: ").toUpperCase();
			if (value.equals("M")) {
				return "MALE";
			}
			if (value.equals("F")) {
				return "FEMALE";
			}
			System.out.println("Ingrese M o F.");
		}
	}
}
