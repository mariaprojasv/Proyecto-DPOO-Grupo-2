package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import Tienda.Articulo;
import Tienda.GestionInventarios;
import Tienda.tienda;

@DisplayName("Inventarios por ubicacion y transferencias")
class GestionInventariosTest {
    private GestionInventarios gestion;
    private tienda norte;
    private tienda sur;

    @BeforeEach
    void setUp() {
        // GIVEN: dos ubicaciones y un articulo disponible solo en Norte.
        gestion = new GestionInventarios();
        norte = new tienda();
        sur = new tienda();
        norte.agregarArticulo(new Articulo("Balon", "Deportes", 20000, 5));
        gestion.registrarTienda("Norte", norte);
        gestion.registrarTienda("Sur", sur);
    }

    @Test
    void registraYConsultaUbicacionesSinDistinguirMayusculas() {
        assertEquals(2, gestion.getUbicaciones().size());
        assertSame(norte, gestion.consultarTienda(" NORTE "));
    }

    @Test
    void noPermiteUbicacionesDuplicadas() {
        assertThrows(IllegalStateException.class, () -> gestion.registrarTienda("norte", new tienda()));
    }

    @ParameterizedTest(name = "ubicacion invalida: [{0}]")
    @CsvSource(value = {"NULL", "' '", "''"}, nullValues = "NULL")
    void rechazaUbicacionesInvalidas(String ubicacion) {
        assertThrows(IllegalArgumentException.class, () -> gestion.consultarTienda(ubicacion));
    }

    @Test
    void rechazaTiendaNula() {
        assertThrows(IllegalArgumentException.class, () -> gestion.registrarTienda("Oeste", null));
    }

    @Test
    void rechazaUbicacionDesconocida() {
        assertThrows(IllegalArgumentException.class, () -> gestion.consultarTienda("Oeste"));
    }

    @Nested
    class Transferencias {
        @Test
        void mueveArticuloConCantidadYPrecioOriginales() {
            // WHEN
            gestion.transferirArticulo("Norte", "Sur", "Balon");
            // THEN
            assertNull(norte.buscarArticulo("Balon"));
            assertEquals(5, sur.buscarArticulo("Balon").getCantidad());
            assertEquals(20000, sur.buscarArticulo("Balon").getPrecio(), 0.001);
        }

        @Test
        void transferirNoModificaUnaCopiaDelInventario() {
            gestion.transferirArticulo("Norte", "Sur", "Balon");
            assertNull(norte.buscarArticulo("Balon"));
            assertNotNull(sur.buscarArticulo("Balon"));
            assertEquals(0, norte.getArticulos().size());
            assertEquals(1, sur.getArticulos().size());
        }

        @Test
        void noPermiteOrigenYDestinoIguales() {
            assertThrows(IllegalArgumentException.class,
                    () -> gestion.transferirArticulo("Norte", "norte", "Balon"));
            assertEquals(5, norte.buscarArticulo("Balon").getCantidad());
        }

        @Test
        void noPermiteArticuloAusenteEnOrigen() {
            assertThrows(IllegalStateException.class,
                    () -> gestion.transferirArticulo("Sur", "Norte", "Balon"));
        }

        @Test
        void noPermiteArticuloDuplicadoEnDestino() {
            sur.agregarArticulo(new Articulo("Balon", "Deportes", 10000, 1));
            assertThrows(IllegalStateException.class,
                    () -> gestion.transferirArticulo("Norte", "Sur", "Balon"));
            assertEquals(5, norte.buscarArticulo("Balon").getCantidad());
            assertEquals(1, sur.buscarArticulo("Balon").getCantidad());
        }

        @Test
        void noPermiteRepetirTransferencia() {
            gestion.transferirArticulo("Norte", "Sur", "Balon");
            assertThrows(IllegalStateException.class,
                    () -> gestion.transferirArticulo("Norte", "Sur", "Balon"));
        }

        @Test
        void noPermiteArticuloSinNombre() {
            assertThrows(IllegalArgumentException.class,
                    () -> gestion.transferirArticulo("Norte", "Sur", " "));
        }

        @Test
        void noPermiteDestinoInexistente() {
            assertThrows(IllegalArgumentException.class,
                    () -> gestion.transferirArticulo("Norte", "Oeste", "Balon"));
        }
    }

    @Test
    void listaDeUbicacionesEsCopiaDefensiva() {
        gestion.getUbicaciones().clear();
        assertEquals(2, gestion.getUbicaciones().size());
    }
}
