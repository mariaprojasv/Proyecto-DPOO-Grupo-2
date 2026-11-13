package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import Tienda.Articulo;
import Tienda.CodigoDescuento;
import Tienda.Venta;
import deportes.Categoria;
import deportes.CompetenciaLiga;
import deportes.Deporte;
import deportes.Equipo;
import deportes.PartidoCompetencia;
import usuarios.Jugador;
import usuarios.Socio;

@DisplayName("Regresion de PruebaFinal: venta y competencia")
class FlujoFinalClubTest {
    @Test void descuentoSeRecalculaSiSeAgregaOtroArticuloDespuesDelCodigo() {
        Articulo camiseta = new Articulo("Camiseta", "Ropa", 10000, 10);
        Venta venta = new Venta(new Socio("socio51", "clave", "Socio", 0));
        venta.agregarDetalle(camiseta, 1);
        venta.aplicarCodigo(new CodigoDescuento("FINAL10", 10));
        venta.agregarDetalle(camiseta, 1);
        venta.calcularTotal();
        assertAll(
                () -> assertEquals(20000, venta.getSubtotal(), 0.0001),
                () -> assertEquals(2000, venta.getDescuento(), 0.0001),
                () -> assertEquals(8, camiseta.getCantidad()));
    }

    @Test void reRegistrarElMismoPartidoNoDuplicaLaTablaDeLiga() {
        Deporte futbol = new Deporte("Futbol", 7, 3, 1);
        Categoria mayores = new Categoria("Mayores", 18, 99);
        Equipo leones = new Equipo("Leones", 18, futbol, mayores);
        java.util.ArrayList<Jugador> nomina = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Jugador jugador = new Jugador("r51_" + i, "clave", "Jugador " + i, 0,
                    LocalDate.of(2000, 1, 1));
            assertTrue(leones.agregarJugador(jugador, "Defensa", i + 1));
            nomina.add(jugador);
        }
        CompetenciaLiga liga = new CompetenciaLiga("Liga", "Club", "2026", 7, futbol, mayores);
        assertTrue(liga.inscribirEquipo(leones, nomina));
        PartidoCompetencia partido = new PartidoCompetencia(LocalDate.of(2026, 11, 12),
                futbol, leones, "Rival", "Cancha");
        assertTrue(partido.registrarMarcador(2, 1));
        liga.registrarPartido(partido);
        liga.registrarPartido(partido);
        assertAll(
                () -> assertEquals(1, liga.getPartidos().size()),
                () -> assertEquals(1, liga.getTablaPosiciones().size()),
                () -> assertEquals(1, liga.getTablaPosiciones().get(0).getPartidosJugados()),
                () -> assertEquals(3, liga.getTablaPosiciones().get(0).getPuntos()));
    }

    @Test void marcadorInvalidoTrasUnoValidoNoAlteraResultado() {
        Deporte futbol = new Deporte("Futbol", 7, 3, 1);
        Equipo leones = new Equipo("Leones", 18, futbol, new Categoria("Mayores", 18, 99));
        PartidoCompetencia partido = new PartidoCompetencia(LocalDate.of(2026, 11, 12),
                futbol, leones, "Rival", "Cancha");
        assertTrue(partido.registrarMarcador(2, 1));
        assertFalse(partido.registrarMarcador(-1, 3));
        assertAll(
                () -> assertEquals(2, partido.getMarcadorLocal()),
                () -> assertEquals(1, partido.getMarcadorVisitante()),
                () -> assertEquals("VICTORIA", partido.getResultado()));
    }
}
