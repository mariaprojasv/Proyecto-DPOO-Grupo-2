package pruebas;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import deportes.*;
import usuarios.Jugador;

class ExcepcionesCompetenciasTest {
    private CompetenciaLiga liga;
    private PartidoCompetencia partido;
    private Equipo equipo;
    private ArrayList<Jugador> jugadores;
    @BeforeEach void preparar() {
        Deporte futbol = new Deporte("Futbol", 7, 3, 1);
        Categoria mayores = new Categoria("Mayores", 18, 99);
        equipo = new Equipo("Leones", 10, futbol, mayores);
        liga = new CompetenciaLiga("Liga", "Club", "2026", 7, futbol, mayores);
        partido = new PartidoCompetencia(LocalDate.of(2026, 11, 2), futbol, equipo, "Rival", "Cancha");
        jugadores = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Jugador jugador = new Jugador("ex48_" + i, "clave", "Jugador " + i, 0, LocalDate.of(2000, 1, 1));
            assertTrue(equipo.agregarJugador(jugador, "Defensa", i + 1));
            jugadores.add(jugador);
        }
    }
    @Test void rechazaPartidoDeEquipoNoInscritoSinModificarLista() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> liga.registrarPartidoValidado(partido));
        assertTrue(error.getMessage().contains("no esta inscrito"));
        assertTrue(liga.getPartidos().isEmpty());
    }
    @Test void rechazaPartidoDuplicadoSinDuplicarRegistro() {
        assertTrue(liga.inscribirEquipo(equipo, jugadores));
        liga.registrarPartidoValidado(partido);
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> liga.registrarPartidoValidado(partido));
        assertTrue(error.getMessage().contains("ya fue registrado"));
        assertEquals(1, liga.getPartidos().size());
    }
    @Test void partidoRegistradoSinMarcadorNoModificaPosiciones() {
        assertTrue(liga.inscribirEquipo(equipo, jugadores));
        liga.registrarPartidoValidado(partido);
        assertTrue(liga.getTablaPosiciones().isEmpty());
    }
}
