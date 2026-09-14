package deportes;

public class Categoria {
	private String nombre;
	private int edadMinima;
	private int edadMaxima;
	public Categoria(String nombre, int edadMinima, int edadMaxima) {
		super();
		this.nombre = nombre;
		this.edadMinima = edadMinima;
		this.edadMaxima = edadMaxima;
	}
	public String getNombre() {
		return nombre;
	}
	public int getEdadMaxima() {
		return edadMaxima;
	}
	public boolean edadPermitida(int edad) {
		return edad >= edadMinima && edad <= edadMaxima;
	}
	

}
