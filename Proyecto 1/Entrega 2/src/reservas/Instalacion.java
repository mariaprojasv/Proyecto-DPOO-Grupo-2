package reservas;

import deportes.Deporte;
import java.util.ArrayList;
import java.util.Set;

public class Instalacion {
    private String nombre;
    private Deporte deporte;
    private int capacidadMaxima;
    private ArrayList<String> modalidades;
    public Instalacion(String nombre, Deporte deporte, int capacidadMaxima, Set<String> modalidades) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.capacidadMaxima = capacidadMaxima;
        this.modalidades = new ArrayList<>();
        if (modalidades != null) this.modalidades.addAll(modalidades);
    }
    public String getNombre() {
	return nombre;
	}
    public Deporte getDeporte() {
	return deporte;
	}
    public int getCapacidadMaxima() {
	return capacidadMaxima;
	}
    public ArrayList<String> getModalidades() {
	return modalidades;
	}
    public boolean admiteModalidad(String modalidad) {
        if (modalidad == null) {
            return false;
        }
        for (String m : modalidades) {
            if (m.equalsIgnoreCase(modalidad)) {
                return true;
            }
        }
        return false;
    }
}
