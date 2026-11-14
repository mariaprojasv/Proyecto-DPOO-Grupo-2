package club;

import java.time.LocalDate;
import java.time.YearMonth;
import usuarios.Socio;

/** Comprobante de pago de una mensualidad de un socio. */
public final class PagoMensualidad {
    private final Socio socio;
    private final YearMonth periodo;
    private final LocalDate fechaPago;
    private final double valor;

    public PagoMensualidad(Socio socio, YearMonth periodo, LocalDate fechaPago, double valor) {
        if (socio == null || periodo == null || fechaPago == null || !Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException("Datos invalidos del pago de mensualidad");
        }
        this.socio = socio;
        this.periodo = periodo;
        this.fechaPago = fechaPago;
        this.valor = valor;
    }
    public Socio getSocio() { return socio; }
    public YearMonth getPeriodo() { return periodo; }
    public LocalDate getFechaPago() { return fechaPago; }
    public double getValor() { return valor; }
}
