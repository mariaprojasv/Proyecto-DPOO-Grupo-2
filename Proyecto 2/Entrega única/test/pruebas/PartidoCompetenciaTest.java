package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import deportes.*;

@DisplayName("Partido de competencia: marcadores y transiciones")
class PartidoCompetenciaTest {
    private PartidoCompetencia partido;

    @BeforeEach
    void setUp() {
        // GIVEN: partido de futbol sin resultado confirmado.
        Deporte futbol = new Deporte("Futbol", 1, 3, 1);
        Categoria adultos = new Categoria("Adultos", 18, 99);
        Equipo equipo = new Equipo("Leones", 5, futbol, adultos);
        partido = new PartidoCompetencia(LocalDate.of(2026, 11, 1), futbol, equipo, "Rival", "Cancha");
    }

    @Test
    void partidoNuevoEstaPendiente() {
        // WHEN: consulto un partido que aun no tiene marcador.
        // THEN: el 0-0 inicial no se confunde con un empate confirmado.
        assertFalse(partido.tieneResultadoRegistrado());
        assertEquals("PENDIENTE", partido.getResultado());
    }

    @ParameterizedTest(name = "marcador {0}-{1}: {2}")
    @CsvSource({"2, 1, VICTORIA", "1, 1, EMPATE", "0, 2, DERROTA", "0, 0, EMPATE"})
    void clasificaMarcadoresValidos(int local, int visitante, String esperado) {
        // WHEN: registro el marcador.
        assertTrue(partido.registrarMarcador(local, visitante));
        // THEN: queda confirmado y clasificado correctamente.
        assertTrue(partido.tieneResultadoRegistrado());
        assertEquals(esperado, partido.getResultado());
        assertEquals(local, partido.getMarcadorLocal());
        assertEquals(visitante, partido.getMarcadorVisitante());
    }

    @ParameterizedTest(name = "marcador invalido {0}-{1}")
    @CsvSource({"-1, 0", "0, -1", "-1, -1"})
    void rechazaMarcadoresNegativos(int local, int visitante) {
        // WHEN: intento registrar un marcador fuera de rango.
        assertFalse(partido.registrarMarcador(local, visitante));
        // THEN: permanece pendiente y sin cambios.
        assertFalse(partido.tieneResultadoRegistrado());
        assertEquals("PENDIENTE", partido.getResultado());
        assertEquals(0, partido.getMarcadorLocal());
        assertEquals(0, partido.getMarcadorVisitante());
    }

    @Nested
    @DisplayName("Transiciones de estado")
    class CorreccionMarcador {
        @Test
        void marcadorInvalidoNoBorraResultadoAnterior() {
            // GIVEN: victoria confirmada.
            assertTrue(partido.registrarMarcador(3, 1));
            // WHEN: intento corregirla con goles negativos.
            assertFalse(partido.registrarMarcador(-1, 1));
            // THEN: se conserva el ultimo resultado valido.
            assertTrue(partido.tieneResultadoRegistrado());
            assertEquals("VICTORIA", partido.getResultado());
            assertEquals(3, partido.getMarcadorLocal());
            assertEquals(1, partido.getMarcadorVisitante());
        }

        @Test
        void marcadorValidoReemplazaResultadoAnterior() {
            // GIVEN: victoria confirmada.
            assertTrue(partido.registrarMarcador(3, 1));
            // WHEN: corrijo a derrota.
            assertTrue(partido.registrarMarcador(0, 2));
            // THEN: solo queda el nuevo resultado.
            assertEquals("DERROTA", partido.getResultado());
            assertEquals(0, partido.getMarcadorLocal());
            assertEquals(2, partido.getMarcadorVisitante());
        }
    }

    @Test
    void baloncestoNoAdmiteEmpate() {
        // GIVEN: partido de baloncesto.
        Deporte baloncesto = new Deporte("Baloncesto", 1, 3, 1);
        Equipo equipo = new Equipo("Halcones", 5, baloncesto, new Categoria("Adultos", 18, 99));
        PartidoCompetencia basket = new PartidoCompetencia(LocalDate.of(2026, 11, 1), baloncesto, equipo, "Rival", "Coliseo");
        // WHEN: intento confirmar empate.
        assertFalse(basket.registrarMarcador(10, 10));
        // THEN: sigue pendiente.
        assertEquals("PENDIENTE", basket.getResultado());
        assertFalse(basket.tieneResultadoRegistrado());
    }
}
