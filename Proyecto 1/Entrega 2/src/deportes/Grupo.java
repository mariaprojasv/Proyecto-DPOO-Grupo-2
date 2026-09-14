package deportes;

import java.util.ArrayList;

public class Grupo {
	private String nombre;
	private ArrayList<Equipo> equipos;

	public Grupo(String nombre) {
		this.nombre = nombre;
		this.equipos = new ArrayList<>();
	}

	public String getNombre() {
		return nombre;
	}

	public ArrayList<Equipo> getEquipos() {
		return equipos;
	}

	public void agregarEquipo(Equipo e) {
		equipos.add(e);
	}
}