package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

public class TelefonoMenu extends CrudMenu {

	private final TelefonoInputAdapterCli adapter;

	public TelefonoMenu(TelefonoInputAdapterCli adapter, ConsoleInput in) {
		super(in);
		this.adapter = adapter;
	}

	@Override
	protected String singular() {
		return "un teléfono";
	}

	@Override
	protected String plural() {
		return "teléfonos";
	}

	private TelefonoModelCli leerDatos(String num) {
		return new TelefonoModelCli(num, in.readString("Operador: "), in.readInt("Cédula del dueño: "));
	}

	@Override
	protected void crear() throws Exception {
		String num = in.readString("Número: ");
		System.out.println("Creado: " + adapter.crear(leerDatos(num)));
	}

	@Override
	protected void listar() {
		adapter.listar().forEach(System.out::println);
	}

	@Override
	protected void buscar() throws Exception {
		System.out.println(adapter.buscar(in.readString("Número: ")));
	}

	@Override
	protected void actualizar() throws Exception {
		String num = in.readString("Número del teléfono a actualizar: ");
		adapter.buscar(num);
		System.out.println("Actualizado: " + adapter.actualizar(num, leerDatos(num)));
	}

	@Override
	protected void eliminar() throws Exception {
		adapter.eliminar(in.readString("Número del teléfono a eliminar: "));
		System.out.println("Teléfono eliminado.");
	}

	@Override
	protected void contar() {
		System.out.println("Total: " + adapter.contar());
	}
}
