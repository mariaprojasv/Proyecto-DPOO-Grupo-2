package club;

import java.util.ArrayList;
import academia.Academia;
import cafeteria.Cafeteria;
import deportes.AreaDeportiva;
import reservas.GestionReservas;
import Tienda.tienda;
import usuarios.Usuario;
import usuarios.GestionTurnos;
import deportes.ControlSanciones;

public class ClubDeportivoSeneca {
    private String nombre;
    private ArrayList<Usuario> usuarios;
    private AreaDeportiva areaDeportiva;
    private Academia academia;
    private GestionReservas gestionReservas;
    private tienda tiendaClub;
    private Cafeteria cafeteria;
    private GestionTurnos gestionTurnos;
    private ControlSanciones controlSanciones;

    public ClubDeportivoSeneca(String nombre) {
        this.nombre = nombre;
        usuarios = new ArrayList<>();
        areaDeportiva = new AreaDeportiva();
        academia = new Academia();
        gestionReservas = new GestionReservas();
        tiendaClub = new tienda();
        cafeteria = new Cafeteria();
        gestionTurnos = new GestionTurnos();
        controlSanciones = new ControlSanciones();
    }

    public String getNombre() {
	return nombre;
	}

    public ArrayList<Usuario> getUsuarios() {
	return usuarios;
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

    public Cafeteria getCafeteria() {
	return cafeteria;
	}

    public ControlSanciones getControlSanciones() {
        return controlSanciones;
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
