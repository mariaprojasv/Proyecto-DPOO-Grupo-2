package cafeteria;

import java.util.ArrayList;
import usuarios.Usuario;
import reservas.Reserva;
import reservas.GestionReservas;

public class Cafeteria {

	private ArrayList<Producto> productos;
	private ArrayList<VentaCafeteria> ventas;

	public Cafeteria() {

	    productos = new ArrayList<>();
        ventas = new ArrayList<>();

	}

	public void agregarProducto(Producto producto) {

		if (producto != null && producto.getNombre() != null
                && buscarProducto(producto.getNombre()) == null) {
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
	return new ArrayList<>(productos);
	}

    public ArrayList<VentaCafeteria> getVentas() {
        return new ArrayList<>(ventas);
    }

    public VentaCafeteria registrarVenta(Usuario comprador, String nombre, int cantidad, double propina) {
        try {
            return registrarVentaValidada(comprador, nombre, cantidad, propina);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            return null;
        }
    }

    public VentaCafeteria registrarVentaValidada(Usuario comprador, String nombre, int cantidad, double propina) {
        if (comprador == null) {
            throw new IllegalArgumentException("El comprador es obligatorio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        if (!Double.isFinite(propina) || propina < 0) {
            throw new IllegalArgumentException("La propina no es valida");
        }
        Producto producto = buscarProducto(nombre);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe");
        }
        if (cantidad > producto.getCantidad()) {
            throw new IllegalStateException("No hay existencias suficientes");
        }
        VentaCafeteria venta = new VentaCafeteria(comprador, producto, cantidad, propina);
        if (!producto.disminuirCantidad(cantidad)) {
            throw new IllegalStateException("No fue posible descontar el inventario");
        }
        ventas.add(venta);
        return venta;
    }

    /** Venta con entrega dentro de una instalacion reservada. Las ventas para llevar usan registrarVentaValidada. */
    public VentaCafeteria despacharEnInstalacion(Usuario comprador, String nombre, int cantidad,
                                                 double propina, Reserva reserva, GestionReservas gestion) {
        if (gestion == null || reserva == null || comprador == null || reserva.getUsuario() != comprador) {
            throw new IllegalArgumentException("Se requiere una reserva vigente del comprador");
        }
        Producto producto = buscarProducto(nombre);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe");
        }
        boolean caliente = producto instanceof bebida && ((bebida) producto).esCaliente();
        if (!gestion.permitirUso(reserva, caliente)) {
            throw new IllegalStateException("No se permite despachar este producto en la instalacion");
        }
        return registrarVentaValidada(comprador, nombre, cantidad, propina);
    }

    /** Solo para restaurar una venta previamente registrada, sin modificar existencias. */
    public void restaurarVenta(VentaCafeteria venta) {
        if (venta == null || venta.getComprador() == null || venta.getProducto() == null
                || !productos.contains(venta.getProducto()) || ventas.contains(venta)) {
            throw new IllegalArgumentException("Venta de cafeteria restaurada invalida o repetida");
        }
        ventas.add(venta);
    }

	public void mostrarMenu() {

		System.out.println("===== MENU CAFETERIA =====");

		for (Producto producto : productos) {
			System.out.println(producto);
		}
	}
}