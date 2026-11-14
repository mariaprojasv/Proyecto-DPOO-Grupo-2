package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import deportes.Categoria;
import deportes.Deporte;
import deportes.Equipo;
import usuarios.Jugador;

@DisplayName("Inscripciones de equipo: reglas y fronteras")
class EquipoTest {
    private Equipo equipo;
    private Jugador jugador;

    @BeforeEach
    void setUp() {
        // GIVEN: equipo con dos cupos y categoria amplia.
        equipo = new Equipo("Equipo R38", 2, new Deporte("Futbol", 1, 3, 1), new Categoria("Adultos", 18, 80));
        jugador = new Jugador("j38", "1234", "Jugador", 0, LocalDate.of(2000, 1, 1));
    }

    @Test
    void inscripcionValidaGuardaJugadorYCamiseta() {
        equipo.agregarJugadorValidado(jugador, "Delantero", 10);
        assertEquals(1, equipo.getInscripciones().size());
        assertSame(jugador, equipo.getInscripciones().get(0).getJugador());
        assertEquals(10, equipo.getInscripciones().get(0).getNumeroCamiseta());
    }

    @ParameterizedTest(name = "camiseta {0}: valido={1}")
    @CsvSource({"-1, false", "0, false", "1, true", "99, true"})
    void bordesNumeroCamiseta(int numero, boolean valido) {
        if (valido) {
            assertDoesNotThrow(() -> equipo.agregarJugadorValidado(jugador, "Defensa", numero));
        } else {
            assertThrows(IllegalArgumentException.class,
                () -> equipo.agregarJugadorValidado(jugador, "Defensa", numero));
        }
    }

    @Test
    void jugadorNuloSeRechaza() {
        assertThrows(IllegalArgumentException.class,
            () -> equipo.agregarJugadorValidado(null, "Defensa", 2));
    }

    @Test
    void posicionVaciaSeRechaza() {
        assertThrows(IllegalArgumentException.class,
            () -> equipo.agregarJugadorValidado(jugador, "  ", 2));
    }

    @Test
    void jugadorDuplicadoSeRechaza() {
        equipo.agregarJugadorValidado(jugador, "Defensa", 2);
        assertThrows(IllegalStateException.class,
            () -> equipo.agregarJugadorValidado(jugador, "Ataque", 3));
    }

    @Test
    void camisetaOcupadaSeRechaza() {
        equipo.agregarJugadorValidado(jugador, "Defensa", 2);
        Jugador otro = new Jugador("otro38", "1234", "Otro", 0, LocalDate.of(2001, 1, 1));
        assertThrows(IllegalStateException.class,
            () -> equipo.agregarJugadorValidado(otro, "Ataque", 2));
    }

    @Test
    void equipoLlenoSeRechaza() {
        equipo.agregarJugadorValidado(jugador, "Defensa", 2);
        equipo.agregarJugadorValidado(new Jugador("dos38", "1234", "Dos", 0, LocalDate.of(2001, 1, 1)), "Ataque", 3);
        assertThrows(IllegalStateException.class,
            () -> equipo.agregarJugadorValidado(new Jugador("tres38", "1234", "Tres", 0, LocalDate.of(2002, 1, 1)), "Portero", 4));
    }

    @Test
    void menorDeEdadSeRechaza() {
        Jugador menor = new Jugador("menor38", "1234", "Menor", 0, LocalDate.now().minusYears(17));
        assertThrows(IllegalArgumentException.class,
            () -> equipo.agregarJugadorValidado(menor, "Defensa", 4));
    }

    @Test
    void retirarJugadorLiberaSuCamiseta() {
        equipo.agregarJugadorValidado(jugador, "Defensa", 2);
        equipo.retirarJugador(jugador);
        assertTrue(equipo.getJugadores().isEmpty());
        assertTrue(equipo.numeroDisponible(2));
    }
}
