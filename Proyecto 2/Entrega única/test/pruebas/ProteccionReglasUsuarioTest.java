package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import club.ClubDeportivoSeneca;
import deportes.Deporte;
import reservas.GestionReservas;
import reservas.Instalacion;
import usuarios.Administrador;
import usuarios.Socio;

/** Verifica que las reglas de negocio no puedan eludirse alterando listados de consulta. */
class ProteccionReglasUsuarioTest {
    @Test
    void listadoDeReservasNoPermiteEliminarOInventarReservas() {
        GestionReservas gestion = new GestionReservas();
        Instalacion cancha = new Instalacion("Cancha", new Deporte("Futbol", 1, 3, 1), 10, Set.of("libre"));
        Socio socio = new Socio("socio-r59", "1234", "Socio", 0);
        assertTrue(gestion.agregarInstalacion(cancha));
        gestion.realizarReservaValidada(socio, cancha, LocalDate.of(2026, 11, 25),
            LocalTime.of(9, 0), LocalTime.of(10, 0), 5, "libre");
        gestion.getReservas().clear();
        assertEquals(1, gestion.getReservas().size());
        assertThrows(IllegalStateException.class, () -> gestion.realizarReservaValidada(
            socio, cancha, LocalDate.of(2026, 11, 25), LocalTime.of(9, 30),
            LocalTime.of(10, 30), 5, "libre"));
    }

    @Test
    void listadoDeInstalacionesNoPermiteRegistrarSinValidacion() {
        GestionReservas gestion = new GestionReservas();
        Instalacion cancha = new Instalacion("Cancha", new Deporte("Futbol", 1, 3, 1), 10, Set.of("libre"));
        gestion.getInstalaciones().add(cancha);
        assertTrue(gestion.getInstalaciones().isEmpty());
        assertNull(gestion.realizarReserva(new Socio("socio-2", "1234", "Socio", 0), cancha,
            LocalDate.of(2026, 11, 25), LocalTime.of(9, 0), LocalTime.of(10, 0), 5, "libre"));
    }

    @Test
    void listadoDeUsuariosNoPermiteEvitarRegistroYControlDeDuplicados() {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Seneca");
        Administrador admin = new Administrador("admin-r59", "1234", "Admin");
        club.getUsuarios().add(admin);
        assertNull(club.buscarUsuario("admin-r59"));
        assertTrue(club.registrarUsuario(admin));
        club.getUsuarios().clear();
        assertSame(admin, club.buscarUsuario("admin-r59"));
        assertFalse(club.registrarUsuario(new Administrador("ADMIN-R59", "1234", "Otro")));
    }

    @Test
    void modalidadesNoSePuedenAlterarDesdeListado() {
        Instalacion cancha = new Instalacion("Cancha", new Deporte("Futbol", 1, 3, 1), 10, Set.of("libre"));
        cancha.getModalidades().add("dobles");
        assertFalse(cancha.admiteModalidad("dobles"));
    }
}
