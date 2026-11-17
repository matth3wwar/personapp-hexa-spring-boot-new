package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;

public class PersonaMenu extends CrudMenu {

	private static final int OPCION_REGRESAR_MODULOS = 0;
	private static final int PERSISTENCIA_MARIADB = 1;
	private static final int PERSISTENCIA_MONGODB = 2;

	private final PersonaInputAdapterCli adapter;

	public PersonaMenu(PersonaInputAdapterCli adapter, ConsoleInput in) {
		super(in);
		this.adapter = adapter;
	}

	/** Elige el motor de persistencia y luego entra al menu CRUD. */
	public void iniciarMenu() {
		while (true) {
			System.out.println("----------------------");
			System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
			System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
			System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
			int opcion = in.readInt("Ingrese una opción: ");
			try {
				switch (opcion) {
				case OPCION_REGRESAR_MODULOS:
					return;
				case PERSISTENCIA_MARIADB:
					adapter.setPersonOutputPortInjection("MARIA");
					ejecutar();
					break;
				case PERSISTENCIA_MONGODB:
					adapter.setPersonOutputPortInjection("MONGO");
					ejecutar();
					break;
				default:
					System.out.println("La opción elegida no es válida.");
				}
			} catch (InvalidOptionException e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}

	@Override
	protected String singular() {
		return "una persona";
	}

	@Override
	protected String plural() {
		return "personas";
	}

	private PersonaModelCli leerDatos(Integer cc) {
		PersonaModelCli model = new PersonaModelCli();
		model.setCc(cc);
		model.setNombre(in.readString("Nombre: "));
		model.setApellido(in.readString("Apellido: "));
		model.setGenero(in.readGender("Género"));
		model.setEdad(in.readOptionalInt("Edad"));
		return model;
	}

	@Override
	protected void crear() throws Exception {
		int cc = in.readInt("Cédula (cc): ");
		System.out.println("Creada: " + adapter.crear(leerDatos(cc)));
	}

	@Override
	protected void listar() {
		adapter.listar().forEach(System.out::println);
	}

	@Override
	protected void buscar() throws Exception {
		System.out.println(adapter.buscar(in.readInt("Cédula (cc): ")));
	}

	@Override
	protected void actualizar() throws Exception {
		int cc = in.readInt("Cédula (cc) de la persona a actualizar: ");
		adapter.buscar(cc);
		System.out.println("Actualizada: " + adapter.actualizar(cc, leerDatos(cc)));
	}

	@Override
	protected void eliminar() throws Exception {
		int cc = in.readInt("Cédula (cc) de la persona a eliminar: ");
		System.out.println(adapter.eliminar(cc) ? "Persona eliminada." : "No se pudo eliminar la persona.");
	}

	@Override
	protected void contar() {
		System.out.println("Total: " + adapter.contar());
	}
}
