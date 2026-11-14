package academia;

import java.time.LocalDate;
import java.util.ArrayList;
import deportes.Deporte;
import usuarios.Jugador;

public class PartidoPractica {
    private LocalDate fecha;
    private Deporte deporte;
    private String observacion;
    private ArrayList<Jugador> jugadores;

    public PartidoPractica(LocalDate fecha, Deporte deporte, String observacion) {
        if (fecha == null || deporte == null) throw new IllegalArgumentException("Datos del partido invalidos");
        this.fecha = fecha;
        this.deporte = deporte;
        this.observacion = observacion;
        this.jugadores = new ArrayList<>();
    }

    public LocalDate getFecha() { return fecha; }
    public Deporte getDeporte() { return deporte; }
    public String getObservacion() { return observacion; }
    public ArrayList<Jugador> getJugadores() { return new ArrayList<>(jugadores); }
    public void agregarJugador(Jugador jugador) {
        if (jugador == null) throw new IllegalArgumentException("Jugador nulo");
        if (jugadores.contains(jugador)) throw new IllegalStateException("Jugador duplicado");
        jugadores.add(jugador);
    }
}
