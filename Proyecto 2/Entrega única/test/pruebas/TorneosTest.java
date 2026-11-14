package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import Tienda.Articulo;
import Tienda.CodigoDescuento;
import Tienda.Venta;
import academia.InscripcionIndividual;
import club.ClubDeportivoSeneca;
import deportes.Categoria;
import deportes.Deporte;
import deportes.Equipo;
import persistencia.PersistenciaArchivos;
import torneos.*;
import usuarios.*;

@DisplayName("Torneos internos recreativos y competitivos")
class TorneosTest {
    @TempDir Path carpeta;
    private ClubDeportivoSeneca club;
    private Administrador admin;
    private GestionTorneos gestion;
    private Deporte tenis;
    private Deporte futbol;
    private Categoria sub15;
    private LocalDate inicio;
    private LocalDate fin;
    private int contador;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Club Deportivo Victoria");
        admin = new Administrador("admin", "clave", "Admin");
        club.registrarUsuario(admin);
        gestion = club.getGestionTorneos();
        tenis = new Deporte("Tenis", 1, 3, 0);
        futbol = new Deporte("Futbol", 2, 3, 1);
        sub15 = new Categoria("Sub-15", 10, 15);
        club.getAreaDeportiva().agregarDeporte(tenis);
        club.getAreaDeportiva().agregarDeporte(futbol);
        club.getAreaDeportiva().agregarCategoria(sub15);
        inicio = LocalDate.of(2026, 12, 5); // sabado
        fin = LocalDate.of(2026, 12, 5);
    }

    private Jugador jugador() {
        contador++;
        Jugador j = new Jugador("j" + contador, "p", "Jugador " + contador, 0, LocalDate.of(2012, 1, 1));
        club.registrarUsuario(j);
        return j;
    }

    private TorneoRecreativo recreativoTenis(int cupos) {
        return gestion.crearTorneoRecreativo(admin, "Recreativo", tenis, Torneo.Modalidad.INDIVIDUAL, null,
                "Intermedio", Torneo.Formato.ELIMINACION, cupos, inicio, fin, 15);
    }

    private TorneoCompetitivo competitivoTenis(int cupos) {
        return gestion.crearTorneoCompetitivo(admin, "Competitivo", tenis, Torneo.Modalidad.INDIVIDUAL, null,
                "Intermedio", Torneo.Formato.ELIMINACION, cupos, inicio, fin, 50000, 0.8);
    }

    @Test void reservaVeintePorCientoRedondeadoHaciaArriba() {
        assertEquals(2, recreativoTenis(10).getCuposPreferenciales());
        TorneoCompetitivo t = competitivoTenis(11);
        assertEquals(3, t.getCuposPreferenciales());
    }

    @Test void soloUnAdministradorRegistradoCreaTorneos() {
        Administrador intruso = new Administrador("otro", "x", "Otro");
        assertThrows(SecurityException.class, () -> gestion.crearTorneoRecreativo(intruso, "X", tenis,
                Torneo.Modalidad.INDIVIDUAL, null, "Intermedio", Torneo.Formato.ELIMINACION, 4, inicio, fin, 10));
    }

    @Test void variosTorneosSimultaneosYNombresUnicos() {
        recreativoTenis(8);
        competitivoTenis(8);
        assertEquals(2, gestion.getTorneosAbiertos().size());
        assertThrows(IllegalStateException.class, () -> recreativoTenis(8));
    }

    @Test void socioRegistradoPuedeInscribirseEnTorneoIndividual() {
        TorneoRecreativo torneo = recreativoTenis(4);
        Socio socio = new Socio("socio1", "clave", "Socio Prueba", 0);
        assertTrue(club.registrarUsuario(socio));
        assertNotNull(gestion.inscribirJugador(torneo, socio));
        assertEquals(1, torneo.getCuposOcupados());
        assertTrue(torneo.getHistorialDetallado().contains("Socio Prueba"));
    }

    @Test void inscribirseYSalirseLiberaElCupoAntesDeIniciar() {
        TorneoRecreativo t = recreativoTenis(4);
        Jugador a = jugador();
        gestion.inscribirJugador(t, a);
        assertEquals(3, t.getCuposDisponibles());
        gestion.retirarJugador(t, a);
        assertEquals(4, t.getCuposDisponibles());
        assertNotNull(gestion.inscribirJugador(t, a));
    }

    @Test void noSePuedeSalirNiInscribirseConElTorneoIniciado() {
        TorneoRecreativo t = recreativoTenis(4);
        Jugador a = jugador();
        Jugador b = jugador();
        gestion.inscribirJugador(t, a);
        gestion.inscribirJugador(t, b);
        gestion.iniciarTorneo(admin, t);
        assertThrows(IllegalStateException.class, () -> gestion.retirarJugador(t, a));
        assertThrows(IllegalStateException.class, () -> gestion.inscribirJugador(t, jugador()));
    }

    @Test void cuposPreferencialesSoloParaElMejorRankingYSeLiberan() {
        TorneoRecreativo t = recreativoTenis(5); // 1 preferencial, 4 regulares
        Jugador estrella = jugador();
        club.getAcademia().inscribirJugador(estrella, tenis, "Intermedio");
        club.getAcademia().actualizarRanking(club.getAcademia().consultarInscripciones(estrella).get(0), 100);
        for (int i = 0; i < 4; i++) {
            gestion.inscribirJugador(t, jugador());
        }
        assertEquals(0, t.getCuposRegularesDisponibles());
        assertThrows(IllegalStateException.class, () -> gestion.inscribirJugador(t, jugador()));
        assertEquals(TorneoCompetitivo.TipoCupo.PREFERENCIAL, gestion.inscribirJugador(t, estrella).getTipoCupo());
        assertEquals(0, t.getCuposDisponibles());
    }

    @Test void cuposPreferencialesSinUsarPasanARegulares() {
        TorneoRecreativo t = recreativoTenis(5);
        for (int i = 0; i < 4; i++) {
            gestion.inscribirJugador(t, jugador());
        }
        assertEquals(1, t.getCuposPreferencialesDisponibles());
        gestion.liberarCuposPreferenciales(admin, t);
        assertEquals(0, t.getCuposPreferencialesDisponibles());
        assertEquals(1, t.getCuposRegularesDisponibles());
        gestion.inscribirJugador(t, jugador());
        assertEquals(0, t.getCuposDisponibles());
    }

    @Test void estudiantesMatriculadosYExternosParticipanPeroNoLosNoMatriculados() {
        TorneoRecreativo t = recreativoTenis(8);
        Estudiante ok = new Estudiante("est1", "p", "Estudiante", "2020", true);
        Estudiante no = new Estudiante("est2", "p", "Retirado", "2019", false);
        PersonaExterna ext = new PersonaExterna("ext1", "p", "Externo");
        club.registrarUsuario(ok);
        club.registrarUsuario(no);
        club.registrarUsuario(ext);
        gestion.inscribirJugador(t, ok);
        gestion.inscribirJugador(t, ext);
        assertThrows(IllegalStateException.class, () -> gestion.inscribirJugador(t, no));
        assertThrows(IllegalArgumentException.class, () -> gestion.inscribirJugador(t,
                new PersonaExterna("fantasma", "p", "No registrado")));
    }

    @Test void empleadoDeTurnoElDiaDelTorneoNoPuedeInscribirse() {
        TorneoRecreativo t = recreativoTenis(8);
        Entrenador entrenador = new Entrenador("ent1", "p", "Entrenador", "Tenis");
        club.registrarUsuario(entrenador);
        club.getGestionTurnos().agregarTurno(entrenador, LocalTime.of(8, 0), LocalTime.of(12, 0),
                EnumSet.of(DayOfWeek.SATURDAY));
        assertThrows(IllegalStateException.class, () -> gestion.inscribirJugador(t, entrenador));
        Fisioterapeuta fisio = new Fisioterapeuta("fis1", "p", "Fisio");
        club.registrarUsuario(fisio);
        club.getGestionTurnos().agregarTurno(fisio, LocalTime.of(8, 0), LocalTime.of(12, 0),
                EnumSet.of(DayOfWeek.MONDAY));
        assertNotNull(gestion.inscribirJugador(t, fisio));
    }

    @Test void empleadosNoParticipanEnTorneosDeConjunto() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Conjunto", futbol, Torneo.Modalidad.CONJUNTO,
                sub15, null, Torneo.Formato.LIGA, 4, inicio, fin, 10);
        Equipo equipo = club.getAreaDeportiva().crearEquipo("Aguilas", futbol, sub15, 5);
        Entrenador entrenador = new Entrenador("ent2", "p", "Entrenador", "Futbol");
        club.registrarUsuario(entrenador);
        assertThrows(IllegalStateException.class, () -> gestion.inscribirJugador(t, entrenador));
        assertThrows(SecurityException.class, () -> gestion.inscribirEquipo(t, equipo, entrenador));
    }

    @Test void empleadoEnTorneoCompetitivoNoPagaNiRecibePremio() {
        TorneoCompetitivo t = competitivoTenis(4);
        Entrenador entrenador = new Entrenador("ent3", "p", "Entrenador", "Tenis");
        club.registrarUsuario(entrenador);
        Jugador a = jugador();
        Jugador b = jugador();
        assertEquals(0, gestion.inscribirJugador(t, entrenador).getTarifaCobrada(), 0.001);
        gestion.inscribirJugador(t, a);
        gestion.inscribirJugador(t, b);
        assertEquals(100000, t.getTotalRecaudado(), 0.001);
        assertEquals(80000, t.getPremioEnEfectivo(), 0.001);
        gestion.iniciarTorneo(admin, t);
        ArrayList<Encuentro> ronda1 = t.getEncuentros();
        // gana siempre el entrenador
        for (Encuentro e : ronda1) {
            if (e.estaJugado()) {
                continue;
            }
            boolean ganaA = e.getCompetidorA().incluye(entrenador) || !e.getCompetidorB().incluye(entrenador);
            gestion.registrarResultado(admin, t, e, ganaA ? 6 : 0, ganaA ? 0 : 6);
        }
        while (!t.estaFinalizado()) {
            for (Encuentro e : t.getEncuentros()) {
                if (!e.estaJugado()) {
                    boolean ganaA = e.getCompetidorA().incluye(entrenador) || !e.getCompetidorB().incluye(entrenador);
                    gestion.registrarResultado(admin, t, e, ganaA ? 6 : 0, ganaA ? 0 : 6);
                    break;
                }
            }
        }
        assertTrue(t.getPremiosEntregados().size() == 1);
        assertNotSame(entrenador, t.getPremiosEntregados().get(0).getBeneficiario());
        assertEquals(80000, t.getPremiosEntregados().get(0).getMonto(), 0.001);
    }

    @Test void eliminacionConByesDefineCampeonYPosiciones() {
        TorneoRecreativo t = recreativoTenis(8);
        ArrayList<Jugador> js = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Jugador j = jugador();
            js.add(j);
            gestion.inscribirJugador(t, j);
        }
        assertEquals(Torneo.Estado.EN_INSCRIPCION, t.getEstado());
        gestion.iniciarTorneo(admin, t);
        assertEquals(Torneo.Estado.EN_CURSO, t.getEstado());
        assertEquals(3, t.getTotalRondas());
        assertEquals(1, gestion.getTorneosEnCurso().size());
        while (!t.estaFinalizado()) {
            for (Encuentro e : t.getEncuentros()) {
                if (!e.estaJugado()) {
                    gestion.registrarResultado(admin, t, e, 2, 1);
                    break;
                }
            }
        }
        ArrayList<PosicionTorneo> pos = t.getPosiciones();
        assertEquals(1, pos.get(0).getPuesto());
        assertEquals(2, pos.get(1).getPuesto());
        assertEquals(3, pos.get(2).getPuesto());
        assertEquals(Torneo.Estado.FINALIZADO, t.getEstado());
        assertEquals(1, gestion.getTorneosFinalizados().size());
        assertTrue(t.getHistorialDetallado().contains("FINALIZADO"));
        assertThrows(IllegalStateException.class, () -> gestion.retirarJugador(t, js.get(0)));
    }

    @Test void eliminacionNoAdmiteEmpates() {
        TorneoRecreativo t = recreativoTenis(4);
        gestion.inscribirJugador(t, jugador());
        gestion.inscribirJugador(t, jugador());
        gestion.iniciarTorneo(admin, t);
        Encuentro e = t.getEncuentros().get(0);
        assertThrows(IllegalArgumentException.class, () -> gestion.registrarResultado(admin, t, e, 1, 1));
    }

    @Test void bonoDelCampeonSeGuardaYSeUsaUnaVezSinAcumularse() {
        TorneoRecreativo t = recreativoTenis(5);
        Jugador campeon = jugador();
        Jugador otro = jugador();
        gestion.inscribirJugador(t, campeon);
        gestion.inscribirJugador(t, otro);
        gestion.iniciarTorneo(admin, t);
        Encuentro final_ = t.getEncuentros().get(0);
        boolean campeonEsA = final_.getCompetidorA().incluye(campeon);
        gestion.registrarResultado(admin, t, final_, campeonEsA ? 6 : 0, campeonEsA ? 0 : 6);
        assertEquals(1, campeon.getBonosDisponibles().size());
        assertTrue(otro.getBonos().isEmpty());
        BonoDescuento bono = campeon.getBonosDisponibles().get(0);
        assertEquals(15, bono.getPorcentaje(), 0.001);

        Venta venta = new Venta(campeon);
        venta.agregarDetalle(new Articulo("Raqueta", "Tenis", 100000, 3), 1);
        venta.aplicarBono(bono);
        assertThrows(IllegalStateException.class, () -> venta.aplicarCodigo(new CodigoDescuento("X", 10)));
        assertThrows(IllegalStateException.class, () -> venta.redimirPuntosEnVenta(1));
        assertEquals(15000, venta.getDescuento(), 0.001);
        venta.finalizarVenta();
        assertFalse(bono.estaDisponible());
        Venta otra = new Venta(campeon);
        assertThrows(IllegalArgumentException.class, () -> otra.aplicarBono(bono));
    }

    @Test void bonoNoSePuedeUsarEnVentaDeOtraPersonaNiConOtroDescuento() {
        BonoDescuento bono = new BonoDescuento(jugador(), 10, "Torneo");
        Jugador otro = jugador();
        Venta venta = new Venta(otro);
        assertThrows(IllegalArgumentException.class, () -> venta.aplicarBono(null));
        assertThrows(SecurityException.class, () -> venta.aplicarBono(bono));
        Venta conCodigo = new Venta(bono.getTitular());
        conCodigo.aplicarCodigo(new CodigoDescuento("Y", 5));
        assertThrows(IllegalStateException.class, () -> conCodigo.aplicarBono(bono));
    }

    @Test void bonoSePuedeRedimirEnLaCafeteria() {
        Jugador j = jugador();
        club.getCafeteria().agregarProducto(new cafeteria.snack("Barra", 1000, 5, "ninguno"));
        BonoDescuento bono = new BonoDescuento(j, 20, "Torneo");
        j.guardarBono(bono);
        cafeteria.VentaCafeteria v = club.getCafeteria().registrarVentaConBono(j, "Barra", 2, 0, bono);
        assertEquals(1600 * 1.08, v.getTotal(), 0.001);
        assertFalse(bono.estaDisponible());
    }

    @Test void huSoc06BonoNoSeAcumulaSiPrimeroSeRedimenPuntos() {
        Socio socio = new Socio("socioPuntos", "clave", "Socio Puntos", 100);
        club.registrarUsuario(socio);
        BonoDescuento bono = new BonoDescuento(socio, 15, "Torneo recreativo");
        socio.guardarBono(bono);
        Venta venta = new Venta(socio);
        venta.agregarDetalle(new Articulo("Raqueta", "Tenis", 1000, 3), 1);
        venta.redimirPuntosEnVenta(10);
        assertThrows(IllegalStateException.class, () -> venta.aplicarBono(bono));
        assertTrue(bono.estaDisponible());
        assertNull(venta.getBonoAplicado());
    }

    @Test void huSoc06CafeteriaRechazaBonoAjenoYBonoConsumido() {
        Jugador titular = jugador();
        Jugador otro = jugador();
        club.getCafeteria().agregarProducto(new cafeteria.snack("BarraSegura", 1000, 6, "ninguno"));
        BonoDescuento bono = new BonoDescuento(titular, 20, "Torneo recreativo");
        titular.guardarBono(bono);
        assertThrows(IllegalArgumentException.class, () ->
                club.getCafeteria().registrarVentaConBono(otro, "BarraSegura", 1, 0, bono));
        assertTrue(bono.estaDisponible());
        club.getCafeteria().registrarVentaConBono(titular, "BarraSegura", 1, 0, bono);
        assertFalse(bono.estaDisponible());
        assertThrows(IllegalArgumentException.class, () ->
                club.getCafeteria().registrarVentaConBono(titular, "BarraSegura", 1, 0, bono));
    }

    @Test void ligaDeEquiposConTablaYPremioDeBono() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Mini", futbol, Torneo.Modalidad.CONJUNTO,
                sub15, null, Torneo.Formato.LIGA, 5, inicio, fin, 10);
        ArrayList<Equipo> equipos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Equipo e = club.getAreaDeportiva().crearEquipo("Eq" + i, futbol, sub15, 5);
            Jugador j1 = jugador();
            Jugador j2 = jugador();
            club.getAreaDeportiva().inscribirJugadorEnEquipo(j1, e, "Delantero", 9);
            club.getAreaDeportiva().inscribirJugadorEnEquipo(j2, e, "Portero", 1);
            equipos.add(e);
            gestion.inscribirEquipo(t, e, j1);
        }
        assertThrows(IllegalStateException.class, () -> gestion.inscribirEquipo(t, equipos.get(0), equipos.get(0).getJugadores().get(0)));
        gestion.iniciarTorneo(admin, t);
        assertEquals(3, t.getEncuentros().size());
        for (Encuentro e : t.getEncuentros()) {
            gestion.registrarResultado(admin, t, e, e.getCompetidorA().getNombre().compareTo(e.getCompetidorB().getNombre()) < 0 ? 2 : 0, 0);
        }
        assertTrue(t.estaFinalizado());
        assertEquals("Eq0", t.getPosiciones().get(0).getCompetidor().getNombre());
        assertEquals(2, ((TorneoRecreativo) t).getBonosOtorgados().size());
        assertEquals(1, equipos.get(0).getJugadores().get(0).getBonosDisponibles().size());
    }

    @Test void equipoSoloLoInscribeUnJugadorDeEseEquipo() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Conj", futbol, Torneo.Modalidad.CONJUNTO,
                sub15, null, Torneo.Formato.LIGA, 4, inicio, fin, 10);
        Equipo e = club.getAreaDeportiva().crearEquipo("A", futbol, sub15, 5);
        Jugador j1 = jugador();
        Jugador j2 = jugador();
        club.getAreaDeportiva().inscribirJugadorEnEquipo(j1, e, "Delantero", 9);
        club.getAreaDeportiva().inscribirJugadorEnEquipo(j2, e, "Portero", 1);
        assertThrows(SecurityException.class, () -> gestion.inscribirEquipo(t, e, jugador()));
        gestion.inscribirEquipo(t, e, j1);
        gestion.retirarEquipo(t, e, j2);
        assertEquals(4, t.getCuposDisponibles());
    }

    @Test void dobles() {
        TorneoCompetitivo t = gestion.crearTorneoCompetitivo(admin, "Dobles", tenis, Torneo.Modalidad.DOBLES, null,
                "Avanzado", Torneo.Formato.ELIMINACION, 4, inicio, fin, 10000, 0.5);
        Jugador a = jugador();
        Jugador b = jugador();
        Jugador c = jugador();
        InscripcionTorneo ins = gestion.inscribirPareja(t, a, b);
        assertEquals(20000, ins.getTarifaCobrada(), 0.001);
        assertThrows(IllegalStateException.class, () -> gestion.inscribirPareja(t, b, c));
        gestion.retirarJugador(t, b);
        assertEquals(4, t.getCuposDisponibles());
        assertNotNull(gestion.inscribirPareja(t, b, c));
    }

    @Test void premioCompetitivoDoblesSeDivideEntreAmbosCampeones() {
        TorneoCompetitivo t = gestion.crearTorneoCompetitivo(admin, "Premio Dobles", tenis,
                Torneo.Modalidad.DOBLES, null, "Intermedio", Torneo.Formato.ELIMINACION,
                4, inicio, fin, 10000, 0.5);
        Jugador a = jugador(), b = jugador(), c = jugador(), d = jugador();
        gestion.inscribirPareja(t, a, b);
        gestion.inscribirPareja(t, c, d);
        assertEquals(40000, t.getTotalRecaudado(), 0.001);
        gestion.iniciarTorneo(admin, t);
        Encuentro final_ = t.getEncuentros().get(0);
        boolean parejaABesA = final_.getCompetidorA().incluye(a);
        gestion.registrarResultado(admin, t, final_, parejaABesA ? 6 : 0, parejaABesA ? 0 : 6);
        assertTrue(t.estaFinalizado());
        assertEquals(2, t.getPremiosEntregados().size());
        assertEquals(10000, t.getPremiosEntregados().get(0).getMonto(), 0.001);
        assertEquals(10000, t.getPremiosEntregados().get(1).getMonto(), 0.001);
        assertTrue(t.getPremiosEntregados().stream().anyMatch(p -> p.getBeneficiario() == a));
        assertTrue(t.getPremiosEntregados().stream().anyMatch(p -> p.getBeneficiario() == b));
    }

    @Test void premioDoblesParejaMixtaNoEntregaEfectivoAlEmpleado() {
        TorneoCompetitivo t = gestion.crearTorneoCompetitivo(admin, "Dobles Mixtos", tenis,
                Torneo.Modalidad.DOBLES, null, "Intermedio", Torneo.Formato.ELIMINACION,
                4, inicio, fin, 10000, 0.5);
        Entrenador empleado = new Entrenador("entPremio", "p", "Entrenador", "Tenis");
        club.registrarUsuario(empleado);
        Jugador socio = jugador(), rival1 = jugador(), rival2 = jugador();
        gestion.inscribirPareja(t, empleado, socio);
        gestion.inscribirPareja(t, rival1, rival2);
        assertEquals(30000, t.getTotalRecaudado(), 0.001);
        gestion.iniciarTorneo(admin, t);
        Encuentro final_ = t.getEncuentros().get(0);
        boolean mixtaEsA = final_.getCompetidorA().incluye(empleado);
        gestion.registrarResultado(admin, t, final_, mixtaEsA ? 6 : 0, mixtaEsA ? 0 : 6);
        assertTrue(t.estaFinalizado());
        assertEquals(1, t.getPremiosEntregados().size());
        assertSame(socio, t.getPremiosEntregados().get(0).getBeneficiario());
        assertEquals(15000, t.getPremiosEntregados().get(0).getMonto(), 0.001);
    }

    @Test void huSoc05EquipoCampeonEntregaBonoAEstudianteYExterno() {
        TorneoRecreativo torneo = gestion.crearTorneoRecreativo(admin, "Bono mixto", futbol,
                Torneo.Modalidad.CONJUNTO, sub15, null, Torneo.Formato.LIGA,
                4, inicio, fin, 20);
        Equipo mixto = club.getAreaDeportiva().crearEquipo("Mixto", futbol, sub15, 4);
        Equipo rival = club.getAreaDeportiva().crearEquipo("Rival", futbol, sub15, 4);
        Estudiante estudiante = new Estudiante("estBono", "p", "Estudiante", "2026", true);
        PersonaExterna externo = new PersonaExterna("extBono", "p", "Externo");
        club.registrarUsuario(estudiante);
        club.registrarUsuario(externo);
        club.getAreaDeportiva().inscribirIntegranteExterno(estudiante, mixto);
        club.getAreaDeportiva().inscribirIntegranteExterno(externo, mixto);
        Jugador rival1 = jugador();
        Jugador rival2 = jugador();
        club.getAreaDeportiva().inscribirJugadorEnEquipo(rival1, rival, "Delantero", 9);
        club.getAreaDeportiva().inscribirJugadorEnEquipo(rival2, rival, "Portero", 1);
        gestion.inscribirEquipo(torneo, mixto, estudiante);
        gestion.inscribirEquipo(torneo, rival, rival1);
        gestion.iniciarTorneo(admin, torneo);
        Encuentro encuentro = torneo.getEncuentros().get(0);
        boolean mixtoEsA = encuentro.getCompetidorA().getNombre().equals("Mixto");
        gestion.registrarResultado(admin, torneo, encuentro,
                mixtoEsA ? 2 : 0, mixtoEsA ? 0 : 2);
        assertTrue(torneo.estaFinalizado());
        assertEquals(2, torneo.getBonosOtorgados().size());
        assertEquals(1, estudiante.getBonosDisponibles().size());
        assertEquals(1, externo.getBonosDisponibles().size());
        assertEquals(20, estudiante.getBonosDisponibles().get(0).getPorcentaje(), 0.001);
        assertEquals(20, externo.getBonosDisponibles().get(0).getPorcentaje(), 0.001);
        assertTrue(rival1.getBonos().isEmpty());
        assertTrue(rival2.getBonos().isEmpty());
    }

    @Test void huJug07RankingDiezSeisTresAlFinalizarLigaIndividual() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Liga ranking", tenis,
                Torneo.Modalidad.INDIVIDUAL, null, "Intermedio", Torneo.Formato.LIGA,
                4, inicio, fin, 15);
        Jugador primero = jugador(), segundo = jugador(), tercero = jugador();
        for (Jugador j : new Jugador[]{primero, segundo, tercero}) {
            club.getAcademia().inscribirJugador(j, tenis, "Intermedio");
            gestion.inscribirJugador(t, j);
        }
        gestion.iniciarTorneo(admin, t);
        for (Encuentro e : t.getEncuentros()) {
            String a = e.getCompetidorA().getNombre();
            String b = e.getCompetidorB().getNombre();
            int prioridadA = a.equals(primero.getNombre()) ? 3 : a.equals(segundo.getNombre()) ? 2 : 1;
            int prioridadB = b.equals(primero.getNombre()) ? 3 : b.equals(segundo.getNombre()) ? 2 : 1;
            gestion.registrarResultado(admin, t, e, prioridadA > prioridadB ? 2 : 0,
                    prioridadA > prioridadB ? 0 : 2);
        }
        assertTrue(t.estaFinalizado());
        assertEquals(10, club.getAcademia().consultarInscripciones(primero).get(0).getRanking());
        assertEquals(6, club.getAcademia().consultarInscripciones(segundo).get(0).getRanking());
        assertEquals(3, club.getAcademia().consultarInscripciones(tercero).get(0).getRanking());
    }

    @Test void huJug07FinalizaSinCrearInscripcionAcademia() {
        TorneoRecreativo t = recreativoTenis(4);
        Jugador a = jugador(), b = jugador();
        gestion.inscribirJugador(t, a);
        gestion.inscribirJugador(t, b);
        gestion.iniciarTorneo(admin, t);
        for (Encuentro e : t.getEncuentros()) {
            if (!e.estaJugado() && !e.esBye()) gestion.registrarResultado(admin, t, e, 2, 0);
        }
        assertTrue(t.estaFinalizado());
        assertTrue(club.getAcademia().consultarInscripciones(a).isEmpty());
        assertTrue(club.getAcademia().consultarInscripciones(b).isEmpty());
    }

    @Test void persistenciaConservaTorneosBonosYTurnos() throws Exception {
        TorneoRecreativo t = recreativoTenis(5);
        Jugador campeon = jugador();
        Jugador otro = jugador();
        gestion.inscribirJugador(t, campeon);
        gestion.inscribirJugador(t, otro);
        gestion.iniciarTorneo(admin, t);
        Encuentro e = t.getEncuentros().get(0);
        gestion.registrarResultado(admin, t, e, 6, 3);
        TorneoCompetitivo c = competitivoTenis(4);
        Estudiante est = new Estudiante("est9", "p", "Est", "99", true);
        club.registrarUsuario(est);
        gestion.inscribirJugador(c, est);
        Entrenador ent = new Entrenador("ent9", "p", "Ent", "Tenis");
        club.registrarUsuario(ent);
        club.getGestionTurnos().agregarTurno(ent, LocalTime.of(8, 0), LocalTime.of(10, 0), EnumSet.of(DayOfWeek.MONDAY));

        PersistenciaArchivos p = new PersistenciaArchivos(carpeta.toString());
        p.guardar(club);
        ClubDeportivoSeneca nuevo = new ClubDeportivoSeneca("Club Deportivo Victoria");
        p.cargar(nuevo);
        assertEquals(2, nuevo.getGestionTorneos().getTorneos().size());
        Torneo r = nuevo.getGestionTorneos().buscarTorneo("Recreativo");
        assertTrue(r.estaFinalizado());
        assertEquals(1, r.getPosiciones().get(0).getPuesto());
        assertEquals(1, ((TorneoRecreativo) r).getBonosOtorgados().size());
        Usuario ganador = r.getPosiciones().get(0).getCompetidor().getIntegrantes().get(0);
        assertEquals(1, nuevo.buscarUsuario(ganador.getLogin()).getBonosDisponibles().size());
        Torneo comp = nuevo.getGestionTorneos().buscarTorneo("Competitivo");
        assertEquals(Torneo.Estado.EN_INSCRIPCION, comp.getEstado());
        assertEquals(50000, comp.getTotalRecaudado(), 0.001);
        assertTrue(nuevo.buscarUsuario("est9") instanceof Estudiante);
        Entrenador entCargado = (Entrenador) nuevo.buscarUsuario("ent9");
        assertTrue(nuevo.getGestionTurnos().estaDeTurno(entCargado, LocalDate.of(2026, 12, 7)));
        assertFalse(nuevo.getGestionTurnos().estaDeTurno(entCargado, LocalDate.of(2026, 12, 8)));
    }

    @Test void huJug05DoblesSoloEsPreferencialSiAmbosEstanEnRanking() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Ranking dobles", tenis,
                Torneo.Modalidad.DOBLES, null, "Intermedio", Torneo.Formato.ELIMINACION,
                5, inicio, fin, 15);
        Jugador primero = jugador();
        Jugador segundo = jugador();
        Jugador sinRanking = jugador();
        club.getAcademia().inscribirJugador(primero, tenis, "Intermedio");
        club.getAcademia().inscribirJugador(segundo, tenis, "Intermedio");
        club.getAcademia().actualizarRanking(club.getAcademia().consultarInscripciones(primero).get(0), 100);
        club.getAcademia().actualizarRanking(club.getAcademia().consultarInscripciones(segundo).get(0), 90);
        // Con cinco cupos solo hay uno preferencial; la pareja debe tener a ambos en el top 1.
        InscripcionTorneo parejaMixta = gestion.inscribirPareja(t, primero, sinRanking);
        assertEquals(Torneo.TipoCupo.REGULAR, parejaMixta.getTipoCupo());
        assertEquals(1, t.getCuposPreferencialesDisponibles());
        // Ninguna pareja puede ser preferencial si solo hay una persona en el top 1.
        assertEquals(Torneo.TipoCupo.REGULAR,
                gestion.inscribirPareja(t, segundo, jugador()).getTipoCupo());
    }

    @Test void huJug05DoblesAmbosDestacadosObtienenCupoPreferencial() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Ranking dobles 2", tenis,
                Torneo.Modalidad.DOBLES, null, "Intermedio", Torneo.Formato.ELIMINACION,
                10, inicio, fin, 15); // 2 cupos preferenciales
        Jugador primero = jugador();
        Jugador segundo = jugador();
        club.getAcademia().inscribirJugador(primero, tenis, "Intermedio");
        club.getAcademia().inscribirJugador(segundo, tenis, "Intermedio");
        club.getAcademia().actualizarRanking(club.getAcademia().consultarInscripciones(primero).get(0), 100);
        club.getAcademia().actualizarRanking(club.getAcademia().consultarInscripciones(segundo).get(0), 90);
        InscripcionTorneo inscripcion = gestion.inscribirPareja(t, primero, segundo);
        assertEquals(Torneo.TipoCupo.PREFERENCIAL, inscripcion.getTipoCupo());
        assertEquals(1, t.getCuposPreferencialesDisponibles());
    }

    @Test void huJug05EquipoConMejorDesempenoObtieneCupoPreferencial() {
        TorneoRecreativo torneo = gestion.crearTorneoRecreativo(admin, "Ranking equipos", futbol,
                Torneo.Modalidad.CONJUNTO, sub15, null, Torneo.Formato.LIGA,
                5, inicio, fin, 10); // 4 regulares y 1 preferencial
        Equipo destacado = club.getAreaDeportiva().crearEquipo("Destacado", futbol, sub15, 4);
        Equipo regular = club.getAreaDeportiva().crearEquipo("Regular", futbol, sub15, 4);
        Jugador a = jugador(), b = jugador(), c = jugador(), d = jugador();
        club.getAreaDeportiva().inscribirJugadorEnEquipo(a, destacado, "Delantero", 9);
        club.getAreaDeportiva().inscribirJugadorEnEquipo(b, destacado, "Portero", 1);
        club.getAreaDeportiva().inscribirJugadorEnEquipo(c, regular, "Delantero", 9);
        club.getAreaDeportiva().inscribirJugadorEnEquipo(d, regular, "Portero", 1);

        deportes.PartidoInterno partido = new deportes.PartidoInterno(inicio, futbol, destacado, regular);
        assertTrue(partido.registrarMarcador(2, 0));
        club.getAreaDeportiva().getPartidos().add(partido);

        assertEquals(destacado, gestion.mejoresEquipos(torneo, 1).get(0));
        InscripcionTorneo inscripcionRegular = gestion.inscribirEquipo(torneo, regular, c);
        assertEquals(Torneo.TipoCupo.REGULAR, inscripcionRegular.getTipoCupo());
        InscripcionTorneo inscripcionDestacada = gestion.inscribirEquipo(torneo, destacado, a);
        assertEquals(Torneo.TipoCupo.PREFERENCIAL, inscripcionDestacada.getTipoCupo());
        assertEquals(0, torneo.getCuposPreferencialesDisponibles());
    }


    @Test void huSoc03FiltraTorneosPorEstadoSinMezclarCategorias() {
        TorneoRecreativo abierto = recreativoTenis(4);
        TorneoCompetitivo enCurso = competitivoTenis(4);
        gestion.inscribirJugador(enCurso, jugador());
        gestion.inscribirJugador(enCurso, jugador());
        gestion.iniciarTorneo(admin, enCurso);
        assertEquals(1, gestion.getTorneosPorEstado(Torneo.Estado.EN_INSCRIPCION).size());
        assertTrue(gestion.getTorneosPorEstado(Torneo.Estado.EN_INSCRIPCION).contains(abierto));
        assertEquals(1, gestion.getTorneosPorEstado(Torneo.Estado.EN_CURSO).size());
        assertTrue(gestion.getTorneosPorEstado(Torneo.Estado.EN_CURSO).contains(enCurso));
        assertTrue(gestion.getTorneosPorEstado(Torneo.Estado.FINALIZADO).isEmpty());
        Encuentro e = enCurso.getEncuentros().get(0);
        gestion.registrarResultado(admin, enCurso, e, 2, 1);
        assertTrue(gestion.getTorneosPorEstado(Torneo.Estado.EN_CURSO).isEmpty());
        assertTrue(gestion.getTorneosPorEstado(Torneo.Estado.FINALIZADO).contains(enCurso));
    }

    @Test void huSoc03HistorialInscripcionMuestraParticipantesYCupos() {
        TorneoRecreativo t = recreativoTenis(4);
        Jugador participante = jugador();
        gestion.inscribirJugador(t, participante);
        String historial = gestion.consultarHistorial(t);
        assertTrue(historial.contains("EN_INSCRIPCION"));
        assertTrue(historial.contains("Participantes:"));
        assertTrue(historial.contains(participante.getNombre()));
        assertTrue(historial.contains("Cupos: 1/4"));
        assertTrue(historial.contains("preferenciales reservados:"));
    }

    @Test void huSoc03HistorialEnCursoDistingueJugadosYPendientes() {
        TorneoRecreativo t = recreativoTenis(4);
        // De 4 cupos, 1 se reserva para participantes preferenciales: hay 3 regulares.
        for (int i = 0; i < 3; i++) gestion.inscribirJugador(t, jugador());
        gestion.iniciarTorneo(admin, t);
        String antes = gestion.consultarHistorial(t);
        assertTrue(antes.contains("EN_CURSO"));
        assertTrue(antes.contains("pendiente"));
        Encuentro primero = null;
        for (Encuentro e : t.getEncuentros()) {
            if (!e.estaJugado()) { primero = e; break; }
        }
        assertNotNull(primero);
        gestion.registrarResultado(admin, t, primero, 2, 1);
        String despues = gestion.consultarHistorial(t);
        assertTrue(despues.contains("2-1"));
        assertTrue(despues.contains("pendiente"));
        assertTrue(despues.contains("EN_CURSO"));
    }

    @Test void huSoc03HistorialFinalizadoMuestraMarcadoresPosicionesYPremios() {
        TorneoCompetitivo t = competitivoTenis(4);
        Jugador primero = jugador();
        Jugador segundo = jugador();
        gestion.inscribirJugador(t, primero);
        gestion.inscribirJugador(t, segundo);
        gestion.iniciarTorneo(admin, t);
        Encuentro encuentro = t.getEncuentros().get(0);
        gestion.registrarResultado(admin, t, encuentro, 3, 1);
        assertTrue(t.estaFinalizado());
        String historial = gestion.consultarHistorial(t);
        assertTrue(historial.contains("FINALIZADO"));
        assertTrue(historial.contains("3-1"));
        assertTrue(historial.contains("Posiciones:"));
        assertTrue(historial.contains("1."));
        assertTrue(historial.contains("2."));
        assertTrue(historial.contains("Bolsa del premio:"));
        assertTrue(historial.contains("Premio para"));
    }



    @Test void huEnt03TurnoEnSegundoDiaImpideInscripcion() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Dos dias", tenis,
                Torneo.Modalidad.INDIVIDUAL, null, "Intermedio", Torneo.Formato.ELIMINACION,
                8, inicio, inicio.plusDays(1), 15);
        Entrenador empleado = new Entrenador("entDom", "p", "Entrenador Domingo", "Tenis");
        club.registrarUsuario(empleado);
        assertTrue(club.getGestionTurnos().agregarTurno(empleado, LocalTime.of(9, 0),
                LocalTime.of(11, 0), EnumSet.of(DayOfWeek.SUNDAY)));
        assertThrows(IllegalStateException.class, () -> gestion.inscribirJugador(t, empleado));
        assertEquals(0, t.getCuposOcupados());
    }

    @Test void huEnt03EntrenadorSinTurnoPuedeInscribirseEnDoblesSinPagar() {
        TorneoCompetitivo t = gestion.crearTorneoCompetitivo(admin, "Dobles Empleados", tenis,
                Torneo.Modalidad.DOBLES, null, "Intermedio", Torneo.Formato.ELIMINACION,
                4, inicio, fin, 10000, 0.5);
        Entrenador entrenador = new Entrenador("entDobles", "p", "Entrenador Dobles", "Tenis");
        club.registrarUsuario(entrenador);
        Jugador companero = jugador();
        InscripcionTorneo inscripcion = gestion.inscribirPareja(t, entrenador, companero);
        assertNotNull(inscripcion);
        assertEquals(10000, inscripcion.getTarifaCobrada(), 0.001);
        assertEquals(1, t.getCuposOcupados());
    }

    @Test void huJug06ParejaDeEstudianteMatriculadoYExternoEsAdmitida() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Dobles Externos", tenis,
                Torneo.Modalidad.DOBLES, null, "Intermedio", Torneo.Formato.ELIMINACION,
                4, inicio, fin, 15);
        Estudiante estudiante = new Estudiante("estDobles", "p", "Estudiante Dobles", "E123", true);
        PersonaExterna externo = new PersonaExterna("extDobles", "p", "Externo Dobles");
        club.registrarUsuario(estudiante);
        club.registrarUsuario(externo);
        InscripcionTorneo inscripcion = gestion.inscribirPareja(t, estudiante, externo);
        assertTrue(inscripcion.getCompetidor().incluye(estudiante));
        assertTrue(inscripcion.getCompetidor().incluye(externo));
        assertEquals(1, t.getCuposOcupados());
    }

    @Test void huJug06RechazaParejaConEstudianteNoMatriculadoSinOcuparCupo() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Dobles Sin Matricula", tenis,
                Torneo.Modalidad.DOBLES, null, "Intermedio", Torneo.Formato.ELIMINACION,
                4, inicio, fin, 15);
        Estudiante retirado = new Estudiante("estRetirado", "p", "Estudiante Retirado", "E124", false);
        PersonaExterna externo = new PersonaExterna("extRetirado", "p", "Externo Inscrito");
        club.registrarUsuario(retirado);
        club.registrarUsuario(externo);
        assertThrows(IllegalStateException.class, () -> gestion.inscribirPareja(t, externo, retirado));
        assertEquals(0, t.getCuposOcupados());
        assertEquals(4, t.getCuposDisponibles());
    }

}
