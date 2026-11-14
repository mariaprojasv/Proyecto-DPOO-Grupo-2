package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import administracion.ReporteDeportivo;
import administracion.ReporteVentas;
import Tienda.Articulo;
import Tienda.Venta;
import club.ClubDeportivoSeneca;
import deportes.Categoria;
import deportes.Deporte;
import deportes.Equipo;
import persistencia.PersistenciaArchivos;
import torneos.*;
import usuarios.*;

@DisplayName("Torneos: equipos mixtos, cobertura por dia, ranking y reportes")
class TorneosExtensionesTest {
    @TempDir Path carpeta;
    private ClubDeportivoSeneca club;
    private Administrador admin;
    private GestionTorneos gestion;
    private Deporte futbol;
    private Deporte tenis;
    private Categoria sub15;
    private final LocalDate dia = LocalDate.of(2026, 12, 5);
    private int n;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Club");
        admin = new Administrador("admin", "p", "Admin");
        club.registrarUsuario(admin);
        gestion = club.getGestionTorneos();
        futbol = new Deporte("Futbol", 2, 3, 1);
        tenis = new Deporte("Tenis", 1, 3, 0);
        sub15 = new Categoria("Sub-15", 10, 15);
        club.getAreaDeportiva().agregarDeporte(futbol);
        club.getAreaDeportiva().agregarDeporte(tenis);
        club.getAreaDeportiva().agregarCategoria(sub15);
    }

    private Jugador jugador() {
        n++;
        Jugador j = new Jugador("j" + n, "p", "Jugador " + n, 0, LocalDate.of(2012, 1, 1));
        club.registrarUsuario(j);
        return j;
    }

    @Test void equipoMixtoConEstudianteYExternoSeInscribeYJuega() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Mixto", futbol, Torneo.Modalidad.CONJUNTO, sub15,
                null, Torneo.Formato.LIGA, 5, dia, dia, 10);
        Equipo a = club.getAreaDeportiva().crearEquipo("A", futbol, sub15, 5);
        Equipo b = club.getAreaDeportiva().crearEquipo("B", futbol, sub15, 5);
        Estudiante est = new Estudiante("est", "p", "Est", "1", true);
        PersonaExterna ext = new PersonaExterna("ext", "p", "Ext");
        club.registrarUsuario(est);
        club.registrarUsuario(ext);
        club.getAreaDeportiva().inscribirIntegranteExterno(est, a);
        club.getAreaDeportiva().inscribirIntegranteExterno(ext, a);
        Jugador jb = jugador();
        Jugador jb2 = jugador();
        club.getAreaDeportiva().inscribirJugadorEnEquipo(jb, b, "Delantero", 9);
        club.getAreaDeportiva().inscribirJugadorEnEquipo(jb2, b, "Portero", 1);
        assertNotNull(gestion.inscribirEquipo(t, a, est));
        gestion.inscribirEquipo(t, b, jb);
        gestion.iniciarTorneo(admin, t);
        gestion.registrarResultado(admin, t, t.getEncuentros().get(0), 3, 0);
        assertTrue(t.estaFinalizado());
        assertEquals(1, est.getBonosDisponibles().size());
        assertEquals(1, ext.getBonosDisponibles().size());
    }

    @Test void estudianteNoMatriculadoOEquipoLlenoNoSeUnen() {
        Equipo a = club.getAreaDeportiva().crearEquipo("A", futbol, sub15, 2);
        Estudiante retirado = new Estudiante("e1", "p", "Retirado", "1", false);
        PersonaExterna e2 = new PersonaExterna("e2", "p", "E2");
        PersonaExterna e3 = new PersonaExterna("e3", "p", "E3");
        PersonaExterna e4 = new PersonaExterna("e4", "p", "E4");
        assertThrows(IllegalArgumentException.class, () -> club.getAreaDeportiva().inscribirIntegranteExterno(retirado, a));
        club.getAreaDeportiva().inscribirIntegranteExterno(e2, a);
        club.getAreaDeportiva().inscribirIntegranteExterno(e3, a);
        assertThrows(IllegalStateException.class, () -> club.getAreaDeportiva().inscribirIntegranteExterno(e4, a));
        assertThrows(IllegalStateException.class, () -> club.getAreaDeportiva().inscribirIntegranteExterno(e2,
                club.getAreaDeportiva().crearEquipo("B", futbol, sub15, 5)));
    }

    @Test void equipoMixtoSePersiste() throws Exception {
        Equipo a = club.getAreaDeportiva().crearEquipo("A", futbol, sub15, 5);
        PersonaExterna ext = new PersonaExterna("ext", "p", "Ext");
        club.registrarUsuario(ext);
        club.getAreaDeportiva().inscribirIntegranteExterno(ext, a);
        PersistenciaArchivos p = new PersistenciaArchivos(carpeta.toString());
        p.guardar(club);
        ClubDeportivoSeneca nuevo = new ClubDeportivoSeneca("Club");
        p.cargar(nuevo);
        assertEquals(1, nuevo.getAreaDeportiva().getEquipos().get(0).getIntegrantesExternos().size());
    }

    @Test void coberturaSeEvaluaPorDia() {
        GestionTurnos g = club.getGestionTurnos();
        Entrenador e1 = new Entrenador("e1", "p", "E1", "x");
        Entrenador e2 = new Entrenador("e2", "p", "E2", "x");
        Fisioterapeuta f = new Fisioterapeuta("f", "p", "F");
        for (Empleado e : new Empleado[] {e1, e2, f}) {
            club.registrarUsuario(e);
            g.agregarTurno(e, LocalTime.of(8, 0), LocalTime.of(9, 0), EnumSet.of(DayOfWeek.MONDAY));
        }
        assertTrue(g.coberturaCompleta(LocalTime.of(8, 0), LocalTime.of(9, 0), EnumSet.of(DayOfWeek.MONDAY)));
        assertFalse(g.coberturaCompleta(LocalTime.of(8, 0), LocalTime.of(9, 0), EnumSet.of(DayOfWeek.TUESDAY)));
        assertFalse(g.coberturaCompleta(LocalTime.of(8, 0), LocalTime.of(9, 0)));
        // turnos sin dias siguen valiendo para todos los dias, como antes
        GestionTurnos h = new ClubDeportivoSeneca("Otro").getGestionTurnos();
        for (Empleado e : new Empleado[] {e1, e2, f}) {
            h.agregarTurno(e, LocalTime.of(8, 0), LocalTime.of(9, 0));
        }
        assertTrue(h.coberturaCompleta(LocalTime.of(8, 0), LocalTime.of(9, 0)));
    }

    @Test void ganarUnTorneoIndividualSubeElRankingDeLaAcademia() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Tenis", tenis, Torneo.Modalidad.INDIVIDUAL, null,
                "Intermedio", Torneo.Formato.ELIMINACION, 5, dia, dia, 10);
        Jugador campeon = jugador();
        Jugador finalista = jugador();
        club.getAcademia().inscribirJugador(campeon, tenis, "Intermedio");
        club.getAcademia().inscribirJugador(finalista, tenis, "Intermedio");
        gestion.inscribirJugador(t, campeon);
        gestion.inscribirJugador(t, finalista);
        gestion.iniciarTorneo(admin, t);
        Encuentro e = t.getEncuentros().get(0);
        boolean campeonEsA = e.getCompetidorA().incluye(campeon);
        gestion.registrarResultado(admin, t, e, campeonEsA ? 6 : 1, campeonEsA ? 1 : 6);
        assertEquals(GestionTorneos.PUNTOS_RANKING[0], club.getAcademia().consultarInscripciones(campeon).get(0).getRanking());
        assertEquals(GestionTorneos.PUNTOS_RANKING[1], club.getAcademia().consultarInscripciones(finalista).get(0).getRanking());
    }

    @Test void reportesIncluyenTorneosPremiosYBonos() {
        TorneoRecreativo r = gestion.crearTorneoRecreativo(admin, "Rec", tenis, Torneo.Modalidad.INDIVIDUAL, null,
                "Intermedio", Torneo.Formato.ELIMINACION, 5, dia, dia, 10);
        TorneoCompetitivo c = gestion.crearTorneoCompetitivo(admin, "Comp", tenis, Torneo.Modalidad.INDIVIDUAL, null,
                "Intermedio", Torneo.Formato.ELIMINACION, 5, dia, dia, 1000, 0.5);
        Jugador a = jugador();
        Jugador b = jugador();
        Jugador x = jugador();
        Jugador y = jugador();
        gestion.inscribirJugador(r, a);
        gestion.inscribirJugador(r, b);
        gestion.inscribirJugador(c, x);
        gestion.inscribirJugador(c, y);
        gestion.iniciarTorneo(admin, r);
        gestion.iniciarTorneo(admin, c);
        gestion.registrarResultado(admin, r, r.getEncuentros().get(0), 6, 0);
        gestion.registrarResultado(admin, c, c.getEncuentros().get(0), 6, 0);
        BonoDescuento bono = r.getBonosOtorgados().get(0);
        Venta v = new Venta(bono.getTitular());
        v.agregarDetalle(new Articulo("Raqueta", "Tenis", 100000, 2), 1);
        v.aplicarBono(bono);
        v.finalizarVenta();
        club.getTienda().restaurarVenta(v);
        ReporteDeportivo rd = new ReporteDeportivo(club, dia, dia);
        assertEquals(2, rd.getTorneos());
        assertEquals(2, rd.getTorneosFinalizados());
        assertEquals(1000, rd.getPremiosEnEfectivo(), 0.001);
        assertEquals(1, rd.getBonosOtorgados());
        ReporteVentas rv = new ReporteVentas(club, v.getFecha(), v.getFecha());
        assertEquals(1, rv.getVentasConBono());
        ReporteVentas rt = new ReporteVentas(club, dia, dia);
        assertEquals(2000, rt.getIngresosTorneos(), 0.001);
    }

    @Test void huAdm07ReporteSinMovimientosEnRangoAjeno() {
        TorneoCompetitivo torneo = gestion.crearTorneoCompetitivo(admin, "Fuera", tenis,
                Torneo.Modalidad.INDIVIDUAL, null, "Intermedio", Torneo.Formato.ELIMINACION,
                5, dia, dia, 1000, 0.5);
        gestion.inscribirJugador(torneo, jugador());
        gestion.inscribirJugador(torneo, jugador());
        LocalDate otroDia = dia.plusDays(10);
        ReporteDeportivo deportivo = new ReporteDeportivo(club, otroDia, otroDia);
        ReporteVentas ventas = new ReporteVentas(club, otroDia, otroDia);
        assertEquals(0, deportivo.getTorneos());
        assertEquals(0, deportivo.getTorneosFinalizados());
        assertEquals(0, deportivo.getPremiosEnEfectivo(), 0.001);
        assertEquals(0, deportivo.getBonosOtorgados());
        assertEquals(0, ventas.getIngresosTorneos(), 0.001);
        assertEquals(0, ventas.getVentasConBono());
    }

    @Test void huAdm07ReporteSeparaTorneosAbiertosDeFinalizadosYRecaudo() {
        TorneoCompetitivo finalizado = gestion.crearTorneoCompetitivo(admin, "Finalizado", tenis,
                Torneo.Modalidad.INDIVIDUAL, null, "Intermedio", Torneo.Formato.ELIMINACION,
                5, dia, dia, 1500, 0.5);
        TorneoRecreativo abierto = gestion.crearTorneoRecreativo(admin, "Abierto", tenis,
                Torneo.Modalidad.INDIVIDUAL, null, "Intermedio", Torneo.Formato.ELIMINACION,
                5, dia, dia, 10);
        gestion.inscribirJugador(finalizado, jugador());
        gestion.inscribirJugador(finalizado, jugador());
        gestion.iniciarTorneo(admin, finalizado);
        gestion.registrarResultado(admin, finalizado, finalizado.getEncuentros().get(0), 6, 2);
        ReporteDeportivo deportivo = new ReporteDeportivo(club, dia, dia);
        ReporteVentas ventas = new ReporteVentas(club, dia, dia);
        assertEquals(2, deportivo.getTorneos());
        assertEquals(1, deportivo.getTorneosFinalizados());
        assertEquals(1, deportivo.getTorneosEnCursoOAbiertos());
        assertEquals(1500, deportivo.getPremiosEnEfectivo(), 0.001);
        assertEquals(0, deportivo.getBonosOtorgados());
        assertEquals(3000, ventas.getIngresosTorneos(), 0.001);
        assertEquals(0, ventas.getVentasConBono());
        assertFalse(abierto.estaFinalizado());
    }

}
