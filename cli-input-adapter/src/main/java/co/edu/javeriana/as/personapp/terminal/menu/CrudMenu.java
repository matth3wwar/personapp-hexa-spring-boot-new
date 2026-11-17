package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.NoSuchElementException;

/** Menu comun con las 6 operaciones CRUD; cada modulo implementa las acciones. */
public abstract class CrudMenu {

	private static final int REGRESAR = 0;
	private static final int CREAR = 1;
	private static final int LISTAR = 2;
	private static final int BUSCAR = 3;
	private static final int ACTUALIZAR = 4;
	private static final int ELIMINAR = 5;
	private static final int CONTAR = 6;

	protected final ConsoleInput in;

	protected CrudMenu(ConsoleInput in) {
		this.in = in;
	}

	/** Nombre en singular y plural para los textos del menu, p. ej. "persona". */
	protected abstract String singular();

	protected abstract String plural();

	protected abstract void crear() throws Exception;

	protected abstract void listar() throws Exception;

	protected abstract void buscar() throws Exception;

	protected abstract void actualizar() throws Exception;

	protected abstract void eliminar() throws Exception;

	protected abstract void contar() throws Exception;

	public void ejecutar() {
		while (true) {
			mostrarMenu();
			int opcion = in.readInt("Ingrese una opción: ");
			if (opcion == REGRESAR) {
				return;
			}
			try {
				switch (opcion) {
				case CREAR:
					crear();
					break;
				case LISTAR:
					listar();
					break;
				case BUSCAR:
					buscar();
					break;
				case ACTUALIZAR:
					actualizar();
					break;
				case ELIMINAR:
					eliminar();
					break;
				case CONTAR:
					contar();
					break;
				default:
					System.out.println("La opción elegida no es válida.");
				}
			} catch (NoSuchElementException e) {
				throw e; // entrada cerrada
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}

	private void mostrarMenu() {
		System.out.println("----------------------");
		System.out.println(CREAR + " para crear " + singular());
		System.out.println(LISTAR + " para ver todas/os las/los " + plural());
		System.out.println(BUSCAR + " para buscar " + singular() + " por identificador");
		System.out.println(ACTUALIZAR + " para actualizar " + singular());
		System.out.println(ELIMINAR + " para eliminar " + singular());
		System.out.println(CONTAR + " para contar " + plural());
		System.out.println(REGRESAR + " para regresar");
	}
}
