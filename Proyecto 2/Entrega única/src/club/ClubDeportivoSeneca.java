package club;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.YearMonth;
import administracion.GestionSugerencias;
import academia.Academia;
import cafeteria.Cafeteria;
import deportes.AreaDeportiva;
import reservas.GestionReservas;
import Tienda.tienda;
import Tienda.GestionInventarios;
import usuarios.Usuario;
import usuarios.GestionTurnos;
import deportes.ControlSanciones;
import torneos.GestionTorneos;

public class ClubDeportivoSeneca {
    private String nombre;
    private final ArrayList<PagoMensualidad> mensualidades = new ArrayList<>();
    private ArrayList<Usuario> usuarios;
    private AreaDeportiva areaDeportiva;
    private Academia academia;
    private GestionReservas gestionReservas;
    private tienda tiendaClub;
    private GestionInventarios gestionInventarios;
    private Cafeteria cafeteria;
    private GestionTurnos gestionTurnos;
    private ControlSanciones controlSanciones;
    private GestionSugerencias gestionSugerencias;
    private GestionTorneos gestionTorneos;

    public ClubDeportivoSeneca(String nombre) {
        this.nombre = nombre;
        usuarios = new ArrayList<>();
        areaDeportiva = new AreaDeportiva();
        academia = new Academia();
        gestionReservas = new GestionReservas();
        tiendaClub = new tienda();
        gestionInventarios = new GestionInventarios();
        cafeteria = new Cafeteria();
        gestionSugerencias = new GestionSugerencias(cafeteria, this);
        gestionTurnos = new GestionTurnos(this);
        gestionTorneos = new GestionTorneos(this);
        controlSanciones = new ControlSanciones();
        areaDeportiva.establecerControlSanciones(controlSanciones);
    }

    public PagoMensualidad registrarMensualidad(usuarios.Socio socio, YearMonth periodo, LocalDate fecha, double valor) {
        if (socio == null || !usuarios.contains(socio)) {
            throw new IllegalArgumentException("El socio debe estar registrado en el club");
        }
        PagoMensualidad pago = new PagoMensualidad(socio, periodo, fecha, valor);
        for (PagoMensualidad existente : mensualidades) {
            if (existente.getSocio() == socio && existente.getPeriodo().equals(periodo)) {
                throw new IllegalStateException("La mensualidad del periodo ya fue registrada");
            }
        }
        mensualidades.add(pago);
        return pago;
    }

    public ArrayList<PagoMensualidad> getMensualidades() {
        return new ArrayList<>(mensualidades);
    }

    public String getNombre() {
	return nombre;
	}

    public ArrayList<Usuario> getUsuarios() {
	return new ArrayList<>(usuarios);
	}

    public AreaDeportiva getAreaDeportiva() {
	return areaDeportiva;
	}

    public Academia getAcademia() {
	return academia;
	}

    public GestionReservas getGestionReservas() {
	return gestionReservas;
	}

    public tienda getTienda() {
	return tiendaClub;
	}

    public GestionInventarios getGestionInventarios() {
        return gestionInventarios;
    }

    public Cafeteria getCafeteria() {
	return cafeteria;
	}

    public GestionSugerencias getGestionSugerencias() {
        return gestionSugerencias;
    }

    public ControlSanciones getControlSanciones() {
        return controlSanciones;
    }

    public GestionTorneos getGestionTorneos() {
        return gestionTorneos;
    }

    public GestionTurnos getGestionTurnos() {
        return gestionTurnos;
    }

    public boolean registrarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getLogin() == null || usuario.getLogin().isEmpty()) {
            return false;
        }
        if (buscarUsuario(usuario.getLogin()) != null) {
            return false;
        }
        usuarios.add(usuario);
        return true;
    }

    public Usuario buscarUsuario(String login) {
        if (login == null) {
            return null;
        }
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equalsIgnoreCase(login)) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario iniciarSesion(String login, String password) {
        Usuario usuario = buscarUsuario(login);
        if (usuario != null && password != null && usuario.autenticar(password)) {
            return usuario;
        }
        return null;
    }
}
