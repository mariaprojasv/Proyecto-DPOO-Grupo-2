package pruebas;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import Tienda.Articulo;
import Tienda.CodigoDescuento;
import Tienda.Venta;
import club.ClubDeportivoSeneca;
import deportes.Tarjeta;
import persistencia.PersistenciaArchivos;
import usuarios.Jugador;
import usuarios.Socio;

@DisplayName("Persistencia: guardar y recuperar informacion")
class PersistenciaArchivosTest {
    @TempDir Path carpetaTemporal;
    private ClubDeportivoSeneca original;
    private PersistenciaArchivos archivos;

    @BeforeEach
    void setUp() {
        // GIVEN: cada prueba usa un club nuevo y una carpeta temporal independiente.
        original = new ClubDeportivoSeneca("Seneca");
        archivos = new PersistenciaArchivos(carpetaTemporal.toString());
    }

    private ClubDeportivoSeneca recuperar() throws IOException {
        archivos.guardar(original);
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(recuperado);
        return recuperado;
    }

    @Nested
    @DisplayName("Sanciones")
    class Sanciones {
        @Test
        void conservaUnaTarjetaAmarilla() throws IOException {
            Jugador jugador = new Jugador("ana42", "123", "Ana", 0, LocalDate.of(2000, 1, 1));
            original.registrarUsuario(jugador);
            original.getControlSanciones().registrarTarjeta(new Tarjeta(jugador, "AMARILLA"));
            // WHEN
            ClubDeportivoSeneca nuevo = recuperar();
            // THEN
            Jugador restaurado = (Jugador) nuevo.buscarUsuario("ana42");
            assertNotNull(restaurado);
            assertEquals(1, nuevo.getControlSanciones().getAmarillas(restaurado));
            assertFalse(nuevo.getControlSanciones().estaSuspendido(restaurado));
        }

        @Test
        void conservaSuspensionPorTarjetaRoja() throws IOException {
            Jugador jugador = new Jugador("luis42", "123", "Luis", 0, LocalDate.of(2000, 1, 1));
            original.registrarUsuario(jugador);
            original.getControlSanciones().registrarTarjeta(new Tarjeta(jugador, "ROJA"));
            // WHEN
            ClubDeportivoSeneca nuevo = recuperar();
            // THEN
            Jugador restaurado = (Jugador) nuevo.buscarUsuario("luis42");
            assertNotNull(restaurado);
            assertTrue(nuevo.getControlSanciones().estaSuspendido(restaurado));
        }

        @Test
        void suspensionCumplidaNoReapareceAlGuardar() throws IOException {
            Jugador jugador = new Jugador("maria42", "123", "Maria", 0, LocalDate.of(2000, 1, 1));
            original.registrarUsuario(jugador);
            original.getControlSanciones().registrarTarjeta(new Tarjeta(jugador, "ROJA"));
            ClubDeportivoSeneca recuperado = recuperar();
            Jugador restaurado = (Jugador) recuperado.buscarUsuario("maria42");
            recuperado.getControlSanciones().cumplirSuspension(restaurado);
            archivos.guardar(recuperado);
            ClubDeportivoSeneca tercero = new ClubDeportivoSeneca("Seneca");
            archivos.cargar(tercero);
            assertFalse(tercero.getControlSanciones().estaSuspendido(
                    (Jugador) tercero.buscarUsuario("maria42")));
        }
    }

    @Nested
    @DisplayName("Ventas")
    class Ventas {
        @Test
        void conservaVentaDetalleTotalYComprador() throws IOException {
            Socio socio = new Socio("cliente42", "1234", "Cliente", 0);
            original.registrarUsuario(socio);
            Articulo articulo = new Articulo("Camiseta", "Ropa", 50000, 10);
            original.getTienda().agregarArticulo(articulo);
            Venta venta = original.getTienda().crearVenta(socio);
            venta.agregarDetalle(articulo, 2);
            venta.aplicarCodigo(new CodigoDescuento("DESCUENTO42", 10));
            venta.calcularTotal();
            double total = venta.getTotal();
            int cantidadRestante = articulo.getCantidad();
            // WHEN
            ClubDeportivoSeneca nuevo = recuperar();
            // THEN
            assertEquals(1, nuevo.getTienda().getVentas().size());
            Venta restaurada = nuevo.getTienda().getVentas().get(0);
            assertEquals(1, restaurada.getDetalles().size());
            assertEquals(2, restaurada.getDetalles().get(0).getCantidad());
            assertEquals(total, restaurada.getTotal(), 0.001);
            assertEquals(cantidadRestante, nuevo.getTienda().getArticulos().get(0).getCantidad());
            assertSame(nuevo.buscarUsuario("cliente42"), restaurada.getComprador());
        }
    }

    @Test
    void guardarCreaArchivosEnCarpetaTemporal() throws IOException {
        // WHEN
        archivos.guardar(original);
        // THEN
        assertTrue(Files.exists(carpetaTemporal.resolve("usuarios.txt")));
        assertTrue(Files.exists(carpetaTemporal.resolve("sanciones.txt")));
    }
}
