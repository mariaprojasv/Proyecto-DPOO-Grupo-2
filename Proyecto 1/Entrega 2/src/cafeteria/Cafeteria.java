package cafeteria;

import java.util.ArrayList;

public class Cafeteria {

	private ArrayList<Producto> productos;

	public Cafeteria() {
		productos = new ArrayList<>();
	}

	public void agregarProducto(Producto producto) {

		if (producto != null) {
			productos.add(producto);
		}
	}

	public Producto buscarProducto(String nombre) {

		for (Producto producto : productos) {

			if (producto.getNombre().equalsIgnoreCase(nombre)) {
				return producto;
			}
		}

		return null;
	}

	public boolean venderProducto(String nombre, int cantidad) {

		Producto producto = buscarProducto(nombre);

		if (producto == null) {
			return false;
		}

		return producto.disminuirCantidad(cantidad);
	}

	public void reabastecer(String nombre, int cantidad) {

		Producto producto = buscarProducto(nombre);

		if (producto != null && cantidad > 0) {
			producto.aumentarCantidad(cantidad);
		}
	}

	public ArrayList<Producto> getProductos() {
		return productos;
	}

	public void mostrarMenu() {

		System.out.println("===== MENU CAFETERIA =====");

		for (Producto producto : productos) {
			System.out.println(producto);
		}
	}
}