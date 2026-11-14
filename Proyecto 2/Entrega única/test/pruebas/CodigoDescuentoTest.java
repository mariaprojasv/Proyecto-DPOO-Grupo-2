package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import Tienda.CodigoDescuento;

@DisplayName("Codigos de descuento: validacion y uso unico")
class CodigoDescuentoTest {
    private CodigoDescuento codigo;

    @BeforeEach
    void setUp() {
        // GIVEN: codigo valido que aun no ha sido utilizado.
        codigo = new CodigoDescuento("PROMO", 20);
    }

    @ParameterizedTest(name = "porcentaje {0} es valido")
    @CsvSource({"1", "50", "100"})
    void porcentajesValidos(double porcentaje) {
        // WHEN
        CodigoDescuento nuevo = new CodigoDescuento("P", porcentaje);
        // THEN
        assertEquals(porcentaje, nuevo.getPorcentaje(), 0.0001);
        assertTrue(nuevo.estaDisponible());
    }

    @ParameterizedTest(name = "porcentaje {0} es invalido")
    @CsvSource({"-1", "0", "101"})
    void porcentajesInvalidos(double porcentaje) {
        assertThrows(IllegalArgumentException.class, () -> new CodigoDescuento("P", porcentaje));
    }

    @Test
    void codigoVacioSeRechaza() {
        assertThrows(IllegalArgumentException.class, () -> new CodigoDescuento("  ", 10));
    }

    @Test
    void codigoNuloSeRechaza() {
        assertThrows(IllegalArgumentException.class, () -> new CodigoDescuento(null, 10));
    }

    @Nested
    class EstadoDeUso {
        @Test
        void marcarUsoCambiaDisponibilidad() {
            codigo.marcarUtilizado();
            assertFalse(codigo.estaDisponible());
        }

        @Test
        void segundoUsoSeRechaza() {
            codigo.marcarUtilizado();
            assertThrows(IllegalStateException.class, () -> codigo.marcarUtilizado());
            assertFalse(codigo.estaDisponible());
        }
    }
}
