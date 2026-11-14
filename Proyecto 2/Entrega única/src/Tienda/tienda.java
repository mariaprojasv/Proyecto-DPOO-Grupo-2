package Tienda;

import java.util.ArrayList;

import usuarios.Usuario;

public class tienda {

	private ArrayList<Articulo> articulos;
	private ArrayList<Venta> ventas;

	public tienda() {
		articulos = new ArrayList<>();
		ventas = new ArrayList<>();
	}

	public void agregarArticulo(Articulo articulo) {

		if (articulo != null && articulo.getNombre() != null
                && buscarArticulo(articulo.getNombre()) == null) {
			articulos.add(articulo);
		}
	}

    /** Retira un articulo existente del inventario real sin exponer la lista interna. */
    public boolean retirarArticulo(String nombre) {
        Articulo articulo = buscarArticulo(nombre);
        return articulo != null && articulos.remove(articulo);
    }

	public Articulo buscarArticulo(String nombre) {

		for (Articulo articulo : articulos) {

			if (articulo.getNombre().equalsIgnoreCase(nombre)) {
				return articulo;
			}
		}

		return null;
	}

	public boolean venderArticulo(Usuario comprador, String nombre, int cantidad) {

		Articulo articulo = buscarArticulo(nombre);

		if (articulo == null) {
			return false;
		}

		if (cantidad <= 0 || cantidad > articulo.getCantidad()) {
			return false;
		}

        if (comprador == null) return false;
        Venta venta = crearVenta(comprador);
        venta.agregarDetalle(articulo, cantidad);
        venta.finalizarVenta();
        registrarVentaFinalizada(venta);
        return true;
	}

	public Venta crearVenta(Usuario comprador) {

        if (comprador == null) throw new IllegalArgumentException("Comprador requerido");
        return new Venta(comprador);
	}

    /** Registra exclusivamente compras confirmadas. */
    public void registrarVentaFinalizada(Venta venta) {
        if (venta == null || venta.getComprador() == null || !venta.isFinalizada()
                || venta.getDetalles().isEmpty() || ventas.contains(venta)) {
            throw new IllegalArgumentException("La venta debe estar finalizada y no registrada");
        }
        ventas.add(venta);
    }

    /** Descarta un carrito no confirmado y devuelve sus unidades al inventario. */
    public void cancelarVenta(Venta venta) {
        if (venta == null || venta.isFinalizada() || ventas.contains(venta)) {
            throw new IllegalArgumentException("Solo se puede cancelar una venta pendiente");
        }
        venta.cancelarVentaPendiente();
    }

	public void reabastecer(String nombre, int cantidad) {

		Articulo articulo = buscarArticulo(nombre);

		if (articulo != null && cantidad > 0) {
			articulo.aumentarCantidad(cantidad);
		}
	}

	public ArrayList<Articulo> getArticulos() {
	return new ArrayList<>(articulos);
	}

	public ArrayList<Venta> getVentas() {
	return new ArrayList<>(ventas);
	}

    /** Solo para restaurar ventas ya guardadas, sin descontar inventario otra vez. */
    public void restaurarVenta(Venta venta) {
        if (venta == null || venta.getComprador() == null || !venta.isFinalizada() || ventas.contains(venta)) {
            throw new IllegalArgumentException("Venta restaurada invalida o repetida");
        }
        ventas.add(venta);
    }

	public void mostrarInventario() {

		System.out.println("===== INVENTARIO TIENDA =====");

		for (Articulo articulo : articulos) {
			System.out.println(articulo);
		}
	}
}