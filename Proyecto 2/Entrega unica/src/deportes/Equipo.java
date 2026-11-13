package deportes;

import java.util.ArrayList;
import usuarios.Jugador;

public class Equipo {
	private String nombre;
	private int cupoMaximo;
	private Deporte deporte;
	private Categoria categoria;
	private ArrayList<InscripcionEquipo> inscripciones;
	public Equipo(String nombre, int cupoMaximo, Deporte deporte, Categoria categoria) {
		this.nombre = nombre;
		this.cupoMaximo = cupoMaximo;
		this.deporte = deporte;
		this.categoria = categoria;
		this.inscripciones = new ArrayList<>();
	}
	public String getNombre() {
	return nombre;
	}
	public int getCupoMaximo() {
	return cupoMaximo;
	}
	public Deporte getDeporte() {
	return deporte;
	}
	public Categoria getCategoria() {
	return categoria;
	}
	public ArrayList<InscripcionEquipo> getInscripciones() {
	return inscripciones;
	}
	
	public boolean numeroDisponible(int numero) {
		for (InscripcionEquipo ins : inscripciones) {
			if (ins.getNumeroCamiseta() == numero) {
				return false;
			}
		}
		return true;
		
	}
	public ArrayList<Jugador> getJugadores() {
	    ArrayList<Jugador> jugadores = new ArrayList<>();

	    for (InscripcionEquipo ins : inscripciones) {
	        jugadores.add(ins.getJugador());
	    }

	    return jugadores;
	}
	public boolean tieneMinimoJugadores() {
	    return inscripciones.size() >= deporte.getMinJugadoresPartido();
	}
	public void retirarJugador(Jugador j) {
	    for (InscripcionEquipo ins : inscripciones) {
	        if (ins.getJugador() == j) {
	            inscripciones.remove(ins);
	            return;
	        }
	    }
	}
    public boolean agregarJugador(Jugador j, String posicion, int numero) {
        try {
            agregarJugadorValidado(j, posicion, numero);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }

    public void agregarJugadorValidado(Jugador j, String posicion, int numero) {
        if (j == null) {
            throw new IllegalArgumentException("El jugador es obligatorio");
        }
        if (posicion == null || posicion.trim().isEmpty()) {
            throw new IllegalArgumentException("La posicion es obligatoria");
        }
        if (numero <= 0) {
            throw new IllegalArgumentException("El numero de camiseta debe ser positivo");
        }
        if (getJugadores().contains(j)) {
            throw new IllegalStateException("El jugador ya esta inscrito en este equipo");
        }
        if (inscripciones.size() >= cupoMaximo) {
            throw new IllegalStateException("El equipo no tiene cupos disponibles");
        }
        if (categoria == null || !categoria.edadPermitida(j.getEdad())) {
            throw new IllegalArgumentException("El jugador no cumple la edad de la categoria");
        }
        if (!numeroDisponible(numero)) {
            throw new IllegalStateException("El numero de camiseta ya esta ocupado");
        }
        inscripciones.add(new InscripcionEquipo(j, posicion, numero));
    }
}
