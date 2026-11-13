package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import administracion.SugerenciaSnack;
import club.ClubDeportivoSeneca;
import persistencia.PersistenciaArchivos;
import usuarios.Administrador;
import usuarios.Entrenador;

@DisplayName("Persistencia de sugerencias de cafeteria")
class PersistenciaSugerenciasTest {
    @TempDir Path carpeta;
    private ClubDeportivoSeneca club;
    private Entrenador autor;
    private Administrador administrador;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Seneca");
        autor = new Entrenador("ent43", "clave", "Entrenador", "Futbol");
        administrador = new Administrador("adm43", "clave", "Admin");
        club.registrarUsuario(autor);
        club.registrarUsuario(administrador);
    }

    private ClubDeportivoSeneca restaurar() throws IOException {
        PersistenciaArchivos archivos = new PersistenciaArchivos(carpeta.toString());
        archivos.guardar(club);
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(recuperado);
        return recuperado;
    }

    @Test void guardaArchivoDeSugerencias() throws IOException {
        club.getGestionSugerencias().sugerirSnack(autor, "Pendiente", 1000, 2, "Ninguno");
        restaurar();
        assertTrue(Files.exists(carpeta.resolve("sugerencias_snacks.txt")));
    }

    @Test void conservaLosTresEstadosYMotivoDeRechazo() throws IOException {
        club.getGestionSugerencias().sugerirSnack(autor, "Pendiente", 1000, 2, "Ninguno");
        SugerenciaSnack aprobada = club.getGestionSugerencias().sugerirSnack(autor, "Aprobada", 2000, 3, "Mani");
        club.getGestionSugerencias().aprobar(administrador, aprobada);
        SugerenciaSnack rechazada = club.getGestionSugerencias().sugerirSnack(autor, "Rechazada", 3000, 1, "Leche");
        club.getGestionSugerencias().rechazar(administrador, rechazada, "No viable");
        ClubDeportivoSeneca nuevo = restaurar();
        assertEquals(3, nuevo.getGestionSugerencias().getSugerencias().size());
        assertEquals(SugerenciaSnack.Estado.PENDIENTE, nuevo.getGestionSugerencias().getSugerencias().get(0).getEstado());
        assertEquals(SugerenciaSnack.Estado.APROBADA, nuevo.getGestionSugerencias().getSugerencias().get(1).getEstado());
        assertEquals(SugerenciaSnack.Estado.RECHAZADA, nuevo.getGestionSugerencias().getSugerencias().get(2).getEstado());
        assertEquals("No viable", nuevo.getGestionSugerencias().getSugerencias().get(2).getMotivo());
        assertEquals("Mani", nuevo.getGestionSugerencias().getSugerencias().get(1).getAlergenos());
    }

    @Test void restauraAutorYPublicaSoloProductoAprobado() throws IOException {
        club.getGestionSugerencias().sugerirSnack(autor, "Pendiente", 1000, 2, "Ninguno");
        SugerenciaSnack aprobada = club.getGestionSugerencias().sugerirSnack(autor, "Aprobada", 2000, 3, "Mani");
        club.getGestionSugerencias().aprobar(administrador, aprobada);
        SugerenciaSnack rechazada = club.getGestionSugerencias().sugerirSnack(autor, "Rechazada", 3000, 1, "Leche");
        club.getGestionSugerencias().rechazar(administrador, rechazada, "No viable");
        ClubDeportivoSeneca nuevo = restaurar();
        assertSame(nuevo.buscarUsuario("ent43"), nuevo.getGestionSugerencias().getSugerencias().get(0).getAutor());
        assertNotNull(nuevo.getCafeteria().buscarProducto("Aprobada"));
        assertNull(nuevo.getCafeteria().buscarProducto("Pendiente"));
        assertNull(nuevo.getCafeteria().buscarProducto("Rechazada"));
        assertEquals(1, nuevo.getCafeteria().getProductos().size());
    }
}
