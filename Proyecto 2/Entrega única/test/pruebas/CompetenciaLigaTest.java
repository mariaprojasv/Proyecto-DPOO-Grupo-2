package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import deportes.*;
import usuarios.Jugador;

@DisplayName("Liga: resultados, posiciones y encapsulacion")
class CompetenciaLigaTest {
    private Deporte deporte;
    private Categoria categoria;
    private Equipo equipo;
    private CompetenciaLiga liga;
    private PartidoCompetencia partido;

    @BeforeEach
    void setUp() {
        // GIVEN: liga nueva, equipo inscrito y partido sin marcador.
        deporte = new Deporte("Futbol", 1, 3, 1);
        categoria = new Categoria("Adultos", 18, 99);
        equipo = new Equipo("Leones", 5, deporte, categoria);
        Jugador jugador = new Jugador("junit34", "1234", "Jugador", 0, LocalDate.of(2000, 1, 1));
        equipo.agregarJugadorValidado(jugador, "Jugador", 1);
        liga = new CompetenciaLiga("Liga", "Club", "2026", 1, deporte, categoria);
        ArrayList<Jugador> nomina = new ArrayList<>();
        nomina.add(jugador);
        assertTrue(liga.inscribirEquipo(equipo, nomina));
        partido = new PartidoCompetencia(LocalDate.of(2026, 11, 1), deporte, equipo, "Rival", "Cancha");
        liga.registrarPartidoValidado(partido);
    }

    @Test
    @DisplayName("Un partido pendiente no suma puntos")
    void partidoPendienteNoCuenta() {
        // WHEN: consulto la tabla sin registrar marcador.
        ArrayList<PosicionTabla> tabla = liga.getTablaPosiciones();
        // THEN: no hay posiciones por resultados confirmados.
        assertTrue(tabla.isEmpty());
    }

    @ParameterizedTest(name = "marcador {0}-{1}: {2} puntos")
    @CsvSource({"2, 1, 3", "1, 1, 1", "0, 2, 0"})
    void puntosSegunResultado(int golesClub, int golesRival, int puntosEsperados) {
        // WHEN: confirmo un marcador de victoria, empate o derrota.
        assertTrue(partido.registrarMarcador(golesClub, golesRival));
        // THEN: la tabla refleja los puntos de la regla deportiva.
        assertEquals(puntosEsperados, liga.getTablaPosiciones().get(0).getPuntos());
        assertEquals(1, liga.getTablaPosiciones().get(0).getPartidosJugados());
    }

    @Nested
    @DisplayName("Transiciones de estado del marcador")
    class CorreccionDeResultados {
        @Test
        void corregirVictoriaAEmpateNoDuplicaPartidos() {
            // GIVEN: una victoria registrada.
            assertTrue(partido.registrarMarcador(2, 1));
            assertEquals(3, liga.getTablaPosiciones().get(0).getPuntos());
            // WHEN: corrijo el marcador a empate.
            assertTrue(partido.registrarMarcador(1, 1));
            // THEN: solo cuenta el resultado actualizado.
            PosicionTabla fila = liga.getTablaPosiciones().get(0);
            assertEquals(1, fila.getPuntos());
            assertEquals(1, fila.getPartidosJugados());
            assertEquals(0, fila.getGanados());
            assertEquals(1, fila.getEmpatados());
        }
    }

    @Test
    void limpiarListaExternaNoBorraPartidos() {
        // WHEN: modifico la lista obtenida del getter.
        liga.getPartidos().clear();
        // THEN: el partido registrado permanece en la competencia.
        assertEquals(1, liga.getPartidos().size());
    }

    @Test
    void modificarFilaExternaNoCambiaTablaInterna() {
        // GIVEN: una victoria confirmada.
        assertTrue(partido.registrarMarcador(2, 1));
        // WHEN: altero la copia de una fila.
        liga.getTablaPosiciones().get(0).registrarResultado("VICTORIA", 3);
        // THEN: la tabla interna no cambia.
        assertEquals(3, liga.getTablaPosiciones().get(0).getPuntos());
        assertEquals(1, liga.getTablaPosiciones().get(0).getPartidosJugados());
    }
}
