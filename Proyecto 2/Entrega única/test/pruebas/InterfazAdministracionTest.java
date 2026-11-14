package pruebas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import administracion.InterfazAdministracion;
import club.ClubDeportivoSeneca;
import usuarios.Administrador;

@DisplayName("Autenticacion y autorizacion administrativa")
class InterfazAdministracionTest {
    private ClubDeportivoSeneca club;
    private InterfazAdministracion interfaz;

    @BeforeEach
    void setUp() {
        // GIVEN: un club nuevo con un administrador registrado.
        club = new ClubDeportivoSeneca("Seneca");
        assertTrue(club.registrarUsuario(new Administrador("admin", "clave", "Ana")));
        interfaz = new InterfazAdministracion(club);
    }

    @Test
    void rechazaClubNulo() {
        assertThrows(IllegalArgumentException.class, () -> new InterfazAdministracion(null));
    }

    @Test
    void iniciaSesionConCredencialesCorrectas() {
        // WHEN
        interfaz.iniciarSesion("admin", "clave");
        // THEN
        assertTrue(interfaz.haySesionAdministrativa());
        assertNotNull(interfaz.consultarReporteInventarios());
    }

    @ParameterizedTest(name = "credenciales invalidas: {0} / {1}")
    @CsvSource(value = {"admin, incorrecta", "desconocido, clave", "admin, NULL", "NULL, clave"}, nullValues = "NULL")
    void rechazaCredencialesInvalidas(String login, String password) {
        assertThrows(SecurityException.class, () -> interfaz.iniciarSesion(login, password));
        assertFalse(interfaz.haySesionAdministrativa());
    }

    @Test
    void noPermiteConsultarReportesSinSesion() {
        assertThrows(SecurityException.class, () -> interfaz.consultarReporteInventarios());
        assertThrows(SecurityException.class, () -> interfaz.consultarReporteDeportivo(LocalDate.now(), LocalDate.now()));
        assertThrows(SecurityException.class, () -> interfaz.consultarReporteVentas(LocalDate.now(), LocalDate.now()));
    }

    @Nested
    class CicloDeSesion {
        @Test
        void cerrarSesionRevocaAcceso() {
            interfaz.iniciarSesion("admin", "clave");
            interfaz.cerrarSesion();
            assertFalse(interfaz.haySesionAdministrativa());
            assertThrows(SecurityException.class, () -> interfaz.consultarReporteInventarios());
        }

        @Test
        void intentoFallidoInvalidaSesionAnterior() {
            interfaz.iniciarSesion("admin", "clave");
            assertThrows(SecurityException.class, () -> interfaz.iniciarSesion("admin", "mal"));
            assertFalse(interfaz.haySesionAdministrativa());
        }

        @Test
        void modificarCopiaDeUsuariosNoEliminaAdministradorNiInvalidaSesion() {
            interfaz.iniciarSesion("admin", "clave");
            club.getUsuarios().clear();
            assertNotNull(club.buscarUsuario("admin"));
            assertTrue(interfaz.haySesionAdministrativa());
            assertNotNull(interfaz.consultarReporteInventarios());
        }
    }
}
