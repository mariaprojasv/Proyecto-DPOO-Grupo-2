package pruebas;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import administracion.GestionSugerencias;
import administracion.SugerenciaSnack;
import club.ClubDeportivoSeneca;
import usuarios.Administrador;

@DisplayName("Sugerencias de snacks y autorizacion")
class GestionSugerenciasTest {
    private ClubDeportivoSeneca club;
    private GestionSugerencias gestion;
    private Administrador admin;

    @BeforeEach
    void setUp() {
        club = new ClubDeportivoSeneca("Seneca");
        gestion = club.getGestionSugerencias();
        admin = new Administrador("admin", "clave", "Ana");
        assertTrue(club.registrarUsuario(admin));
    }

    private SugerenciaSnack sugerencia() {
        return gestion.sugerirSnack(admin, "Barra de avena", 3500, 8, "Gluten");
    }

    @Test
    void registraSugerenciaPendiente() {
        SugerenciaSnack s = sugerencia();
        assertEquals(SugerenciaSnack.Estado.PENDIENTE, s.getEstado());
        assertEquals(1, gestion.getSugerencias().size());
    }

    @Test
    void noAceptaAutorNoRegistrado() {
        Administrador externo = new Administrador("externo", "clave", "Externo");
        assertThrows(SecurityException.class,
                () -> gestion.sugerirSnack(externo, "Granola", 2500, 3, "Avena"));
    }

    @ParameterizedTest(name = "precio {0}, cantidad {1}")
    @CsvSource({"0, 1", "-1, 1", "100, -1"})
    void rechazaDatosInvalidos(double precio, int cantidad) {
        assertThrows(IllegalArgumentException.class,
                () -> gestion.sugerirSnack(admin, "Granola", precio, cantidad, "Avena"));
    }

    @Test
    void noPermiteDuplicarSugerenciaPendiente() {
        sugerencia();
        assertThrows(IllegalStateException.class,
                () -> gestion.sugerirSnack(admin, "barra DE AVENA", 3500, 8, "Gluten"));
    }

    @Nested
    class Resolucion {
        @Test
        void aprobarCreaProductoYActualizaEstado() {
            SugerenciaSnack s = sugerencia();
            gestion.aprobar(admin, s);
            assertEquals(SugerenciaSnack.Estado.APROBADA, s.getEstado());
            assertNotNull(club.getCafeteria().buscarProducto("Barra de avena"));
        }

        @Test
        void rechazarRegistraMotivo() {
            SugerenciaSnack s = sugerencia();
            gestion.rechazar(admin, s, "No cumple requisitos");
            assertEquals(SugerenciaSnack.Estado.RECHAZADA, s.getEstado());
            assertEquals("No cumple requisitos", s.getMotivo());
        }

        @Test
        void noResuelveDosVeces() {
            SugerenciaSnack s = sugerencia();
            gestion.aprobar(admin, s);
            assertThrows(IllegalStateException.class, () -> gestion.aprobar(admin, s));
        }

        @Test
        void noPermiteAdministradorExterno() {
            SugerenciaSnack s = sugerencia();
            Administrador externo = new Administrador("otro", "clave", "Otro");
            assertThrows(SecurityException.class, () -> gestion.aprobar(externo, s));
            assertEquals(SugerenciaSnack.Estado.PENDIENTE, s.getEstado());
        }

        @Test
        void noAceptaSugerenciaAjena() {
            SugerenciaSnack ajena = new SugerenciaSnack(admin, "Otro", 1500, 1, "Ninguno");
            assertThrows(IllegalArgumentException.class, () -> gestion.aprobar(admin, ajena));
        }
    }

    @Test
    void listaDevueltaNoExponeColeccionInterna() {
        sugerencia();
        gestion.getSugerencias().clear();
        assertEquals(1, gestion.getSugerencias().size());
    }
}
