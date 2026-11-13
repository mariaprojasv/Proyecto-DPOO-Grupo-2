package administracion;

import java.time.LocalDate;
import club.ClubDeportivoSeneca;
import Tienda.Venta;
import cafeteria.VentaCafeteria;
import club.PagoMensualidad;

public class ReporteVentas {
    private final int ventasTienda;
    private final int pagosMensualidades;
    private final double ingresosMensualidades;
    private final int ventasCafeteria;
    private final double ingresosTienda;
    private final double ingresosCafeteria;

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
}
