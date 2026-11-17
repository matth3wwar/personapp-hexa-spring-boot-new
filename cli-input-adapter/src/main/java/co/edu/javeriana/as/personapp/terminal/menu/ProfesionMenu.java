package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

public class ProfesionMenu extends CrudMenu {

	private final ProfesionInputAdapterCli adapter;

	public ProfesionMenu(ProfesionInputAdapterCli adapter, ConsoleInput in) {
		super(in);
		this.adapter = adapter;
	}

	@Override
	protected String singular() {
		return "una profesión";
	}

	@Override
	protected String plural() {
		return "profesiones";
	}

	private ProfesionModelCli leerDatos(Integer id) {
		return new ProfesionModelCli(id, in.readString("Nombre: "), in.readOptionalString("Descripción"));
	}

	@Override
	protected void crear() throws Exception {
		int id = in.readInt("Id: ");
		System.out.println("Creada: " + adapter.crear(leerDatos(id)));
	}

	@Override
	protected void listar() {
		adapter.listar().forEach(System.out::println);
	}

	@Override
	protected void buscar() throws Exception {
		System.out.println(adapter.buscar(in.readInt("Id: ")));
	}

	@Override
	protected void actualizar() throws Exception {
		int id = in.readInt("Id de la profesión a actualizar: ");
		adapter.buscar(id);
		System.out.println("Actualizada: " + adapter.actualizar(id, leerDatos(id)));
	}

	@Override
	protected void eliminar() throws Exception {
		adapter.eliminar(in.readInt("Id de la profesión a eliminar: "));
		System.out.println("Profesión eliminada.");
	}

	@Override
	protected void contar() {
		System.out.println("Total: " + adapter.contar());
	}
}
