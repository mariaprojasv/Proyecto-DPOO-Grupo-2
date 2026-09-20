package Tienda;

public class DetalleVenta {

	private Articulo articulo;
	private int cantidad;
	private double precioUnitario;

	public DetalleVenta(Articulo articulo, int cantidad) {
		this.articulo = articulo;
		this.cantidad = cantidad;
		this.precioUnitario = articulo.getPrecio();
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public int getCantidad() {
		return cantidad;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public double calcularSubtotal() {
		return cantidad * precioUnitario;
	}

	@Override
	public String toString() {
		return articulo.getNombre() + " x" + cantidad + " = $" + calcularSubtotal();
	}
}