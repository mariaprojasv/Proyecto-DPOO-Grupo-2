package administracion;

import club.ClubDeportivoSeneca;
import deportes.Competencia;
import deportes.Partido;
import java.time.LocalDate;
import java.util.HashSet;

public class ReporteDeportivo {
    private final int equipos;
    private final int competencias;
    private final int partidos;
    private final int victorias;
    private final int empates;
    private final int derrotas;
    private final int pendientes;

    public ReporteDeportivo(ClubDeportivoSeneca club, LocalDate desde, LocalDate hasta) {
        if (club == null || desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El club y el rango de fechas son obligatorios");
        }
        equipos = club.getAreaDeportiva().getEquipos().size();
        competencias = club.getAreaDeportiva().getCompetencias().size();
        HashSet<Partido> unicos = new HashSet<>();
        unicos.addAll(club.getAreaDeportiva().getPartidos());
        for (Competencia competencia : club.getAreaDeportiva().getCompetencias()) {
            unicos.addAll(competencia.getPartidos());
        }
        int cantidad = 0;
        int ganados = 0;
        int empatados = 0;
        int perdidos = 0;
        int sinResultado = 0;
        for (Partido partido : unicos) {
            if (partido != null && partido.getFecha() != null
                    && !partido.getFecha().isBefore(desde) && !partido.getFecha().isAfter(hasta)) {
                cantidad++;
                String resultado = partido.getResultado();
                if ("VICTORIA".equals(resultado)) {
                    ganados++;
                } else if ("EMPATE".equals(resultado)) {
                    empatados++;
                } else if ("DERROTA".equals(resultado)) {
                    perdidos++;
                } else if ("PENDIENTE".equals(resultado)) {
                    sinResultado++;
                }
            }
        }
        partidos = cantidad;
        victorias = ganados;
        empates = empatados;
        derrotas = perdidos;
        pendientes = sinResultado;
    }

    public int getEquipos() { return equipos; }
    public int getCompetencias() { return competencias; }
    public int getPartidos() { return partidos; }
    public int getVictorias() { return victorias; }
    public int getEmpates() { return empates; }
    public int getDerrotas() { return derrotas; }
    public int getPendientes() { return pendientes; }
}
