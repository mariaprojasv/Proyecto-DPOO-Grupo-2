package administracion;

import club.ClubDeportivoSeneca;
import java.time.LocalDate;
import usuarios.Administrador;
import usuarios.SolicitudCambioTurno;
import deportes.Categoria;
import deportes.Deporte;
import torneos.Encuentro;
import torneos.Torneo;
import torneos.TorneoCompetitivo;
import torneos.TorneoRecreativo;

public class InterfazAdministracion {
    private final ClubDeportivoSeneca club;
    private Administrador sesion;

    public InterfazAdministracion(ClubDeportivoSeneca club) {
        if (club == null) {
            throw new IllegalArgumentException("El club es obligatorio");
        }
        this.club = club;
    }

    public void iniciarSesion(String login, String password) {
        cerrarSesion();
        if (!(club.iniciarSesion(login, password) instanceof Administrador)) {
            throw new SecurityException("Credenciales de administrador invalidas");
        }
        sesion = (Administrador) club.buscarUsuario(login);
    }

    public void cerrarSesion() {
        sesion = null;
    }

    public boolean haySesionAdministrativa() {
        return sesion != null && club.buscarUsuario(sesion.getLogin()) == sesion;
    }

    private Administrador administradorAutorizado() {
        if (!haySesionAdministrativa()) {
            throw new SecurityException("Debe iniciar sesion como administrador");
        }
        return sesion;
    }

    public ReporteVentas consultarReporteVentas(LocalDate desde, LocalDate hasta) {
        administradorAutorizado();
        return new ReporteVentas(club, desde, hasta);
    }

    public ReporteDeportivo consultarReporteDeportivo(LocalDate desde, LocalDate hasta) {
        administradorAutorizado();
        return new ReporteDeportivo(club, desde, hasta);
    }

    public ReporteInventarios consultarReporteInventarios() {
        administradorAutorizado();
        return new ReporteInventarios(club);
    }

    public void aprobarSugerencia(SugerenciaSnack sugerencia) {
        club.getGestionSugerencias().aprobar(administradorAutorizado(), sugerencia);
    }

    public void rechazarSugerencia(SugerenciaSnack sugerencia, String motivo) {
        club.getGestionSugerencias().rechazar(administradorAutorizado(), sugerencia, motivo);
    }

    public void aprobarCambioTurno(SolicitudCambioTurno solicitud) {
        club.getGestionTurnos().aprobarCambio(administradorAutorizado(), solicitud);
    }

    public void rechazarCambioTurno(SolicitudCambioTurno solicitud) {
        club.getGestionTurnos().rechazarCambio(administradorAutorizado(), solicitud);
    }

    public TorneoRecreativo crearTorneoRecreativo(String nombre, Deporte deporte, Torneo.Modalidad modalidad,
            Categoria categoria, String nivel, Torneo.Formato formato, int cupos,
            LocalDate inicio, LocalDate fin, double porcentajeBono) {
        return club.getGestionTorneos().crearTorneoRecreativo(administradorAutorizado(), nombre, deporte, modalidad,
                categoria, nivel, formato, cupos, inicio, fin, porcentajeBono);
    }

    public TorneoCompetitivo crearTorneoCompetitivo(String nombre, Deporte deporte, Torneo.Modalidad modalidad,
            Categoria categoria, String nivel, Torneo.Formato formato, int cupos,
            LocalDate inicio, LocalDate fin, double tarifa, double porcentajePremio) {
        return club.getGestionTorneos().crearTorneoCompetitivo(administradorAutorizado(), nombre, deporte, modalidad,
                categoria, nivel, formato, cupos, inicio, fin, tarifa, porcentajePremio);
    }

    public void liberarCuposPreferenciales(Torneo torneo) {
        club.getGestionTorneos().liberarCuposPreferenciales(administradorAutorizado(), torneo);
    }

    public void iniciarTorneo(Torneo torneo) {
        club.getGestionTorneos().iniciarTorneo(administradorAutorizado(), torneo);
    }

    public void registrarResultadoTorneo(Torneo torneo, Encuentro encuentro, int marcadorA, int marcadorB) {
        club.getGestionTorneos().registrarResultado(administradorAutorizado(), torneo, encuentro, marcadorA, marcadorB);
    }
}
