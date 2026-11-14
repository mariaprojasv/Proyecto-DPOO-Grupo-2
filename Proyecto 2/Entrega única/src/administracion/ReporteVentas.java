package administracion;

import java.time.LocalDate;
import club.ClubDeportivoSeneca;
import Tienda.Venta;
import cafeteria.VentaCafeteria;
import club.PagoMensualidad;
import torneos.Torneo;
import torneos.TorneoCompetitivo;

public class ReporteVentas {
    private final int ventasTienda;
    private final int pagosMensualidades;
    private final double ingresosMensualidades;
    private final int ventasCafeteria;
    private final double ingresosTienda;
    private final double ingresosCafeteria;
    private final double ingresosTorneos;
    private final int ventasConBono;

    public ReporteVentas(ClubDeportivoSeneca club, LocalDate desde, LocalDate hasta) {
        if (club == null || desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El club y el rango de fechas son obligatorios");
        }
        int cantidadTienda = 0;
        int cantidadCafeteria = 0;
        double totalTienda = 0;
        double totalCafeteria = 0;
        for (Venta venta : club.getTienda().getVentas()) {
            if (venta.getFecha() != null && !venta.getFecha().isBefore(desde) && !venta.getFecha().isAfter(hasta)) {
                cantidadTienda++;
                totalTienda += venta.getTotal();
            }
        }
        for (VentaCafeteria venta : club.getCafeteria().getVentas()) {
            if (venta.getFecha() != null && !venta.getFecha().isBefore(desde) && !venta.getFecha().isAfter(hasta)) {
                cantidadCafeteria++;
                totalCafeteria += venta.getTotal();
            }
        }
        int pagos = 0;
        double totalMensualidades = 0;
        for (PagoMensualidad pago : club.getMensualidades()) {
            if (!pago.getFechaPago().isBefore(desde) && !pago.getFechaPago().isAfter(hasta)) {
                pagos++;
                totalMensualidades += pago.getValor();
            }
        }
        double torneos = 0;
        for (Torneo torneo : club.getGestionTorneos().getTorneos()) {
            if (torneo instanceof TorneoCompetitivo && !torneo.getFechaInicio().isBefore(desde)
                    && !torneo.getFechaInicio().isAfter(hasta)) {
                torneos += torneo.getTotalRecaudado();
            }
        }
        int conBono = 0;
        for (Venta venta : club.getTienda().getVentas()) {
            if (venta.getBonoAplicado() != null && !venta.getFecha().isBefore(desde) && !venta.getFecha().isAfter(hasta)) {
                conBono++;
            }
        }
        for (VentaCafeteria venta : club.getCafeteria().getVentas()) {
            if (venta.getBono() != null && !venta.getFecha().isBefore(desde) && !venta.getFecha().isAfter(hasta)) {
                conBono++;
            }
        }
        ingresosTorneos = torneos;
        ventasConBono = conBono;
        pagosMensualidades = pagos;
        ingresosMensualidades = totalMensualidades;
        ventasTienda = cantidadTienda;
        ventasCafeteria = cantidadCafeteria;
        ingresosTienda = totalTienda;
        ingresosCafeteria = totalCafeteria;
    }

    public int getPagosMensualidades() { return pagosMensualidades; }
    public double getIngresosMensualidades() { return ingresosMensualidades; }
    public double getIngresosPorRubroComercial() { return ingresosTienda + ingresosCafeteria; }
    public int getVentasTienda() { return ventasTienda; }
    public int getVentasCafeteria() { return ventasCafeteria; }
    public int getTotalVentas() { return ventasTienda + ventasCafeteria; }
    public double getIngresosTienda() { return ingresosTienda; }
    public double getIngresosCafeteria() { return ingresosCafeteria; }
    public double getIngresosTotales() { return ingresosTienda + ingresosCafeteria + ingresosMensualidades; }
    /** Inscripciones cobradas en torneos competitivos que inician en el rango. No se suma a los ingresos totales. */
    public double getIngresosTorneos() { return ingresosTorneos; }
    /** Ventas (tienda y cafeteria) pagadas con bono de torneo en el rango, desde la ejecucion actual. */
    public int getVentasConBono() { return ventasConBono; }
}
