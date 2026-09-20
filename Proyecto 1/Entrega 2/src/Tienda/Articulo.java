package Tienda;

public class Articulo {

	private String nombre;
	private String categoria;
	private double precio;
	private int cantidad;

	public Articulo(String nombre, String categoria, double precio, int cantidad) {
		this.nombre = nombre;
		this.categoria = categoria;
		this.precio = precio;
		this.cantidad = cantidad;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCategoria() {
		return categoria;
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
		return nombre + " | " + categoria + " | $" + precio + " | Stock: " + cantidad;
	}
}