package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

public class EstudioMenu extends CrudMenu {

	private final EstudioInputAdapterCli adapter;

	public EstudioMenu(EstudioInputAdapterCli adapter, ConsoleInput in) {
		super(in);
		this.adapter = adapter;
	}

	@Override
	protected String singular() {
		return "un estudio";
	}

	@Override
	protected String plural() {
		return "estudios";
	}

	private EstudioModelCli leerDatos(int idProf, int ccPer) {
		return new EstudioModelCli(idProf, ccPer, in.readOptionalDate("Fecha de graduación"),
				in.readOptionalString("Universidad"));
	}

	@Override
	protected void crear() throws Exception {
		int idProf = in.readInt("Id de la profesión: ");
		int ccPer = in.readInt("Cédula de la persona: ");
		System.out.println("Creado: " + adapter.crear(leerDatos(idProf, ccPer)));
	}

	@Override
	protected void listar() {
		adapter.listar().forEach(System.out::println);
	}

	@Override
	protected void buscar() throws Exception {
		int idProf = in.readInt("Id de la profesión: ");
		int ccPer = in.readInt("Cédula de la persona: ");
		System.out.println(adapter.buscar(idProf, ccPer));
	}

	@Override
	protected void actualizar() throws Exception {
		int idProf = in.readInt("Id de la profesión del estudio a actualizar: ");
		int ccPer = in.readInt("Cédula de la persona del estudio a actualizar: ");
		adapter.buscar(idProf, ccPer);
		System.out.println("Actualizado: " + adapter.actualizar(idProf, ccPer, leerDatos(idProf, ccPer)));
	}

	@Override
	protected void eliminar() throws Exception {
		int idProf = in.readInt("Id de la profesión del estudio a eliminar: ");
		int ccPer = in.readInt("Cédula de la persona del estudio a eliminar: ");
		adapter.eliminar(idProf, ccPer);
		System.out.println("Estudio eliminado.");
	}

	@Override
	protected void contar() {
		System.out.println("Total: " + adapter.contar());
	}
}
