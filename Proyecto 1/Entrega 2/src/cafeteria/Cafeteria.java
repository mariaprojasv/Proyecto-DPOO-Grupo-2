package cafeteria;

import java.util.ArrayList;
import usuarios.Usuario;

public class Cafeteria {

	private ArrayList<Producto> productos;
	private ArrayList<VentaCafeteria> ventas;

	public Cafeteria() {

	    productos = new ArrayList<>();
        ventas = new ArrayList<>();

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

    public ArrayList<VentaCafeteria> getVentas() {
        return ventas;
    }

    public VentaCafeteria registrarVenta(Usuario comprador, String nombre, int cantidad, double propina) {
        Producto producto = buscarProducto(nombre);
        if (comprador == null || producto == null || cantidad <= 0 || propina < 0) {
            return null;
        }
        if (!producto.disminuirCantidad(cantidad)) {
            return null;
        }
        VentaCafeteria venta = new VentaCafeteria(comprador, producto, cantidad, propina);
        ventas.add(venta);
        return venta;
    }

	public void mostrarMenu() {

		System.out.println("===== MENU CAFETERIA =====");

		for (Producto producto : productos) {
			System.out.println(producto);
		}
	}
}