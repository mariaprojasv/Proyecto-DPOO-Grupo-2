package deportes;

import java.time.LocalDate;
import java.util.ArrayList;
import usuarios.Jugador;
import usuarios.Usuario;

public class AreaDeportiva {
	private ArrayList<Deporte> deportes;
	private ArrayList<Categoria> categorias;
	private ArrayList<Equipo> equipos;
	private ArrayList<Partido> partidos;
	private ArrayList<Competencia> competencias;
    private ControlSanciones controlSanciones;

	public AreaDeportiva() {
		this.deportes = new ArrayList<>();
		this.categorias = new ArrayList<>();
		this.equipos = new ArrayList<>();
		this.partidos = new ArrayList<>();
		this.competencias = new ArrayList<>();
	}

	public ArrayList<Deporte> getDeportes() {
	return deportes;
	}
	public ArrayList<Categoria> getCategorias() {
	return categorias;
	}
	public ArrayList<Equipo> getEquipos() {
	return equipos;
	}
	public ArrayList<Partido> getPartidos() {
	return partidos;
	}
	public ArrayList<Competencia> getCompetencias() {
	return competencias;
	}

	public void agregarDeporte(Deporte d) {

	    if (d != null && !deportes.contains(d)) {
            deportes.add(d);
        }

	}

	public void agregarCategoria(Categoria c) {

	    if (c != null && !categorias.contains(c)) {
            categorias.add(c);
        }

	}

	public Equipo crearEquipo(String nombre, Deporte deporte, Categoria categoria, int cupoMaximo) {
		Equipo e = new Equipo(nombre, cupoMaximo, deporte, categoria);
		equipos.add(e);
		return e;
	}

	public ArrayList<Equipo> getEquiposDeJugador(Jugador j) {
		ArrayList<Equipo> resultado = new ArrayList<>();
		for (Equipo e : equipos) {
			if (e.getJugadores().contains(j)) {
				resultado.add(e);
			}
		}
		return resultado;
	}

	public boolean inscribirJugadorEnEquipo(Jugador j, Equipo equipo, String posicion, int numero) {
        try {
            inscribirJugadorEnEquipoValidado(j, equipo, posicion, numero);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }

    public void inscribirJugadorEnEquipoValidado(Jugador j, Equipo equipo, String posicion, int numero) {
        if (j == null || equipo == null) {
            throw new IllegalArgumentException("El jugador y el equipo son obligatorios");
        }
        if (!equipos.contains(equipo)) {
            throw new IllegalArgumentException("El equipo no pertenece al area deportiva");
        }
        for (Equipo e : getEquiposDeJugador(j)) {
            if (e.getDeporte() == equipo.getDeporte()) {
                throw new IllegalStateException("El jugador ya pertenece a un equipo de este deporte");
            }
        }
        equipo.agregarJugadorValidado(j, posicion, numero);
    }

    /** Une a un estudiante matriculado o persona externa a un equipo (un equipo por deporte). */
    public void inscribirIntegranteExterno(Usuario usuario, Equipo equipo) {
        if (usuario == null || equipo == null || !equipos.contains(equipo)) {
            throw new IllegalArgumentException("El integrante y el equipo son obligatorios");
        }
        for (Equipo e : equipos) {
            if (e.getDeporte() == equipo.getDeporte() && e.getIntegrantes().contains(usuario)) {
                throw new IllegalStateException("El integrante ya pertenece a un equipo de este deporte");
            }
        }
        equipo.agregarIntegranteExternoValidado(usuario);
    }

	public void darDeBajaEquipo(Equipo e) {

	    equipos.remove(e);

	}

	public void establecerControlSanciones(ControlSanciones control) {
        controlSanciones = control;
    }


	public PartidoInterno programarPartidoInterno(LocalDate fecha, Equipo local, Equipo visitante) {
		if (fecha == null || local == null || visitante == null || local == visitante
                || !equipos.contains(local) || !equipos.contains(visitante)
                || local.getDeporte() != visitante.getDeporte() || local.getCategoria() != visitante.getCategoria()) {
			return null;
		}
		PartidoInterno p = new PartidoInterno(fecha, local.getDeporte(), local, visitante);
        p.establecerControlSanciones(controlSanciones);
        if (!p.puedeJugarse() || !tieneMinimoHabilitados(local) || !tieneMinimoHabilitados(visitante)) {
			return null;
		}
		partidos.add(p);
		return p;
	}

    private boolean tieneMinimoHabilitados(Equipo equipo) {
        int habilitados = 0;
        for (Jugador jugador : equipo.getJugadores()) {
            if (controlSanciones == null || !controlSanciones.estaSuspendido(jugador)) {
                habilitados++;
            }
        }
        return habilitados >= equipo.getDeporte().getMinJugadoresPartido();
    }

	public void crearCompetencia(Competencia c) {

	    competencias.add(c);

	}

	public boolean inscribirEquipoEnCompetencia(Equipo e, Competencia c, ArrayList<Jugador> jugadores) {

	    return c.inscribirEquipo(e, jugadores);

	}

	public void registrarPartidoCompetencia(Competencia c, PartidoCompetencia p) {
        if (c == null || !competencias.contains(c) || !c.admitePartido(p)) {
            return;
        }
        if (controlSanciones != null) {
            p.establecerControlSanciones(controlSanciones);
        }
        c.registrarPartido(p);
        if (c.getPartidos().contains(p) && !partidos.contains(p)) {
            partidos.add(p);
        }
    }
}