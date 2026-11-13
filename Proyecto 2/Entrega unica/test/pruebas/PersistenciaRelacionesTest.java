package pruebas;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import club.ClubDeportivoSeneca;
import deportes.*;
import academia.Entrenamiento;
import persistencia.PersistenciaArchivos;
import reservas.Instalacion;
import usuarios.*;

class PersistenciaRelacionesTest {
    @TempDir Path carpeta;
    @Test void conservaRelacionesEntreUsuariosReservasEquiposYAcademia() throws Exception {
        ClubDeportivoSeneca original = new ClubDeportivoSeneca("Seneca");
        Deporte futbol = new Deporte("Futbol", 7, 3, 1);
        Categoria mayores = new Categoria("Mayores", 18, 99);
        Jugador jugador = new Jugador("jug48", "1234", "Jugador Uno", 0, LocalDate.of(2000, 1, 1));
        Entrenador entrenador = new Entrenador("ent48", "1234", "Entrenador Uno", "Futbol");
        original.registrarUsuario(jugador);
        original.registrarUsuario(entrenador);
        original.getAreaDeportiva().agregarDeporte(futbol);
        original.getAreaDeportiva().agregarCategoria(mayores);
        Equipo equipo = original.getAreaDeportiva().crearEquipo("Leones", futbol, mayores, 18);
        assertTrue(original.getAreaDeportiva().inscribirJugadorEnEquipo(jugador, equipo, "Defensa", 4));
        HashSet<String> modalidades = new HashSet<>();
        modalidades.add("Futbol 7");
        Instalacion cancha = new Instalacion("Cancha Norte", futbol, 14, modalidades);
        original.getGestionReservas().agregarInstalacion(cancha);
        assertNotNull(original.getGestionReservas().realizarReserva(jugador, cancha, LocalDate.of(2026, 11, 2), LocalTime.of(10, 0), LocalTime.of(11, 0), 10, "Futbol 7"));
        assertTrue(original.getAcademia().inscribirJugador(jugador, futbol));
        Entrenamiento entrenamiento = original.getAcademia().programarEntrenamiento(LocalDate.of(2026, 11, 3), futbol, entrenador);
        assertTrue(original.getAcademia().registrarAsistencia(entrenamiento, jugador, true, "Puntual"));
        PersistenciaArchivos archivos = new PersistenciaArchivos(carpeta.toString());
        archivos.guardar(original);
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(recuperado);
        assertAll(
            () -> assertEquals(1, recuperado.getAreaDeportiva().getCategorias().size()),
            () -> assertEquals(1, recuperado.getAreaDeportiva().getEquipos().size()),
            () -> assertEquals(1, recuperado.getAreaDeportiva().getEquipos().get(0).getJugadores().size()),
            () -> assertEquals(1, recuperado.getGestionReservas().getInstalaciones().size()),
            () -> assertEquals(1, recuperado.getGestionReservas().getReservas().size()),
            () -> assertSame(recuperado.buscarUsuario("jug48"), recuperado.getGestionReservas().getReservas().get(0).getUsuario()),
            () -> assertEquals(1, recuperado.getAcademia().getInscripciones().size()),
            () -> assertEquals(1, recuperado.getAcademia().getEntrenamientos().size()),
            () -> assertEquals(1, recuperado.getAcademia().getEntrenamientos().get(0).getAsistencias().size()),
            () -> assertInstanceOf(Entrenador.class, recuperado.buscarUsuario("ent48"))
        );
    }
}
