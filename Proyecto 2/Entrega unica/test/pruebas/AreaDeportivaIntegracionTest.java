package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import deportes.*;
import usuarios.Jugador;

/** Migracion de escenarios de PruebaDeportes: reglas entre equipos y partidos. */
class AreaDeportivaIntegracionTest {
    private AreaDeportiva area;
    private Deporte futbol, baloncesto;
    private Categoria adultos;
    private Equipo leones, tigres, aguilas;
    private Jugador ana;

    @BeforeEach
    void preparar() {
        area = new AreaDeportiva();
        futbol = new Deporte("Futbol", 2, 3, 1);
        baloncesto = new Deporte("Baloncesto", 2, 2, 0);
        adultos = new Categoria("Adultos", 18, 99);
        area.agregarDeporte(futbol);
        area.agregarDeporte(baloncesto);
        area.agregarCategoria(adultos);
        leones = area.crearEquipo("Leones", futbol, adultos, 4);
        tigres = area.crearEquipo("Tigres", futbol, adultos, 4);
        aguilas = area.crearEquipo("Aguilas", baloncesto, adultos, 4);
        ana = jugador("ana53");
    }

    private Jugador jugador(String login) {
        return new Jugador(login, "clave", login, 0, LocalDate.of(2000, 1, 1));
    }

    @Test
    void jugadorNoPuedePertenecerADosEquiposDelMismoDeporte() {
        assertTrue(area.inscribirJugadorEnEquipo(ana, leones, "Delantera", 1));
        assertFalse(area.inscribirJugadorEnEquipo(ana, tigres, "Defensa", 2));
        assertEquals(1, area.getEquiposDeJugador(ana).size());
        assertTrue(tigres.getJugadores().isEmpty());
    }

    @Test
    void jugadorPuedeInscribirseEnDeportesDiferentes() {
        assertTrue(area.inscribirJugadorEnEquipo(ana, leones, "Delantera", 1));
        assertTrue(area.inscribirJugadorEnEquipo(ana, aguilas, "Base", 2));
        assertEquals(2, area.getEquiposDeJugador(ana).size());
    }

    @Test
    void noProgramaPartidoSiFaltaMinimoDeJugadores() {
        assertTrue(area.inscribirJugadorEnEquipo(ana, leones, "Delantera", 1));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("dos53"), leones, "Defensa", 2));
        assertNull(area.programarPartidoInterno(LocalDate.of(2026, 11, 20), leones, tigres));
        assertTrue(area.getPartidos().isEmpty());
    }

    @Test
    void programaPartidoCuandoAmbosEquiposCumplenMinimo() {
        assertTrue(area.inscribirJugadorEnEquipo(ana, leones, "Delantera", 1));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("dos53"), leones, "Defensa", 2));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("tres53"), tigres, "Delantera", 3));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("cuatro53"), tigres, "Defensa", 4));
        PartidoInterno partido = area.programarPartidoInterno(LocalDate.of(2026, 11, 20), leones, tigres);
        assertNotNull(partido);
        assertEquals(1, area.getPartidos().size());
        assertSame(partido, area.getPartidos().get(0));
    }

    @Test
    void numeroDeCamisetaRepetidoNoCreaInscripcion() {
        assertTrue(area.inscribirJugadorEnEquipo(ana, leones, "Delantera", 7));
        assertFalse(area.inscribirJugadorEnEquipo(jugador("otro54"), leones, "Defensa", 7));
        assertEquals(1, leones.getJugadores().size());
        assertFalse(leones.numeroDisponible(7));
    }

    @Test
    void menorDeEdadNoPuedeIngresarACategoriaAdultos() {
        Jugador menor = new Jugador("menor54", "clave", "Menor", 0, LocalDate.of(2015, 3, 3));
        assertFalse(area.inscribirJugadorEnEquipo(menor, leones, "Defensa", 8));
        assertTrue(leones.getJugadores().isEmpty());
    }

    @Test
    void partidoEntreDeportesDistintosNoSeProgramaAunqueHayaJugadores() {
        assertTrue(area.inscribirJugadorEnEquipo(ana, leones, "Delantera", 1));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("dos54"), leones, "Defensa", 2));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("tres54"), aguilas, "Base", 3));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("cuatro54"), aguilas, "Alero", 4));
        assertNull(area.programarPartidoInterno(LocalDate.of(2026, 11, 21), leones, aguilas));
        assertTrue(area.getPartidos().isEmpty());
    }
    @Test
    void suspensionReduceJugadoresHabilitadosYBloqueaPartido() {
        ControlSanciones sanciones = new ControlSanciones();
        area.establecerControlSanciones(sanciones);
        assertTrue(area.inscribirJugadorEnEquipo(ana, leones, "Delantera", 1));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("dos56"), leones, "Defensa", 2));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("tres56"), tigres, "Delantera", 3));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("cuatro56"), tigres, "Defensa", 4));
        assertTrue(sanciones.registrarTarjeta(new Tarjeta(ana, "ROJA")));
        assertNull(area.programarPartidoInterno(LocalDate.of(2026, 11, 25), leones, tigres));
        assertTrue(area.getPartidos().isEmpty());
        sanciones.cumplirSuspension(ana);
        assertNotNull(area.programarPartidoInterno(LocalDate.of(2026, 11, 25), leones, tigres));
    }

    @Test
    void noPermitePartidoDelMismoEquipoContraSiMismo() {
        assertTrue(area.inscribirJugadorEnEquipo(ana, leones, "Delantera", 1));
        assertTrue(area.inscribirJugadorEnEquipo(jugador("dosmismo56"), leones, "Defensa", 2));
        assertNull(area.programarPartidoInterno(LocalDate.of(2026, 11, 25), leones, leones));
    }
}
