package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import club.ClubDeportivoSeneca;
import deportes.Deporte;
import reservas.Instalacion;
import Tienda.Articulo;
import Tienda.Venta;
import cafeteria.bebida;
import cafeteria.snack;
import persistencia.PersistenciaArchivos;
import usuarios.Socio;

@DisplayName("Flujo general del club: reservas, ventas, cafeteria y persistencia")
class FlujoGeneralClubTest {
    @TempDir Path carpeta;
    private ClubDeportivoSeneca club;
    private Socio socio;
    private Deporte futbol;
    private Instalacion cancha;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Seneca");
        socio = new Socio("socio50", "clave", "Socio", 0);
        assertTrue(club.registrarUsuario(socio));
        futbol = new Deporte("Futbol", 7, 3, 1);
        club.getAreaDeportiva().agregarDeporte(futbol);
        HashSet<String> modalidades = new HashSet<>();
        modalidades.add("Futbol 7");
        cancha = new Instalacion("Cancha Principal", futbol, 14, modalidades);
        assertTrue(club.getGestionReservas().agregarInstalacion(cancha));
    }

    @Test void rechazaReservaSolapadaSinCrearOtraReserva() {
        LocalDate dia = LocalDate.of(2026, 11, 25);
        assertNotNull(club.getGestionReservas().realizarReserva(socio, cancha, dia,
                LocalTime.of(10, 0), LocalTime.of(11, 0), 10, "Futbol 7"));
        assertNull(club.getGestionReservas().realizarReserva(socio, cancha, dia,
                LocalTime.of(10, 30), LocalTime.of(11, 30), 10, "Futbol 7"));
        assertEquals(1, club.getGestionReservas().getReservas().size());
    }

    @Test void ventasYCafeteriaDescuentanExistenciasUnaSolaVez() {
        Articulo camiseta = new Articulo("Camiseta", "Ropa", 50000, 20);
        club.getTienda().agregarArticulo(camiseta);
        Venta venta = club.getTienda().crearVenta(socio);
        venta.agregarDetalle(camiseta, 2);
        venta.calcularTotal();
        club.getCafeteria().agregarProducto(new bebida("Cafe", 5000, 20, true));
        club.getCafeteria().agregarProducto(new snack("Sandwich", 10000, 10, "Gluten"));
        assertTrue(club.getCafeteria().venderProducto("Cafe", 2));
        assertAll(
                () -> assertEquals(18, camiseta.getCantidad()),
                () -> assertEquals(18, club.getCafeteria().buscarProducto("Cafe").getCantidad()),
                () -> assertEquals(1, club.getTienda().getVentas().size()));
    }

    @Test void recuperacionConjuntaNoDuplicaVentasNiDescuentosDeInventario() throws Exception {
        Articulo camiseta = new Articulo("Camiseta", "Ropa", 50000, 20);
        club.getTienda().agregarArticulo(camiseta);
        Venta venta = club.getTienda().crearVenta(socio);
        venta.agregarDetalle(camiseta, 2);
        venta.calcularTotal();
        club.getCafeteria().agregarProducto(new bebida("Cafe", 5000, 20, true));
        assertTrue(club.getCafeteria().venderProducto("Cafe", 2));
        assertNotNull(club.getGestionReservas().realizarReserva(socio, cancha,
                LocalDate.of(2026, 11, 25), LocalTime.of(10, 0), LocalTime.of(11, 0), 10, "Futbol 7"));
        PersistenciaArchivos archivos = new PersistenciaArchivos(carpeta.toString());
        archivos.guardar(club);
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(recuperado);
        assertAll(
                () -> assertEquals(1, recuperado.getGestionReservas().getReservas().size()),
                () -> assertEquals(1, recuperado.getTienda().getVentas().size()),
                () -> assertEquals(18, recuperado.getTienda().buscarArticulo("Camiseta").getCantidad()),
                () -> assertEquals(18, recuperado.getCafeteria().buscarProducto("Cafe").getCantidad()),
                () -> assertNotNull(recuperado.buscarUsuario("socio50")));
    }
}
