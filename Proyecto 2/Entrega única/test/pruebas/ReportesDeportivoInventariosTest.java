package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import administracion.*;
import club.ClubDeportivoSeneca;
import Tienda.Articulo;
import Tienda.tienda;
import usuarios.Administrador;

@DisplayName("Reportes deportivos e inventarios")
class ReportesDeportivoInventariosTest {
    private ClubDeportivoSeneca club;
    private InterfazAdministracion interfaz;
    private LocalDate hoy;
    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Seneca");
        interfaz = new InterfazAdministracion(club);
        hoy = LocalDate.now();
    }
    @Test void reporteDeportivoVacio() {
        ReporteDeportivo reporte = new ReporteDeportivo(club, hoy, hoy);
        assertAll(() -> assertEquals(0, reporte.getEquipos()),
                  () -> assertEquals(0, reporte.getCompetencias()),
                  () -> assertEquals(0, reporte.getPartidos()),
                  () -> assertEquals(0, reporte.getPendientes()));
    }
    @Test void rangoDeportivoInvertidoEsInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new ReporteDeportivo(club, hoy, hoy.minusDays(1)));
    }
    @Test void inventariosDeDosUbicacionesSeSuman() {
        club.getTienda().agregarArticulo(new Articulo("Balon", "Deporte", 100, 3));
        tienda norte = new tienda();
        norte.agregarArticulo(new Articulo("Camiseta", "Ropa", 50, 2));
        club.getGestionInventarios().registrarTienda("Norte", norte);
        ReporteInventarios reporte = new ReporteInventarios(club);
        assertAll(() -> assertEquals(2, reporte.getUbicaciones()),
                  () -> assertEquals(2, reporte.getReferencias()),
                  () -> assertEquals(5, reporte.getUnidades()),
                  () -> assertEquals(400, reporte.getValorExistencias(), 0.001),
                  () -> assertEquals(3, club.getTienda().getArticulos().get(0).getCantidad()));
    }
    @Test void consultarReportesSinSesionEstaProhibido() {
        assertThrows(SecurityException.class, () -> interfaz.consultarReporteInventarios());
        assertThrows(SecurityException.class, () -> interfaz.consultarReporteDeportivo(hoy, hoy));
    }
    @Test void administradorPuedeConsultarYAlCerrarSeRevocaAcceso() {
        assertTrue(club.registrarUsuario(new Administrador("admin", "clave", "Admin")));
        interfaz.iniciarSesion("admin", "clave");
        assertEquals(0, interfaz.consultarReporteInventarios().getUnidades());
        assertEquals(0, interfaz.consultarReporteDeportivo(hoy, hoy).getPartidos());
        interfaz.cerrarSesion();
        assertThrows(SecurityException.class, () -> interfaz.consultarReporteInventarios());
    }
    @Test void reporteInventarioNoAceptaClubNulo() {
        assertThrows(IllegalArgumentException.class, () -> new ReporteInventarios(null));
    }
}
