package Tienda;

import java.time.LocalDate;
import java.util.ArrayList;

import usuarios.Usuario;
import usuarios.Socio;
import torneos.BonoDescuento;

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
    private boolean cancelada;
    private int puntosRedimidos;
    private CodigoDescuento codigoAplicado;
    private BonoDescuento bonoAplicado;

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
        if (cancelada) throw new IllegalStateException("Venta cancelada");
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
        if (bonoAplicado != null) {
            throw new IllegalStateException("El bono de torneo no se acumula con otros descuentos");
        }
        codigoAplicado = codigo;
        porcentajeDescuento = codigo.getPorcentaje();
        calcularTotal();
    }

    /** Aplica un bono ganado en un torneo recreativo; no se acumula con codigos ni con puntos. */
    public void aplicarBono(BonoDescuento bono) {
        if (puntosAcreditados) {
            throw new IllegalStateException("No se puede modificar una venta finalizada");
        }
        if (bono == null || !bono.estaDisponible()) {
            throw new IllegalArgumentException("El bono no es valido o ya fue utilizado");
        }
        if (bono.getTitular() != comprador) {
            throw new SecurityException("El bono pertenece a otro usuario");
        }
        if (bonoAplicado != null || codigoAplicado != null || puntosRedimidos != 0 || porcentajeDescuento > 0) {
            throw new IllegalStateException("El bono de torneo no se acumula con otros descuentos");
        }
        bonoAplicado = bono;
        porcentajeDescuento = bono.getPorcentaje();
        calcularTotal();
    }

    /** Vincula un bono ya utilizado a una venta historica sin consumirlo nuevamente. */
    public void restaurarBono(BonoDescuento bono) {
        if (puntosAcreditados || bono == null || bono.getTitular() != comprador || bono.estaDisponible()
                || codigoAplicado != null || puntosRedimidos != 0 || bonoAplicado != null) {
            throw new IllegalArgumentException("Bono historico invalido");
        }
        bonoAplicado = bono;
        porcentajeDescuento = bono.getPorcentaje();
        calcularTotal();
    }

    public BonoDescuento getBonoAplicado() {
        return bonoAplicado;
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
        if (bonoAplicado != null) {
            throw new IllegalStateException("El bono de torneo no se acumula con otros descuentos");
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
        if (cancelada) throw new IllegalStateException("Venta cancelada");
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
        if (bonoAplicado != null && !bonoAplicado.estaDisponible()) {
            throw new IllegalStateException("El bono ya fue utilizado");
        }
        if (puntosRedimidos > 0) {
            ((Socio) comprador).redimirPuntos(puntosRedimidos);
        }
        if (codigoAplicado != null) {
            codigoAplicado.marcarUtilizado();
        }
        if (bonoAplicado != null) {
            bonoAplicado.marcarUsado();
        }
        if (comprador instanceof Socio && puntosGenerados > 0) {
            ((Socio) comprador).acreditarPuntos(puntosGenerados);
        }
        puntosAcreditados = true;
    }

    /** Reintegra existencias reservadas por una compra abandonada, solo una vez. */
    public void cancelarVentaPendiente() {
        if (cancelada || puntosAcreditados) {
            throw new IllegalStateException("La venta no se puede cancelar");
        }
        for (DetalleVenta detalle : detalles) {
            detalle.getArticulo().aumentarCantidad(detalle.getCantidad());
        }
        detalles.clear();
        calcularTotal();
        cancelada = true;
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