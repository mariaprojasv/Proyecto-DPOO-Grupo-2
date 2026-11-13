package cafeteria;

import java.time.LocalDate;
import usuarios.Usuario;

public class VentaCafeteria {
    private Usuario comprador;
    private Producto producto;
    private int cantidad;
    private LocalDate fecha;
    private double subtotal;
    private double impuesto;
    private double propina;
    private double total;

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
