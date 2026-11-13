package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import club.ClubDeportivoSeneca;
import usuarios.Administrador;
import usuarios.Socio;

@DisplayName("Club: usuarios, autenticacion y modulos")
class ClubUsuariosTest {
    private ClubDeportivoSeneca club;
    private Administrador admin;
    private Socio socio;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Club Deportivo Seneca");
        admin = new Administrador("admin49", "clave", "Administrador");
        socio = new Socio("socio49", "abcd", "Socio", 0);
    }

    @Test void registraUsuariosYLosEncuentraSinDuplicarLogin() {
        assertTrue(club.registrarUsuario(admin));
        assertTrue(club.registrarUsuario(socio));
        assertSame(socio, club.buscarUsuario("SOCIO49"));
        assertFalse(club.registrarUsuario(new Socio("SOCIO49", "otra", "Duplicado", 0)));
        assertEquals(2, club.getUsuarios().size());
    }

    @Test void rechazaUsuarioNuloYLoginVacio() {
        assertFalse(club.registrarUsuario(null));
        assertFalse(club.registrarUsuario(new Socio("", "clave", "Sin login", 0)));
        assertTrue(club.getUsuarios().isEmpty());
    }

    @Test void autenticacionAceptaCredencialesCorrectas() {
        club.registrarUsuario(admin);
        assertSame(admin, club.iniciarSesion("admin49", "clave"));
    }

    @Test void autenticacionRechazaContrasenaIncorrectaUsuarioInexistenteYNulo() {
        club.registrarUsuario(admin);
        assertNull(club.iniciarSesion("admin49", "incorrecta"));
        assertNull(club.iniciarSesion("inexistente", "clave"));
        assertNull(club.iniciarSesion("admin49", null));
        assertNull(club.buscarUsuario(null));
    }

    @Test void inicializaModulosDelClub() {
        assertEquals("Club Deportivo Seneca", club.getNombre());
        assertNotNull(club.getAreaDeportiva());
        assertNotNull(club.getAcademia());
        assertNotNull(club.getGestionReservas());
        assertNotNull(club.getTienda());
        assertNotNull(club.getCafeteria());
        assertNotNull(club.getGestionTurnos());
        assertNotNull(club.getControlSanciones());
        assertNotNull(club.getGestionSugerencias());
        assertNotNull(club.getGestionInventarios());
    }
}
