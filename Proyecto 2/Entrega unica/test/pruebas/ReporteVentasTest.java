package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import administracion.ReporteVentas;
import club.ClubDeportivoSeneca;
import cafeteria.bebida;
import usuarios.Socio;

@DisplayName("Reportes de ventas: totales, filtros y validaciones")
class ReporteVentasTest {
    private ClubDeportivoSeneca club;
    private LocalDate hoy;
    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Seneca");
        hoy = LocalDate.now();
    }
    @Test void clubSinVentasProduceTotalesCero() {
        ReporteVentas reporte = new ReporteVentas(club, hoy, hoy);
        assertAll(() -> assertEquals(0, reporte.getTotalVentas()),
                  () -> assertEquals(0, reporte.getIngresosTotales(), 0.001));
    }
    @Test void ventaDeCafeteriaSeIncluyeEnTotal() {
        club.getCafeteria().agregarProducto(new bebida("Jugo", 1000, 4, false));
        club.getCafeteria().registrarVentaValidada(new Socio("socio", "clave", "Socio", 0), "Jugo", 2, 100);
        ReporteVentas reporte = new ReporteVentas(club, hoy, hoy);
        assertAll(() -> assertEquals(1, reporte.getVentasCafeteria()),
                  () -> assertEquals(0, reporte.getVentasTienda()),
                  () -> assertEquals(1, reporte.getTotalVentas()),
                  () -> assertEquals(2260, reporte.getIngresosCafeteria(), 0.001),
                  () -> assertEquals(2260, reporte.getIngresosTotales(), 0.001));
    }
    @Test void ventasFueraDelRangoNoSeIncluyen() {
        club.getCafeteria().agregarProducto(new bebida("Jugo", 1000, 4, false));
        club.getCafeteria().registrarVentaValidada(new Socio("socio", "clave", "Socio", 0), "Jugo", 2, 100);
        ReporteVentas reporte = new ReporteVentas(club, hoy.minusDays(2), hoy.minusDays(1));
        assertEquals(0, reporte.getTotalVentas());
        assertEquals(0, reporte.getIngresosTotales(), 0.001);
        assertEquals(1, club.getCafeteria().getVentas().size());
    }
    @Test void rangoInvertidoEsInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new ReporteVentas(club, hoy, hoy.minusDays(1)));
    }
    @Test void clubNuloEsInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new ReporteVentas(null, hoy, hoy));
    }
}
