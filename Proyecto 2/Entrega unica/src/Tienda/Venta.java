package Tienda;

import java.time.LocalDate;
import java.util.ArrayList;

import usuarios.Usuario;
import usuarios.Socio;

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
	private boolean puntosAcreditados;
    private int puntosRedimidos;
    private CodigoDescuento codigoAplicado;

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
		this.puntosAcreditados = false;
        this.puntosRedimidos = 0;
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
		if (puntosAcreditados) {
			throw new IllegalStateException("No se puede modificar una venta finalizada");
		}

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

    public void aplicarCodigo(CodigoDescuento codigo) {
        if (puntosAcreditados) {
            throw new IllegalStateException("No se puede modificar una venta finalizada");
        }
        if (codigo == null || !codigo.estaDisponible()) {
            throw new IllegalArgumentException("El codigo de descuento no es valido o ya fue utilizado");
        }
        if (codigoAplicado != null) {
            throw new IllegalStateException("Solo se permite un codigo por venta");
        }
        codigoAplicado = codigo;
        porcentajeDescuento = codigo.getPorcentaje();
        calcularTotal();
    }

    public void redimirPuntosEnVenta(int puntos) {
        if (puntosAcreditados) {
            throw new IllegalStateException("No se puede modificar una venta finalizada");
        }
        if (!(comprador instanceof Socio)) {
            throw new SecurityException("Solo un socio puede redimir puntos");
        }
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos deben ser positivos");
        }
        if (puntosRedimidos != 0) {
            throw new IllegalStateException("Esta venta ya tiene una redencion de puntos");
        }
        calcularTotal();
        if (puntos > total) {
            throw new IllegalArgumentException("Los puntos exceden el valor de la compra");
        }
        if (puntos > ((Socio) comprador).getPuntosFidelidad()) {
            throw new IllegalStateException("El socio no tiene puntos suficientes");
        }
        puntosRedimidos = puntos;
        calcularTotal();
    }

    public void restaurarDescuento(double porcentaje) {
        if (puntosAcreditados || porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("Descuento historico invalido");
        }
        porcentajeDescuento = porcentaje;
        calcularTotal();
    }

	public void calcularTotal() {

		calcularSubtotal();
		descuento = subtotal * porcentajeDescuento / 100.0;

		double valorConDescuento = subtotal - descuento;

		impuestos = valorConDescuento * 0.19;

		total = Math.max(0, valorConDescuento + impuestos - puntosRedimidos);

		puntosGenerados = (int) (total * 0.02);
	}

    public void finalizarVenta() {
        if (puntosAcreditados) {
            throw new IllegalStateException("La venta ya fue finalizada");
        }
        if (detalles.isEmpty()) {
            throw new IllegalStateException("No se puede finalizar una venta sin productos");
        }
        calcularTotal();
        if (codigoAplicado != null && !codigoAplicado.estaDisponible()) {
            throw new IllegalStateException("El codigo ya fue utilizado");
        }
        if (puntosRedimidos > 0) {
            ((Socio) comprador).redimirPuntos(puntosRedimidos);
        }
        if (codigoAplicado != null) {
            codigoAplicado.marcarUtilizado();
        }
        if (comprador instanceof Socio && puntosGenerados > 0) {
            ((Socio) comprador).acreditarPuntos(puntosGenerados);
        }
        puntosAcreditados = true;
    }

    public int getPuntosRedimidos() {
        return puntosRedimidos;
    }

    public boolean isFinalizada() {
        return puntosAcreditados;
    }

    public void marcarComoRestaurada() {
        if (puntosAcreditados) {
            throw new IllegalStateException("La venta ya esta finalizada");
        }
        calcularTotal();
        puntosAcreditados = true;
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
        resultado += "Puntos redimidos: " + puntosRedimidos + "\n";
		resultado += "Impuestos: $" + impuestos + "\n";
		resultado += "Total: $" + total + "\n";
		resultado += "Puntos generados: " + puntosGenerados + "\n";

		return resultado;
	}
}