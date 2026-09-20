package cafeteria;

public class bebida extends Producto{

	private boolean caliente;

	public bebida(String nombre, double precio, int cantidad, boolean caliente) {
		super(nombre, precio, cantidad);
		this.caliente = caliente;
	}

	public boolean esCaliente() {
		return caliente;
	}

	@Override
	public String toString() {

		String tipo;

		if (caliente) {
			tipo = "Caliente";
		} else {
			tipo = "Fria";
		}

		return super.toString() + " | " + tipo;
	}
}