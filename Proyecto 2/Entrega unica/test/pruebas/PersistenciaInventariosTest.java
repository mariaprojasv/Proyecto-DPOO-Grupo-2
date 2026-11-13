package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import club.ClubDeportivoSeneca;
import persistencia.PersistenciaArchivos;
import Tienda.Articulo;
import Tienda.tienda;

@DisplayName("Persistencia de inventarios por ubicacion")
class PersistenciaInventariosTest {
    @TempDir Path carpeta;
    private ClubDeportivoSeneca club;
    private PersistenciaArchivos archivos;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Seneca");
        archivos = new PersistenciaArchivos(carpeta.toString());
        tienda norte = new tienda();
        tienda sur = new tienda();
        norte.agregarArticulo(new Articulo("Balon", "Deportes", 15000, 5));
        sur.agregarArticulo(new Articulo("Cono", "Entrenamiento", 3000, 3));
        club.getGestionInventarios().registrarTienda("Norte", norte);
        club.getGestionInventarios().registrarTienda("Sur", sur);
        club.getGestionInventarios().registrarTienda("Bodega", new tienda());
        club.getTienda().agregarArticulo(new Articulo("Camiseta", "Ropa", 40000, 2));
    }

    private ClubDeportivoSeneca restaurar() throws IOException {
        archivos.guardar(club);
        ClubDeportivoSeneca nuevo = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(nuevo);
        return nuevo;
    }

    @Test void guardaUbicacionesYTiendasVacias() throws IOException {
        ClubDeportivoSeneca nuevo = restaurar();
        assertTrue(Files.exists(carpeta.resolve("inventarios_ubicaciones.txt")));
        assertEquals(3, nuevo.getGestionInventarios().getUbicaciones().size());
        assertTrue(nuevo.getGestionInventarios().consultarTienda("Bodega").getArticulos().isEmpty());
    }

    @Test void conservaTransferenciaCantidadYPrecio() throws IOException {
        club.getGestionInventarios().transferirArticulo("Norte", "Sur", "Balon");
        ClubDeportivoSeneca nuevo = restaurar();
        assertNull(nuevo.getGestionInventarios().consultarTienda("Norte").buscarArticulo("Balon"));
        Articulo balon = nuevo.getGestionInventarios().consultarTienda("Sur").buscarArticulo("Balon");
        assertEquals(5, balon.getCantidad());
        assertEquals(15000, balon.getPrecio());
        assertEquals(3, nuevo.getGestionInventarios().consultarTienda("Sur").buscarArticulo("Cono").getCantidad());
    }

    @Test void inventarioPrincipalNoSeMezclaConUbicaciones() throws IOException {
        ClubDeportivoSeneca nuevo = restaurar();
        assertEquals(2, nuevo.getTienda().buscarArticulo("Camiseta").getCantidad());
        assertNull(nuevo.getTienda().buscarArticulo("Balon"));
    }

    @Test void transferenciaSigueDisponibleDespuesDeCargar() throws IOException {
        ClubDeportivoSeneca nuevo = restaurar();
        nuevo.getGestionInventarios().transferirArticulo("Norte", "Bodega", "Balon");
        assertEquals(5, nuevo.getGestionInventarios().consultarTienda("Bodega").buscarArticulo("Balon").getCantidad());
    }

    @Test void rechazaArchivoConCantidadNegativa() throws IOException {
        archivos.guardar(club);
        Files.writeString(carpeta.resolve("inventarios_ubicaciones.txt"), "T|Norte\nA|Norte|Roto|Prueba|3|-2\n");
        assertThrows(IOException.class, () -> archivos.cargar(new ClubDeportivoSeneca("Seneca")));
    }

    @Test void archivoDeUbicacionesAusenteEsCompatible() throws IOException {
        archivos.guardar(club);
        Files.delete(carpeta.resolve("inventarios_ubicaciones.txt"));
        ClubDeportivoSeneca nuevo = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(nuevo);
        assertTrue(nuevo.getGestionInventarios().getUbicaciones().isEmpty());
    }
}
