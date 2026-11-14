package torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import deportes.Categoria;
import deportes.Deporte;
import usuarios.Usuario;

/**
 * Torneo interno del club. Puede ser de deporte de conjunto (equipos) o de disciplina individual
 * (jugadores o parejas) y es recreativo o competitivo segun la subclase.
 *
 * Las operaciones que cambian el estado del torneo no son publicas: se hacen a traves de
 * {@link GestionTorneos}, que verifica quien actua y las reglas de participacion.
 */
public abstract class Torneo {
    public enum Modalidad { CONJUNTO, INDIVIDUAL, DOBLES }
    public enum Formato { LIGA, ELIMINACION }
    public enum Estado { EN_INSCRIPCION, EN_CURSO, FINALIZADO }
    public enum TipoCupo { PREFERENCIAL, REGULAR }

    /** Porcentaje de cupos reservado para los mejores del ranking o desempeno. */
    public static final double PORCENTAJE_PREFERENCIAL = 0.20;

    private final String nombre;
    private final Deporte deporte;
    private final Modalidad modalidad;
    private final Categoria categoria;
    private final String nivel;
    private final Formato formato;
    private final int cupos;
    private final int cuposPreferenciales;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private Estado estado = Estado.EN_INSCRIPCION;
    private boolean preferencialesLiberados;
    private int totalRondas;
    private final ArrayList<InscripcionTorneo> inscripciones = new ArrayList<>();
    private final ArrayList<Encuentro> encuentros = new ArrayList<>();

    protected Torneo(String nombre, Deporte deporte, Modalidad modalidad, Categoria categoria, String nivel,
            Formato formato, int cupos, LocalDate fechaInicio, LocalDate fechaFin) {
        if (nombre == null || nombre.trim().isEmpty() || deporte == null || modalidad == null || formato == null
                || fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("Datos del torneo invalidos");
        }
        if (modalidad == Modalidad.CONJUNTO && categoria == null) {
            throw new IllegalArgumentException("Un torneo de conjunto requiere una categoria");
        }
        if (modalidad != Modalidad.CONJUNTO && (nivel == null || nivel.trim().isEmpty())) {
            throw new IllegalArgumentException("Un torneo individual requiere un nivel");
        }
        if (cupos < 2) {
            throw new IllegalArgumentException("El torneo requiere al menos dos cupos");
        }
        this.nombre = nombre.trim();
        this.deporte = deporte;
        this.modalidad = modalidad;
        this.categoria = modalidad == Modalidad.CONJUNTO ? categoria : null;
        this.nivel = modalidad == Modalidad.CONJUNTO ? null : nivel.trim();
        this.formato = formato;
        this.cupos = cupos;
        this.cuposPreferenciales = (int) Math.ceil(cupos * PORCENTAJE_PREFERENCIAL - 1e-9);
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // ---- reglas propias de cada tipo de torneo ----

    public abstract boolean esCompetitivo();

    /** Tarifa que paga este competidor al inscribirse. */
    protected abstract double calcularTarifa(Competidor competidor);

    /** Entrega el premio a partir de las posiciones finales (ordenadas del primer puesto hacia abajo). */
    protected abstract void otorgarPremios(List<PosicionTorneo> posiciones);

    // ---- consultas ----

    public String getNombre() { return nombre; }
    public Deporte getDeporte() { return deporte; }
    public Modalidad getModalidad() { return modalidad; }
    public Categoria getCategoria() { return categoria; }
    public String getNivel() { return nivel; }
    public Formato getFormato() { return formato; }
    public int getCupos() { return cupos; }
    public int getCuposPreferenciales() { return cuposPreferenciales; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public Estado getEstado() { return estado; }
    public boolean estaFinalizado() { return estado == Estado.FINALIZADO; }
    public boolean haIniciado() { return estado != Estado.EN_INSCRIPCION; }
    public boolean sonPreferencialesLiberados() { return preferencialesLiberados; }
    public int getTotalRondas() { return totalRondas; }

    public String getCategoriaONivel() {
        return categoria != null ? categoria.getNombre() : nivel;
    }

    public ArrayList<InscripcionTorneo> getInscripciones() { return new ArrayList<>(inscripciones); }
    public ArrayList<Encuentro> getEncuentros() { return new ArrayList<>(encuentros); }

    public ArrayList<Competidor> getCompetidores() {
        ArrayList<Competidor> lista = new ArrayList<>();
        for (InscripcionTorneo i : inscripciones) {
            lista.add(i.getCompetidor());
        }
        return lista;
    }

    public ArrayList<Usuario> getParticipantes() {
        ArrayList<Usuario> lista = new ArrayList<>();
        for (InscripcionTorneo i : inscripciones) {
            for (Usuario u : i.getCompetidor().getIntegrantes()) {
                if (!lista.contains(u)) {
                    lista.add(u);
                }
            }
        }
        return lista;
    }

    public double getTotalRecaudado() {
        double total = 0;
        for (InscripcionTorneo i : inscripciones) {
            total += i.getTarifaCobrada();
        }
        return total;
    }

    private int contar(TipoCupo tipo) {
        int n = 0;
        for (InscripcionTorneo i : inscripciones) {
            if (i.getTipoCupo() == tipo) {
                n++;
            }
        }
        return n;
    }

    public int getCuposOcupados() { return inscripciones.size(); }
    public int getCuposDisponibles() { return cupos - inscripciones.size(); }

    /** Cupos reservados que todavia esperan a un jugador o equipo del ranking (0 una vez liberados). */
    public int getCuposPreferencialesDisponibles() {
        return preferencialesLiberados ? 0 : cuposPreferenciales - contar(TipoCupo.PREFERENCIAL);
    }

    /** Cupos abiertos a cualquier participante; incluye los preferenciales ya liberados. */
    public int getCuposRegularesDisponibles() {
        int reservados = preferencialesLiberados ? contar(TipoCupo.PREFERENCIAL) : cuposPreferenciales;
        return cupos - reservados - contar(TipoCupo.REGULAR);
    }

    public InscripcionTorneo buscarInscripcion(Usuario usuario) {
        for (InscripcionTorneo i : inscripciones) {
            if (i.getCompetidor().incluye(usuario)) {
                return i;
            }
        }
        return null;
    }

    public InscripcionTorneo buscarInscripcion(deportes.Equipo equipo) {
        for (InscripcionTorneo i : inscripciones) {
            if (i.getCompetidor() instanceof CompetidorEquipo
                    && ((CompetidorEquipo) i.getCompetidor()).getEquipo() == equipo) {
                return i;
            }
        }
        return null;
    }

    // ---- inscripcion ----

    InscripcionTorneo inscribir(Competidor competidor, boolean elegiblePreferente) {
        if (estado != Estado.EN_INSCRIPCION) {
            throw new IllegalStateException("Las inscripciones cerraron: el torneo ya inicio");
        }
        validarTipoDeCompetidor(competidor);
        for (Usuario u : competidor.getIntegrantes()) {
            if (buscarInscripcion(u) != null) {
                throw new IllegalStateException(u.getNombre() + " ya esta inscrito en este torneo");
            }
        }
        boolean hayPreferencial = !preferencialesLiberados && contar(TipoCupo.PREFERENCIAL) < cuposPreferenciales;
        boolean hayRegular = getCuposRegularesDisponibles() > 0;
        TipoCupo tipo;
        if (elegiblePreferente && hayPreferencial) {
            tipo = TipoCupo.PREFERENCIAL;
        } else if (hayRegular) {
            tipo = TipoCupo.REGULAR;
        } else {
            throw new IllegalStateException("No hay cupos disponibles para este participante");
        }
        InscripcionTorneo inscripcion = new InscripcionTorneo(competidor, tipo, calcularTarifa(competidor));
        inscripciones.add(inscripcion);
        return inscripcion;
    }

    private void validarTipoDeCompetidor(Competidor competidor) {
        if (competidor == null) {
            throw new IllegalArgumentException("El competidor es obligatorio");
        }
        boolean correcto = (modalidad == Modalidad.CONJUNTO && competidor instanceof CompetidorEquipo)
                || (modalidad == Modalidad.INDIVIDUAL && competidor instanceof CompetidorIndividual)
                || (modalidad == Modalidad.DOBLES && competidor instanceof CompetidorPareja);
        if (!correcto) {
            throw new IllegalArgumentException("El tipo de competidor no corresponde a la modalidad del torneo");
        }
    }

    /** Retira una inscripcion y libera su cupo. Solo antes de que el torneo inicie. */
    void retirar(InscripcionTorneo inscripcion) {
        if (estado != Estado.EN_INSCRIPCION) {
            throw new IllegalStateException("No es posible salirse: el torneo ya inicio");
        }
        if (inscripcion == null || !inscripciones.remove(inscripcion)) {
            throw new IllegalArgumentException("La inscripcion no existe en este torneo");
        }
    }

    /** Los cupos preferenciales sin ocupar pasan a ser regulares. */
    void liberarCuposPreferenciales() {
        if (estado != Estado.EN_INSCRIPCION) {
            throw new IllegalStateException("Solo se liberan cupos mientras hay inscripciones");
        }
        preferencialesLiberados = true;
    }

    // ---- desarrollo del torneo ----

    void iniciar() {
        if (estado != Estado.EN_INSCRIPCION) {
            throw new IllegalStateException("El torneo ya inicio");
        }
        if (inscripciones.size() < 2) {
            throw new IllegalStateException("Se requieren al menos dos inscritos para iniciar");
        }
        for (InscripcionTorneo i : inscripciones) {
            if (i.getCompetidor() instanceof CompetidorEquipo
                    && !((CompetidorEquipo) i.getCompetidor()).getEquipo().tieneMinimoIntegrantes()) {
                throw new IllegalStateException("El equipo " + i.getCompetidor().getNombre()
                        + " ya no tiene los jugadores minimos");
            }
        }
        preferencialesLiberados = true;
        estado = Estado.EN_CURSO;
        if (formato == Formato.LIGA) {
            generarLiga();
        } else {
            generarEliminacionInicial();
        }
    }

    void registrarResultado(Encuentro encuentro, int marcadorA, int marcadorB) {
        if (estado != Estado.EN_CURSO) {
            throw new IllegalStateException("Solo se registran resultados en un torneo en curso");
        }
        if (encuentro == null || !encuentros.contains(encuentro)) {
            throw new IllegalArgumentException("El encuentro no pertenece a este torneo");
        }
        if (marcadorA == marcadorB && (formato == Formato.ELIMINACION || sinEmpates())) {
            throw new IllegalArgumentException("Este encuentro no admite empates");
        }
        encuentro.registrar(marcadorA, marcadorB);
        if (formato == Formato.LIGA) {
            if (todosJugados(0)) {
                finalizar();
            }
        } else if (todosJugados(encuentro.getRonda())) {
            if (encuentro.getRonda() == totalRondas) {
                finalizar();
            } else {
                generarSiguienteRonda(encuentro.getRonda());
            }
        }
    }

    private boolean sinEmpates() {
        return deporte.getNombre() != null && deporte.getNombre().equalsIgnoreCase("Baloncesto");
    }

    private boolean todosJugados(int ronda) {
        for (Encuentro e : encuentros) {
            if ((ronda == 0 || e.getRonda() == ronda) && !e.estaJugado()) {
                return false;
            }
        }
        return true;
    }

    private void generarLiga() {
        ArrayList<Competidor> lista = getCompetidores();
        if (lista.size() % 2 != 0) {
            lista.add(null);
        }
        int n = lista.size();
        for (int ronda = 0; ronda < n - 1; ronda++) {
            for (int i = 0; i < n / 2; i++) {
                Competidor a = lista.get(i);
                Competidor b = lista.get(n - 1 - i);
                if (a != null && b != null) {
                    encuentros.add(new Encuentro(ronda + 1, a, b));
                }
            }
            lista.add(1, lista.remove(n - 1));
        }
        totalRondas = n - 1;
    }

    private void generarEliminacionInicial() {
        ArrayList<Competidor> semillas = new ArrayList<>();
        for (InscripcionTorneo i : inscripciones) {
            if (i.getTipoCupo() == TipoCupo.PREFERENCIAL) {
                semillas.add(i.getCompetidor());
            }
        }
        for (InscripcionTorneo i : inscripciones) {
            if (i.getTipoCupo() == TipoCupo.REGULAR) {
                semillas.add(i.getCompetidor());
            }
        }
        int n = semillas.size();
        int tamano = 1;
        while (tamano < n) {
            tamano <<= 1;
        }
        totalRondas = Integer.numberOfTrailingZeros(tamano);
        ArrayList<Integer> orden = new ArrayList<>();
        orden.add(1);
        while (orden.size() < tamano) {
            int suma = orden.size() * 2 + 1;
            ArrayList<Integer> siguiente = new ArrayList<>();
            for (int semilla : orden) {
                siguiente.add(semilla);
                siguiente.add(suma - semilla);
            }
            orden = siguiente;
        }
        for (int i = 0; i < tamano; i += 2) {
            int sa = orden.get(i);
            int sb = orden.get(i + 1);
            Competidor a = sa <= n ? semillas.get(sa - 1) : null;
            Competidor b = sb <= n ? semillas.get(sb - 1) : null;
            if (a == null) {
                a = b;
                b = null;
            }
            encuentros.add(new Encuentro(1, a, b));
        }
        if (todosJugados(1) && totalRondas > 1) {
            generarSiguienteRonda(1);
        }
    }

    private void generarSiguienteRonda(int rondaActual) {
        ArrayList<Competidor> ganadores = new ArrayList<>();
        for (Encuentro e : encuentros) {
            if (e.getRonda() == rondaActual) {
                ganadores.add(e.getGanador());
            }
        }
        for (int i = 0; i + 1 < ganadores.size(); i += 2) {
            encuentros.add(new Encuentro(rondaActual + 1, ganadores.get(i), ganadores.get(i + 1)));
        }
    }

    private void finalizar() {
        estado = Estado.FINALIZADO;
        otorgarPremios(getPosiciones());
    }

    // ---- posiciones ----

    /**
     * Posiciones del torneo. En liga se calculan con los resultados registrados (puntos del deporte,
     * diferencia y puntos a favor). En eliminacion el puesto 0 indica que el competidor sigue en carrera.
     */
    public ArrayList<PosicionTorneo> getPosiciones() {
        ArrayList<PosicionTorneo> filas = new ArrayList<>();
        for (InscripcionTorneo i : inscripciones) {
            filas.add(new PosicionTorneo(i.getCompetidor()));
        }
        if (!haIniciado()) {
            return filas;
        }
        for (Encuentro e : encuentros) {
            if (!e.estaJugado() || e.esBye()) {
                continue;
            }
            PosicionTorneo a = buscarFila(filas, e.getCompetidorA());
            PosicionTorneo b = buscarFila(filas, e.getCompetidorB());
            int ma = e.getMarcadorA();
            int mb = e.getMarcadorB();
            a.registrar(ma, mb, puntosPorMarcador(ma, mb));
            b.registrar(mb, ma, puntosPorMarcador(mb, ma));
        }
        if (formato == Formato.LIGA) {
            filas.sort(Comparator.comparingInt(PosicionTorneo::getPuntos).reversed()
                    .thenComparing(Comparator.comparingInt(PosicionTorneo::getDiferencia).reversed())
                    .thenComparing(Comparator.comparingInt(PosicionTorneo::getFavor).reversed()));
            for (int i = 0; i < filas.size(); i++) {
                filas.get(i).setPuesto(i + 1);
            }
        } else {
            asignarPuestosEliminacion(filas);
        }
        return filas;
    }

    private int puntosPorMarcador(int propios, int rivales) {
        if (propios > rivales) {
            return deporte.getPuntosGanar();
        }
        return propios == rivales ? deporte.getPuntosEmpate() : 0;
    }

    private PosicionTorneo buscarFila(ArrayList<PosicionTorneo> filas, Competidor competidor) {
        for (PosicionTorneo fila : filas) {
            if (fila.getCompetidor() == competidor) {
                return fila;
            }
        }
        throw new IllegalStateException("Competidor sin inscripcion");
    }

    private void asignarPuestosEliminacion(ArrayList<PosicionTorneo> filas) {
        for (Encuentro e : encuentros) {
            Competidor perdedor = e.getPerdedor();
            if (perdedor != null) {
                buscarFila(filas, perdedor).setPuesto((1 << (totalRondas - e.getRonda())) + 1);
            }
        }
        if (estado == Estado.FINALIZADO) {
            for (Encuentro e : encuentros) {
                if (e.getRonda() == totalRondas && e.getGanador() != null) {
                    buscarFila(filas, e.getGanador()).setPuesto(1);
                }
            }
        }
        // del primer puesto hacia abajo; los que siguen en carrera (0) quedan al final
        filas.sort(Comparator.comparingInt(f -> f.getPuesto() == 0 ? Integer.MAX_VALUE : f.getPuesto()));
    }

    // ---- historial ----

    /** Historial legible con participantes, encuentros, posiciones y premios, sin importar el estado. */
    public String getHistorialDetallado() {
        StringBuilder sb = new StringBuilder();
        sb.append("TORNEO ").append(nombre).append(esCompetitivo() ? " (competitivo)" : " (recreativo)").append('\n');
        sb.append("Deporte: ").append(deporte.getNombre()).append(" | ").append(modalidad)
                .append(" | ").append(categoria != null ? "Categoria " : "Nivel ").append(getCategoriaONivel()).append('\n');
        sb.append("Formato: ").append(formato).append(" | Estado: ").append(estado)
                .append(" | Fechas: ").append(fechaInicio.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-uuuu"))).append(" a ").append(fechaFin.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-uuuu"))).append('\n');
        sb.append("Cupos: ").append(getCuposOcupados()).append('/').append(cupos)
                .append(" (preferenciales reservados: ").append(cuposPreferenciales).append(")\n");
        sb.append("Participantes:\n");
        for (InscripcionTorneo i : inscripciones) {
            sb.append("  - ").append(i.getCompetidor().getNombre()).append(" [").append(i.getTipoCupo()).append("]\n");
        }
        sb.append("Encuentros:\n");
        for (Encuentro e : encuentros) {
            sb.append("  R").append(e.getRonda()).append(": ").append(e.getCompetidorA().getNombre());
            if (e.esBye()) {
                sb.append(" avanza sin rival\n");
            } else {
                sb.append(" vs ").append(e.getCompetidorB().getNombre()).append(" -> ")
                        .append(e.estaJugado() ? e.getMarcadorA() + "-" + e.getMarcadorB() : "pendiente").append('\n');
            }
        }
        sb.append("Posiciones:\n");
        for (PosicionTorneo p : getPosiciones()) {
            sb.append("  ").append(p.getPuesto() == 0 ? "-" : String.valueOf(p.getPuesto())).append(". ")
                    .append(p.getCompetidor().getNombre()).append(" (").append(p.getPuntos()).append(" pts, PJ ")
                    .append(p.getJugados()).append(")\n");
        }
        sb.append(describirPremios());
        return sb.toString();
    }

    protected abstract String describirPremios();

    // ---- restauracion desde almacenamiento ----

    public void restaurarInscripcion(Competidor competidor, TipoCupo tipo, double tarifa) {
        validarTipoDeCompetidor(competidor);
        inscripciones.add(new InscripcionTorneo(competidor, tipo, tarifa));
    }

    public Encuentro restaurarEncuentro(int ronda, Competidor a, Competidor b, int marcadorA, int marcadorB, boolean jugado) {
        Encuentro e = new Encuentro(ronda, a, b);
        e.restaurar(marcadorA, marcadorB, jugado);
        encuentros.add(e);
        return e;
    }

    public void restaurarEstado(Estado estado, boolean preferencialesLiberados, int totalRondas) {
        if (estado == null || totalRondas < 0) {
            throw new IllegalArgumentException("Estado de torneo invalido");
        }
        this.estado = estado;
        this.preferencialesLiberados = preferencialesLiberados;
        this.totalRondas = totalRondas;
    }
}
