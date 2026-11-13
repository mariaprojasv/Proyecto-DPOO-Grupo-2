package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import academia.*;
import club.ClubDeportivoSeneca;
import deportes.Deporte;
import persistencia.PersistenciaArchivos;
import reservas.Instalacion;
import usuarios.Entrenador;
import usuarios.Jugador;

@DisplayName("Persistencia de academia")
class PersistenciaAcademiaTest {
    @TempDir Path carpeta;
    private ClubDeportivoSeneca club;
    private Deporte tenis;
    private Jugador ana;
    private Jugador bob;
    private Entrenador entrenador;
    private Instalacion cancha;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Seneca");
        tenis = new Deporte("Tenis", 2, 3, 1);
        club.getAreaDeportiva().agregarDeporte(tenis);
        ana = new Jugador("ana44", "pass", "Ana", 0, LocalDate.of(2001, 2, 3));
        bob = new Jugador("bob44", "pass", "Bob", 0, LocalDate.of(2000, 1, 2));
        entrenador = new Entrenador("ent44", "pass", "Entrenador", "Tenis");
        club.registrarUsuario(ana);
        club.registrarUsuario(bob);
        club.registrarUsuario(entrenador);
        cancha = new Instalacion("Cancha44", tenis, 4, new HashSet<String>());
        club.getGestionReservas().agregarInstalacion(cancha);
    }

    private ClubDeportivoSeneca restaurar() throws IOException {
        PersistenciaArchivos archivos = new PersistenciaArchivos(carpeta.toString());
        archivos.guardar(club);
        ClubDeportivoSeneca nuevo = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(nuevo);
        return nuevo;
    }

    @Test void conservaInscripcionesNivelYRanking() throws IOException {
        club.getAcademia().inscribirJugador(ana, tenis, "AVANZADO");
        club.getAcademia().inscribirJugador(bob, tenis, "INICIAL");
        club.getAcademia().actualizarRanking(club.getAcademia().consultarInscripciones(ana).get(0), 50);
        ClubDeportivoSeneca nuevo = restaurar();
        assertEquals(2, nuevo.getAcademia().getInscripciones().size());
        InscripcionIndividual inscripcion = nuevo.getAcademia().consultarInscripciones((Jugador) nuevo.buscarUsuario("ana44")).get(0);
        assertEquals("AVANZADO", inscripcion.getNivel());
        assertEquals(50, inscripcion.getRanking());
    }

    @Test void conservaEntrenamientoYRelacionConInstalacion() throws IOException {
        Entrenamiento sesion = club.getAcademia().programarEntrenamientoValidado(LocalDate.of(2026, 11, 20), tenis, entrenador, LocalTime.of(10, 30), 90, cancha);
        ClubDeportivoSeneca nuevo = restaurar();
        assertEquals(1, nuevo.getAcademia().getEntrenamientos().size());
        Entrenamiento recuperado = nuevo.getAcademia().getEntrenamientos().get(0);
        assertEquals(LocalTime.of(10, 30), recuperado.getHoraInicio());
        assertEquals(90, recuperado.getDuracionMinutos());
        assertSame(nuevo.getGestionReservas().getInstalaciones().get(0), recuperado.getInstalacion());
    }

    @Test void conservaInvitadosYAsistencia() throws IOException {
        club.getAcademia().inscribirJugador(ana, tenis, "AVANZADO");
        Entrenamiento sesion = club.getAcademia().programarEntrenamientoValidado(LocalDate.of(2026, 11, 20), tenis, entrenador, LocalTime.of(10, 30), 90, cancha);
        sesion.invitarJugador(ana);
        club.getAcademia().registrarAsistenciaValidada(sesion, ana, AsistenciaEntrenamiento.Estado.TARDE, 4, "Trafico");
        ClubDeportivoSeneca nuevo = restaurar();
        Entrenamiento recuperado = nuevo.getAcademia().getEntrenamientos().get(0);
        assertSame(nuevo.buscarUsuario("ana44"), recuperado.getInvitados().get(0));
        assertEquals(AsistenciaEntrenamiento.Estado.TARDE, recuperado.getAsistencias().get(0).getEstado());
        assertEquals(4, recuperado.getAsistencias().get(0).getCalificacion());
    }

    @Test void conservaParticipantesDePartidoPractica() throws IOException {
        // Given: ambos participantes deben estar inscritos en el deporte.
        club.getAcademia().inscribirJugador(ana, tenis, "AVANZADO");
        club.getAcademia().inscribirJugador(bob, tenis, "INICIAL");
        PartidoPractica partido = club.getAcademia().programarPartidoPractica(LocalDate.of(2026, 11, 21), tenis, "Practica libre");
        club.getAcademia().agregarJugadorPartidoPractica(partido, ana);
        club.getAcademia().agregarJugadorPartidoPractica(partido, bob);
        ClubDeportivoSeneca nuevo = restaurar();
        assertEquals(1, nuevo.getAcademia().getPartidosPractica().size());
        assertEquals(2, nuevo.getAcademia().getPartidosPractica().get(0).getJugadores().size());
    }
}
