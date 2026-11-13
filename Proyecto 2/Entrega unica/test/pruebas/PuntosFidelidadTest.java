package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import usuarios.Socio;

@DisplayName("Socio: acreditacion y redencion de puntos")
class PuntosFidelidadTest {
    private Socio socio;

    @BeforeEach
    void setUp() {
        // GIVEN: socio con 100 puntos.
        socio = new Socio("puntos37", "1234", "Socio", 100);
    }

    @ParameterizedTest(name = "acreditar {0} deja {1}")
    @CsvSource({"1, 101", "20, 120", "100, 200"})
    void acreditarPuntosPositivos(int cantidad, int esperado) {
        socio.acreditarPuntos(cantidad);
        assertEquals(esperado, socio.getPuntosFidelidad());
    }

    @ParameterizedTest(name = "redimir {0} deja {1}")
    @CsvSource({"1, 99", "50, 50", "100, 0"})
    void redimirHastaSaldoDisponible(int cantidad, int esperado) {
        socio.redimirPuntos(cantidad);
        assertEquals(esperado, socio.getPuntosFidelidad());
    }

    @Test
    void acreditarCeroNoCambiaSaldo() {
        assertThrows(IllegalArgumentException.class, () -> socio.acreditarPuntos(0));
        assertEquals(100, socio.getPuntosFidelidad());
    }

    @Test
    void redimirNegativosNoCambiaSaldo() {
        assertThrows(IllegalArgumentException.class, () -> socio.redimirPuntos(-1));
        assertEquals(100, socio.getPuntosFidelidad());
    }

    @Test
    void redimirMasDelSaldoSeRechaza() {
        assertThrows(IllegalStateException.class, () -> socio.redimirPuntos(101));
        assertEquals(100, socio.getPuntosFidelidad());
    }
}
