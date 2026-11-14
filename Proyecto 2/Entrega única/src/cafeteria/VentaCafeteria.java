package cafeteria;

import java.time.LocalDate;
import usuarios.Usuario;
import torneos.BonoDescuento;

public class VentaCafeteria {
    private Usuario comprador;
    private Producto producto;
    private int cantidad;
    private LocalDate fecha;
    private double subtotal;
    private double impuesto;
    private double propina;
    private double total;
    private double porcentajeDescuento;
    private double descuento;
    private BonoDescuento bono;

    public VentaCafeteria(Usuario comprador, Producto producto, int cantidad, double propina) {
        this.comprador = comprador;
        this.producto = producto;
        this.cantidad = cantidad;
        this.fecha = LocalDate.now();
        this.subtotal = producto.getPrecio() * cantidad;
        this.impuesto = subtotal * 0.08;
        this.propina = propina;
        this.total = subtotal + impuesto + propina;
    }

    /** Aplica el bono de torneo (no acumulable). El bono se consume al registrar la venta en la cafeteria. */
    public void aplicarBono(BonoDescuento bono) {
        if (bono == null || !bono.estaDisponible()) {
            throw new IllegalArgumentException("El bono no es valido o ya fue utilizado");
        }
        if (bono.getTitular() != comprador) {
            throw new SecurityException("El bono pertenece a otro usuario");
        }
        if (this.bono != null || porcentajeDescuento > 0) {
            throw new IllegalStateException("El bono de torneo no se acumula con otros descuentos");
        }
        this.bono = bono;
        establecerPorcentajeDescuento(bono.getPorcentaje());
    }

    /** Restaura el descuento de una venta ya guardada. */
    public void establecerPorcentajeDescuento(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("Descuento invalido");
        }
        this.porcentajeDescuento = porcentaje;
        this.descuento = subtotal * porcentaje / 100.0;
        double base = subtotal - descuento;
        this.impuesto = base * 0.08;
        this.total = base + impuesto + propina;
    }

    /** Restaura el vinculo sin volver a consumir el bono. */
    public void restaurarBono(BonoDescuento bono) {
        if (bono == null || bono.getTitular() != comprador || bono.estaDisponible() || this.bono != null) {
            throw new IllegalArgumentException("Bono historico invalido");
        }
        this.bono = bono;
        establecerPorcentajeDescuento(bono.getPorcentaje());
    }

    public BonoDescuento getBono() { return bono; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public double getDescuento() { return descuento; }

    public void establecerFecha(LocalDate fecha) {
        if (fecha != null) {
            this.fecha = fecha;
        }
    }

    public Usuario getComprador() {
        return comprador;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public double getPropina() {
        return propina;
    }

    public double getTotal() {
        return total;
    }
}
