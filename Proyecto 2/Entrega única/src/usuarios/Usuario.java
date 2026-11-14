package usuarios;

import java.util.ArrayList;
import torneos.BonoDescuento;

public abstract class Usuario {
	private String login;
	private String password;
	private String nombre;
	private final ArrayList<BonoDescuento> bonos = new ArrayList<>();
	public Usuario(String login, String password, String nombre) {
		this.login = login;
		this.password = password;
		this.nombre = nombre;
	}
	public String getLogin() {
	return login;
	}
	public String getPassword() {
	return password;
	}
	public String getNombre() {
	return nombre;
	}
	public boolean autenticar(String password) {
	    return this.password.equals(password);
	}

	public ArrayList<BonoDescuento> getBonos() {
	    return new ArrayList<>(bonos);
	}
	public ArrayList<BonoDescuento> getBonosDisponibles() {
	    ArrayList<BonoDescuento> disponibles = new ArrayList<>();
	    for (BonoDescuento bono : bonos) {
	        if (bono.estaDisponible()) {
	            disponibles.add(bono);
	        }
	    }
	    return disponibles;
	}
	/** Guarda un bono ganado para usarlo en una compra futura. */
	public void guardarBono(BonoDescuento bono) {
	    if (bono == null || bono.getTitular() != this) {
	        throw new IllegalArgumentException("El bono no pertenece a este usuario");
	    }
	    if (!bonos.contains(bono)) {
	        bonos.add(bono);
	    }
	}
}
