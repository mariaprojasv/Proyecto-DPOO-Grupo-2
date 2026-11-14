package torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import academia.InscripcionIndividual;
import club.ClubDeportivoSeneca;
import deportes.Categoria;
import deportes.Deporte;
import deportes.Equipo;
import deportes.Partido;
import deportes.PartidoInterno;
import usuarios.Administrador;
import usuarios.Empleado;
import usuarios.Entrenador;
import usuarios.Estudiante;
import usuarios.Fisioterapeuta;
import usuarios.Jugador;
import usuarios.PersonaExterna;
import usuarios.Socio;
import usuarios.Usuario;

/** Administra los torneos internos del club: creacion, inscripciones, desarrollo y consultas. */
public class GestionTorneos {
    private final ClubDeportivoSeneca club;
    private final ArrayList<Torneo> torneos = new ArrayList<>();

    public GestionTorneos(ClubDeportivoSeneca club) {
        if (club == null) {
            throw new IllegalArgumentException("El club es obligatorio");
        }
        this.club = club;
    }

    // ---------- creacion (solo administrador) ----------

    /**
     * Crea un torneo recreativo. Para CONJUNTO se indica la categoria (nivel = null); para INDIVIDUAL y
     * DOBLES se indica el nivel (categoria = null).
     */
    public TorneoRecreativo crearTorneoRecreativo(Administrador admin, String nombre, Deporte deporte,
            Torneo.Modalidad modalidad, Categoria categoria, String nivel, Torneo.Formato formato, int cupos,
            LocalDate fechaInicio, LocalDate fechaFin, double porcentajeBono) {
        verificarAdministrador(admin);
        validarCreacion(nombre, deporte);
        TorneoRecreativo torneo = new TorneoRecreativo(nombre, deporte, modalidad, categoria, nivel, formato,
                cupos, fechaInicio, fechaFin, porcentajeBono);
        torneos.add(torneo);
        return torneo;
    }

    /** Crea un torneo competitivo con tarifa de inscripcion y premio sobre el total recaudado. */
    public TorneoCompetitivo crearTorneoCompetitivo(Administrador admin, String nombre, Deporte deporte,
            Torneo.Modalidad modalidad, Categoria categoria, String nivel, Torneo.Formato formato, int cupos,
            LocalDate fechaInicio, LocalDate fechaFin, double tarifaInscripcion, double porcentajePremio) {
        verificarAdministrador(admin);
        validarCreacion(nombre, deporte);
        TorneoCompetitivo torneo = new TorneoCompetitivo(nombre, deporte, modalidad, categoria, nivel, formato,
                cupos, fechaInicio, fechaFin, tarifaInscripcion, porcentajePremio);
        torneos.add(torneo);
        return torneo;
    }

    private void validarCreacion(String nombre, Deporte deporte) {
        if (nombre == null || buscarTorneo(nombre) != null) {
            throw new IllegalStateException("Ya existe un torneo con ese nombre");
        }
        if (deporte == null || !club.getAreaDeportiva().getDeportes().contains(deporte)) {
            throw new IllegalArgumentException("El deporte no pertenece al area deportiva");
        }
    }

    // ---------- inscripciones ----------

    /** Inscribe a un participante en un torneo individual. */
    public InscripcionTorneo inscribirJugador(Torneo torneo, Usuario usuario) {
        exigirTorneo(torneo);
        if (torneo.getModalidad() != Torneo.Modalidad.INDIVIDUAL) {
            throw new IllegalStateException("Este torneo no es individual");
        }
        validarParticipante(torneo, usuario);
        Competidor competidor = new CompetidorIndividual(usuario);
        return torneo.inscribir(competidor, esPreferente(torneo, competidor));
    }

    /** Inscribe una pareja en un torneo de dobles. */
    public InscripcionTorneo inscribirPareja(Torneo torneo, Usuario primero, Usuario segundo) {
        exigirTorneo(torneo);
        if (torneo.getModalidad() != Torneo.Modalidad.DOBLES) {
            throw new IllegalStateException("Este torneo no es de dobles");
        }
        if (primero == null || segundo == null || primero == segundo) {
            throw new IllegalArgumentException("Una pareja requiere dos participantes distintos");
        }
        validarParticipante(torneo, primero);
        validarParticipante(torneo, segundo);
        Competidor competidor = new CompetidorPareja(primero, segundo);
        return torneo.inscribir(competidor, esPreferente(torneo, competidor));
    }

    /** Inscribe un equipo completo; lo solicita uno de sus jugadores. */
    public InscripcionTorneo inscribirEquipo(Torneo torneo, Equipo equipo, Usuario solicitante) {
        exigirTorneo(torneo);
        if (torneo.getModalidad() != Torneo.Modalidad.CONJUNTO) {
            throw new IllegalStateException("Este torneo no es de deporte de conjunto");
        }
        if (equipo == null || !club.getAreaDeportiva().getEquipos().contains(equipo)) {
            throw new IllegalArgumentException("El equipo no pertenece al area deportiva");
        }
        if (solicitante == null || !equipo.getIntegrantes().contains(solicitante)) {
            throw new SecurityException("Solo un integrante del equipo puede inscribirlo");
        }
        if (equipo.getDeporte() != torneo.getDeporte() || equipo.getCategoria() != torneo.getCategoria()) {
            throw new IllegalStateException("El equipo no corresponde al deporte o la categoria del torneo");
        }
        if (!equipo.tieneMinimoIntegrantes()) {
            throw new IllegalStateException("El equipo no esta completo");
        }
        for (Usuario externo : equipo.getIntegrantesExternos()) {
            if (club.buscarUsuario(externo.getLogin()) != externo
                    || (externo instanceof Estudiante && !((Estudiante) externo).estaMatriculado())) {
                throw new IllegalStateException(externo.getNombre() + " no puede participar: no esta registrado o matriculado");
            }
        }
        Competidor competidor = new CompetidorEquipo(equipo);
        return torneo.inscribir(competidor, esPreferente(torneo, competidor));
    }

    /** El participante (o un integrante de su pareja) se sale antes de que inicie; se libera el cupo. */
    public void retirarJugador(Torneo torneo, Usuario usuario) {
        exigirTorneo(torneo);
        InscripcionTorneo inscripcion = torneo.buscarInscripcion(usuario);
        if (inscripcion == null || inscripcion.getCompetidor() instanceof CompetidorEquipo) {
            throw new IllegalStateException("El usuario no tiene una inscripcion individual en este torneo");
        }
        torneo.retirar(inscripcion);
    }

    public void retirarEquipo(Torneo torneo, Equipo equipo, Usuario solicitante) {
        exigirTorneo(torneo);
        InscripcionTorneo inscripcion = torneo.buscarInscripcion(equipo);
        if (inscripcion == null) {
            throw new IllegalStateException("El equipo no esta inscrito en este torneo");
        }
        if (solicitante == null || !inscripcion.getCompetidor().incluye(solicitante)) {
            throw new SecurityException("Solo un jugador del equipo puede retirarlo");
        }
        torneo.retirar(inscripcion);
    }

    private void validarParticipante(Torneo torneo, Usuario usuario) {
        if (usuario == null || club.buscarUsuario(usuario.getLogin()) != usuario) {
            throw new IllegalArgumentException("El participante debe estar registrado en el club");
        }
        if (usuario instanceof Jugador || usuario instanceof PersonaExterna || usuario instanceof Socio) {
            return;
        }
        if (usuario instanceof Estudiante) {
            if (!((Estudiante) usuario).estaMatriculado()) {
                throw new IllegalStateException("El estudiante no esta matriculado en la universidad");
            }
            return;
        }
        if (usuario instanceof Entrenador || usuario instanceof Fisioterapeuta) {
            for (LocalDate dia = torneo.getFechaInicio(); !dia.isAfter(torneo.getFechaFin()); dia = dia.plusDays(1)) {
                if (club.getGestionTurnos().estaDeTurno((Empleado) usuario, dia)) {
                    throw new IllegalStateException("El empleado esta de turno el " + dia);
                }
            }
            return;
        }
        throw new IllegalStateException("Este tipo de usuario no puede participar en torneos");
    }

    // ---------- cupos preferenciales ----------

    /**
     * Un competidor es preferencial si esta entre los mejores del ranking de la disciplina y el nivel
     * (individual; en dobles lo deben ser ambos) o entre los equipos con mejor desempeno de la categoria.
     * Cada torneo reserva tantos puestos como cupos preferenciales tiene.
     */
    boolean esPreferente(Torneo torneo, Competidor competidor) {
        int n = torneo.getCuposPreferenciales();
        if (competidor instanceof CompetidorEquipo) {
            return mejoresEquipos(torneo, n).contains(((CompetidorEquipo) competidor).getEquipo());
        }
        List<Usuario> mejores = mejoresJugadores(torneo, n);
        for (Usuario u : competidor.getIntegrantes()) {
            if (!mejores.contains(u)) {
                return false;
            }
        }
        return true;
    }

    public List<Usuario> mejoresJugadores(Torneo torneo, int cantidad) {
        List<Usuario> resultado = new ArrayList<>();
        if (torneo.getModalidad() == Torneo.Modalidad.CONJUNTO) {
            return resultado;
        }
        for (InscripcionIndividual i : club.getAcademia().consultarRanking(torneo.getDeporte())) {
            if (resultado.size() >= cantidad) {
                break;
            }
            if (i.getRanking() > 0 && i.getNivel().equalsIgnoreCase(torneo.getNivel())) {
                resultado.add(i.getJugador());
            }
        }
        return resultado;
    }

    public List<Equipo> mejoresEquipos(Torneo torneo, int cantidad) {
        List<Equipo> equipos = new ArrayList<>();
        List<Integer> puntos = new ArrayList<>();
        for (Equipo e : club.getAreaDeportiva().getEquipos()) {
            if (e.getDeporte() == torneo.getDeporte() && e.getCategoria() == torneo.getCategoria()) {
                equipos.add(e);
                puntos.add(puntosDeDesempeno(e));
            }
        }
        List<Equipo> resultado = new ArrayList<>();
        while (resultado.size() < cantidad && !equipos.isEmpty()) {
            int mejor = 0;
            for (int i = 1; i < equipos.size(); i++) {
                if (puntos.get(i) > puntos.get(mejor)) {
                    mejor = i;
                }
            }
            if (puntos.get(mejor) <= 0) {
                break;
            }
            resultado.add(equipos.remove(mejor));
            puntos.remove(mejor);
        }
        return resultado;
    }

    private int puntosDeDesempeno(Equipo equipo) {
        int total = 0;
        for (Partido p : club.getAreaDeportiva().getPartidos()) {
            if (!p.tieneResultadoRegistrado()) {
                continue;
            }
            String resultado = p.getResultado();
            if (p.getEquipoClub() == equipo) {
                total += p.getDeporte().getPuntosPorResultado(resultado);
            } else if (p instanceof PartidoInterno && ((PartidoInterno) p).getRival() == equipo) {
                String inverso = resultado.equals("VICTORIA") ? "DERROTA" : (resultado.equals("DERROTA") ? "VICTORIA" : resultado);
                total += p.getDeporte().getPuntosPorResultado(inverso);
            }
        }
        return total;
    }

    // ---------- desarrollo del torneo (solo administrador) ----------

    public void liberarCuposPreferenciales(Administrador admin, Torneo torneo) {
        verificarAdministrador(admin);
        exigirTorneo(torneo);
        torneo.liberarCuposPreferenciales();
    }

    public void iniciarTorneo(Administrador admin, Torneo torneo) {
        verificarAdministrador(admin);
        exigirTorneo(torneo);
        torneo.iniciar();
    }

    public void registrarResultado(Administrador admin, Torneo torneo, Encuentro encuentro, int marcadorA, int marcadorB) {
        verificarAdministrador(admin);
        exigirTorneo(torneo);
        boolean yaTerminado = torneo.estaFinalizado();
        torneo.registrarResultado(encuentro, marcadorA, marcadorB);
        if (!yaTerminado && torneo.estaFinalizado()) {
            actualizarRankingPorResultado(torneo);
        }
    }

    /** Puntos de ranking de la academia segun el puesto: 1.o 10, 2.o 6, 3.o 3 (solo disciplinas individuales). */
    public static final int[] PUNTOS_RANKING = {10, 6, 3};

    private void actualizarRankingPorResultado(Torneo torneo) {
        if (torneo.getModalidad() == Torneo.Modalidad.CONJUNTO) {
            return;
        }
        for (PosicionTorneo posicion : torneo.getPosiciones()) {
            int puesto = posicion.getPuesto();
            if (puesto < 1 || puesto > PUNTOS_RANKING.length) {
                continue;
            }
            for (Usuario u : posicion.getCompetidor().getIntegrantes()) {
                if (!(u instanceof Jugador)) {
                    continue;
                }
                for (InscripcionIndividual ins : club.getAcademia().consultarInscripciones((Jugador) u)) {
                    if (ins.getDeporte() == torneo.getDeporte()) {
                        club.getAcademia().actualizarRanking(ins, PUNTOS_RANKING[puesto - 1]);
                    }
                }
            }
        }
    }

    // ---------- consultas ----------

    public ArrayList<Torneo> getTorneos() { return new ArrayList<>(torneos); }

    public Torneo buscarTorneo(String nombre) {
        if (nombre == null) {
            return null;
        }
        for (Torneo t : torneos) {
            if (t.getNombre().equalsIgnoreCase(nombre.trim())) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Torneo> getTorneosPorEstado(Torneo.Estado estado) {
        ArrayList<Torneo> lista = new ArrayList<>();
        for (Torneo t : torneos) {
            if (t.getEstado() == estado) {
                lista.add(t);
            }
        }
        return lista;
    }

    public ArrayList<Torneo> getTorneosAbiertos() { return getTorneosPorEstado(Torneo.Estado.EN_INSCRIPCION); }
    public ArrayList<Torneo> getTorneosEnCurso() { return getTorneosPorEstado(Torneo.Estado.EN_CURSO); }
    public ArrayList<Torneo> getTorneosFinalizados() { return getTorneosPorEstado(Torneo.Estado.FINALIZADO); }

    /** Historial detallado de un torneo en cualquier estado. */
    public String consultarHistorial(Torneo torneo) {
        exigirTorneo(torneo);
        return torneo.getHistorialDetallado();
    }

    /** Solo para restaurar torneos guardados. */
    public void restaurarTorneo(Torneo torneo) {
        if (torneo == null || torneos.contains(torneo) || buscarTorneo(torneo.getNombre()) != null) {
            throw new IllegalArgumentException("Torneo restaurado invalido o repetido");
        }
        torneos.add(torneo);
    }

    private void exigirTorneo(Torneo torneo) {
        if (torneo == null || !torneos.contains(torneo)) {
            throw new IllegalArgumentException("El torneo no existe");
        }
    }

    private void verificarAdministrador(Administrador administrador) {
        if (administrador == null || club.buscarUsuario(administrador.getLogin()) != administrador) {
            throw new SecurityException("Solo un administrador puede gestionar torneos");
        }
    }
}
