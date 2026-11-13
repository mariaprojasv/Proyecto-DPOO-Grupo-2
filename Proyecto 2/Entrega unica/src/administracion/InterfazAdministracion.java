package administracion;

import club.ClubDeportivoSeneca;
import java.time.LocalDate;
import usuarios.Administrador;
import usuarios.SolicitudCambioTurno;

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
}
