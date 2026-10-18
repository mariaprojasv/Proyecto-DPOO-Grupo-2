package Tienda;

import java.time.LocalDate;
import java.util.ArrayList;

import usuarios.Usuario;

public class Venta {

	private LocalDate fecha;
	private Usuario comprador;
	private ArrayList<DetalleVenta> detalles;

	private double subtotal;
	private double descuento;
	private double porcentajeDescuento;
	private double impuestos;
	private double total;
	private int puntosGenerados;

	public Venta(Usuario comprador) {
		this.fecha = LocalDate.now();
		this.comprador = comprador;
		this.detalles = new ArrayList<>();

		this.subtotal = 0;
		this.descuento = 0;
		this.porcentajeDescuento = 0;
		this.impuestos = 0;
		this.total = 0;
		this.puntosGenerados = 0;
	}

	public void establecerFecha(LocalDate fecha) {
		if (fecha != null) {
			this.fecha = fecha;
		}
	}

	public void agregarDetalleGuardado(Articulo articulo, int cantidad, double precioUnitario) {
		if (articulo != null && cantidad > 0 && precioUnitario >= 0) {
			detalles.add(new DetalleVenta(articulo, cantidad, precioUnitario));
			calcularSubtotal();
		}
	}

	public void agregarDetalle(Articulo articulo, int cantidad) {

		if (articulo == null || cantidad <= 0) {
			return;
		}

		if (cantidad > articulo.getCantidad()) {
			return;
		}

		articulo.disminuirCantidad(cantidad);

		DetalleVenta detalle = new DetalleVenta(articulo, cantidad);
		detalles.add(detalle);

		calcularSubtotal();
	}

	public void calcularSubtotal() {

		subtotal = 0;

		for (DetalleVenta detalle : detalles) {
			subtotal += detalle.calcularSubtotal();
		}
	}

	public void aplicarDescuento(double porcentaje) {

		if (porcentaje < 0) {
			porcentaje = 0;
		}

		if (porcentaje > 100) {
			porcentaje = 100;
		}

		porcentajeDescuento = porcentaje;
		descuento = subtotal * porcentajeDescuento / 100.0;
	}

	public void calcularTotal() {

		calcularSubtotal();
		descuento = subtotal * porcentajeDescuento / 100.0;

		double valorConDescuento = subtotal - descuento;

		impuestos = valorConDescuento * 0.19;

		total = valorConDescuento + impuestos;

		puntosGenerados = (int) (total * 0.02);
	}

	public LocalDate getFecha() {
	return fecha;
	}

	public Usuario getComprador() {
	return comprador;
	}

	public ArrayList<DetalleVenta> getDetalles() {
	return detalles;
	}

	public double getSubtotal() {
	return subtotal;
	}

	public double getDescuento() {
	return descuento;
	}

	public double getImpuestos() {
	return impuestos;
	}

	public double getTotal() {
	return total;
	}

	public int getPuntosGenerados() {
	return puntosGenerados;
	}

	@Override
	public String toString() {

		String resultado = "";

		resultado += "===== VENTA =====\n";
		resultado += "Fecha: " + fecha + "\n";
		resultado += "Comprador: " + comprador.getNombre() + "\n";

		for (DetalleVenta detalle : detalles) {
			resultado += detalle + "\n";
		}

		resultado += "Subtotal: $" + subtotal + "\n";
		resultado += "Descuento: $" + descuento + "\n";
		resultado += "Impuestos: $" + impuestos + "\n";
		resultado += "Total: $" + total + "\n";
		resultado += "Puntos generados: " + puntosGenerados + "\n";

		return resultado;
	}
}