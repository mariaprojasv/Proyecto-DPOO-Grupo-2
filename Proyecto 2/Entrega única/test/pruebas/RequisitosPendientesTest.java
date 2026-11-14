package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import club.ClubDeportivoSeneca;
import persistencia.PersistenciaArchivos;
import administracion.ReporteVentas;
import deportes.Deporte;
import reservas.Instalacion;
import reservas.Reserva;
import cafeteria.bebida;
import usuarios.Socio;

class RequisitosPendientesTest {
    @TempDir Path temporal;

    @Test void mensualidadSeRegistraUnaVezYSeReportaPorRubro() {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Seneca");
        Socio socio = new Socio("s1", "clave", "Socio", 0);
        club.registrarUsuario(socio);
        club.registrarMensualidad(socio, YearMonth.of(2026, 11), LocalDate.of(2026, 11, 12), 95000);
        assertThrows(IllegalStateException.class, () -> club.registrarMensualidad(socio,
                YearMonth.of(2026, 11), LocalDate.of(2026, 11, 13), 95000));
        ReporteVentas reporte = new ReporteVentas(club, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));
        assertEquals(1, reporte.getPagosMensualidades());
        assertEquals(95000, reporte.getIngresosMensualidades(), 0.01);
        assertEquals(95000, reporte.getIngresosTotales(), 0.01);
    }

    @Test void mensualidadesPersistenSinDuplicarse() throws Exception {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Seneca");
        Socio socio = new Socio("s2", "clave", "Socio", 0);
        club.registrarUsuario(socio);
        club.registrarMensualidad(socio, YearMonth.of(2026, 10), LocalDate.of(2026, 10, 5), 80000);
        PersistenciaArchivos archivos = new PersistenciaArchivos(temporal.toString());
        archivos.guardar(club);
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(recuperado);
        assertEquals(1, recuperado.getMensualidades().size());
        assertEquals(80000, recuperado.getMensualidades().get(0).getValor(), 0.01);
    }

    @Test void despachoCalienteRechazadoEnMesaTechadaSinDescontarStock() {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Seneca");
        Socio socio = new Socio("s3", "clave", "Socio", 0);
        club.registrarUsuario(socio);
        Deporte deporte = new Deporte("Tenis de mesa", 2, 3, 1);
        Instalacion mesa = new Instalacion("Mesa cubierta", deporte, 2, Set.of("individual"));
        mesa.establecerTechada(true);
        club.getGestionReservas().agregarInstalacion(mesa);
        Reserva reserva = club.getGestionReservas().realizarReservaValidada(socio, mesa,
                LocalDate.of(2026, 11, 13), LocalTime.of(10, 0), LocalTime.of(11, 0), 2, "individual");
        club.getCafeteria().agregarProducto(new bebida("Cafe", 2000, 3, true));
        assertThrows(IllegalStateException.class, () -> club.getCafeteria().despacharEnInstalacion(
                socio, "Cafe", 1, 0, reserva, club.getGestionReservas()));
        assertEquals(3, club.getCafeteria().buscarProducto("Cafe").getCantidad());
        mesa.establecerTechada(false);
        assertNotNull(club.getCafeteria().despacharEnInstalacion(socio, "Cafe", 1, 0,
                reserva, club.getGestionReservas()));
        assertEquals(2, club.getCafeteria().buscarProducto("Cafe").getCantidad());
    }
}
