package cafeteria;

public abstract class Producto {

	private String nombre;
	private double precio;
	private int cantidad;

	public Producto(String nombre, double precio, int cantidad) {
		this.nombre = nombre;
		this.precio = precio;
		this.cantidad = cantidad;
	}

	public String getNombre() {
		return nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void aumentarCantidad(int cantidad) {

		if (cantidad > 0) {
			this.cantidad += cantidad;
		}
	}

	public boolean disminuirCantidad(int cantidad) {

		if (cantidad <= 0 || cantidad > this.cantidad) {
			return false;
		}

		this.cantidad -= cantidad;
		return true;
	}

	@Override
	public String toString() {
		return nombre + " | $" + precio + " | Cantidad: " + cantidad;
	}
}