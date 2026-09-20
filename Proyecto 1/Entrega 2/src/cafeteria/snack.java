package cafeteria;

public class snack extends Producto {

	private String alergenos;

	public snack(String nombre, double precio, int cantidad, String alergenos) {
		super(nombre, precio, cantidad);
		this.alergenos = alergenos;
	}

	public String getAlergenos() {
		return alergenos;
	}

	@Override
	public String toString() {
		return super.toString() + " | Alergenos: " + alergenos;
	}
}