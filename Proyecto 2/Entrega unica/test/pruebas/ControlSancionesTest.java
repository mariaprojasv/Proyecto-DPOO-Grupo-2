package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import deportes.ControlSanciones;
import deportes.Tarjeta;
import usuarios.Jugador;

@DisplayName("Sanciones: tarjetas y suspensiones")
class ControlSancionesTest {
    private ControlSanciones control;
    private Jugador jugador;

    @BeforeEach
    void setUp() {
        // GIVEN: un jugador sin tarjetas ni sanciones.
        control = new ControlSanciones();
        jugador = new Jugador("j38", "1234", "Jugador", 0, LocalDate.of(2000, 1, 1));
    }

    @ParameterizedTest(name = "tarjeta {0}: aceptada={1}")
    @CsvSource({"AMARILLA, true", "amarilla, true", "ROJA, true", "AZUL, false"})
    void tiposDeTarjeta(String tipo, boolean aceptada) {
        // WHEN
        boolean resultado = control.registrarTarjeta(new Tarjeta(jugador, tipo));
        // THEN
        assertEquals(aceptada, resultado);
    }

    @Test
    void rechazaTarjetaNula() {
        assertFalse(control.registrarTarjeta(null));
        assertFalse(control.estaSuspendido(jugador));
    }

    @Test
    void rechazaJugadorNulo() {
        assertFalse(control.registrarTarjeta(new Tarjeta(null, "ROJA")));
    }

    @Nested
    @DisplayName("Transiciones de estado")
    class Transiciones {
        @Test
        void primeraAmarillaNoSuspende() {
            assertTrue(control.registrarTarjeta(new Tarjeta(jugador, "AMARILLA")));
            assertEquals(1, control.getAmarillas(jugador));
            assertFalse(control.estaSuspendido(jugador));
        }

        @Test
        void segundaAmarillaSuspendeYReiniciaContador() {
            control.registrarTarjeta(new Tarjeta(jugador, "AMARILLA"));
            control.registrarTarjeta(new Tarjeta(jugador, "AMARILLA"));
            assertTrue(control.estaSuspendido(jugador));
            assertEquals(0, control.getAmarillas(jugador));
        }

        @Test
        void rojaSuspendeInmediatamente() {
            control.registrarTarjeta(new Tarjeta(jugador, "ROJA"));
            assertTrue(control.estaSuspendido(jugador));
            assertEquals(1, control.getSuspendidos().size());
        }

        @Test
        void cumplirSuspensionHabilitaAlJugador() {
            control.registrarTarjeta(new Tarjeta(jugador, "ROJA"));
            control.cumplirSuspension(jugador);
            assertFalse(control.estaSuspendido(jugador));
        }

        @Test
        void rojaRepetidaNoDuplicaSuspendidos() {
            control.registrarTarjeta(new Tarjeta(jugador, "ROJA"));
            control.registrarTarjeta(new Tarjeta(jugador, "ROJA"));
            assertEquals(1, control.getSuspendidos().size());
        }
    }

    @Test
    void listaDevueltaNoPermiteBorrarSuspensionInterna() {
        control.registrarTarjeta(new Tarjeta(jugador, "ROJA"));
        control.getSuspendidos().clear();
        assertTrue(control.estaSuspendido(jugador));
    }
}
