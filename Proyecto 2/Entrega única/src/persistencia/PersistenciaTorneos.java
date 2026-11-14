package persistencia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import club.ClubDeportivoSeneca;
import deportes.Categoria;
import deportes.Equipo;
import torneos.BonoDescuento;
import torneos.Competidor;
import torneos.CompetidorEquipo;
import torneos.CompetidorIndividual;
import torneos.CompetidorPareja;
import torneos.Encuentro;
import torneos.InscripcionTorneo;
import torneos.PremioEfectivo;
import torneos.Torneo;
import torneos.TorneoCompetitivo;
import torneos.TorneoRecreativo;
import usuarios.Usuario;

/** Guarda y carga los torneos, sus inscripciones, encuentros, premios y los bonos de descuento. */
class PersistenciaTorneos {
    private final File carpeta;

    PersistenciaTorneos(File carpeta) {
        this.carpeta = carpeta;
    }

    private List<BonoDescuento> todosLosBonos(ClubDeportivoSeneca club) {
        List<BonoDescuento> bonos = new ArrayList<>();
        for (Usuario u : club.getUsuarios()) {
            bonos.addAll(u.getBonos());
        }
        return bonos;
    }

    void guardar(ClubDeportivoSeneca club) throws IOException {
        List<BonoDescuento> bonos = todosLosBonos(club);
        PrintWriter archivo = new PrintWriter(new File(carpeta, "bonos.txt"), "UTF-8");
        try {
            for (BonoDescuento b : bonos) {
                archivo.println(limpiar(b.getTitular().getLogin()) + "|" + b.getPorcentaje() + "|"
                        + limpiar(b.getOrigen()) + "|" + !b.estaDisponible() + "|" + b.getId());
            }
        } finally {
            archivo.close();
        }
        List<Torneo> torneos = club.getGestionTorneos().getTorneos();
        PrintWriter t = new PrintWriter(new File(carpeta, "torneos.txt"), "UTF-8");
        PrintWriter ins = new PrintWriter(new File(carpeta, "torneos_inscripciones.txt"), "UTF-8");
        PrintWriter enc = new PrintWriter(new File(carpeta, "torneos_encuentros.txt"), "UTF-8");
        PrintWriter pre = new PrintWriter(new File(carpeta, "torneos_premios.txt"), "UTF-8");
        PrintWriter bon = new PrintWriter(new File(carpeta, "torneos_bonos.txt"), "UTF-8");
        try {
            for (int ti = 0; ti < torneos.size(); ti++) {
                Torneo torneo = torneos.get(ti);
                String p1;
                String p2 = "";
                String tipo;
                if (torneo instanceof TorneoRecreativo) {
                    tipo = "R";
                    p1 = String.valueOf(((TorneoRecreativo) torneo).getPorcentajeBono());
                } else {
                    tipo = "C";
                    p1 = String.valueOf(((TorneoCompetitivo) torneo).getTarifaInscripcion());
                    p2 = String.valueOf(((TorneoCompetitivo) torneo).getPorcentajePremio());
                }
                t.println(tipo + "|" + limpiar(torneo.getNombre()) + "|"
                        + club.getAreaDeportiva().getDeportes().indexOf(torneo.getDeporte()) + "|" + torneo.getModalidad()
                        + "|" + club.getAreaDeportiva().getCategorias().indexOf(torneo.getCategoria()) + "|"
                        + limpiar(torneo.getNivel() == null ? "" : torneo.getNivel()) + "|" + torneo.getFormato() + "|"
                        + torneo.getCupos() + "|" + torneo.getFechaInicio() + "|" + torneo.getFechaFin() + "|"
                        + torneo.getEstado() + "|" + torneo.sonPreferencialesLiberados() + "|" + torneo.getTotalRondas()
                        + "|" + p1 + "|" + p2);
                ArrayList<InscripcionTorneo> inscripciones = torneo.getInscripciones();
                for (InscripcionTorneo i : inscripciones) {
                    ins.println(ti + "|" + i.getTipoCupo() + "|" + i.getTarifaCobrada() + "|"
                            + referencia(club, i.getCompetidor()));
                }
                for (Encuentro e : torneo.getEncuentros()) {
                    enc.println(ti + "|" + e.getRonda() + "|" + indiceCompetidor(inscripciones, e.getCompetidorA()) + "|"
                            + indiceCompetidor(inscripciones, e.getCompetidorB()) + "|" + e.getMarcadorA() + "|"
                            + e.getMarcadorB() + "|" + e.estaJugado());
                }
                if (torneo instanceof TorneoCompetitivo) {
                    for (PremioEfectivo p : ((TorneoCompetitivo) torneo).getPremiosEntregados()) {
                        pre.println(ti + "|" + limpiar(p.getBeneficiario().getLogin()) + "|" + p.getMonto());
                    }
                } else {
                    for (BonoDescuento b : ((TorneoRecreativo) torneo).getBonosOtorgados()) {
                        bon.println(ti + "|" + bonos.indexOf(b));
                    }
                }
            }
        } finally {
            t.close();
            ins.close();
            enc.close();
            pre.close();
            bon.close();
        }
    }

    void cargarBonos(ClubDeportivoSeneca club) throws IOException {
        for (String linea : leer("bonos.txt")) {
            String[] d = linea.split("\\|", -1);
            Usuario titular = (d.length == 4 || d.length == 5) ? club.buscarUsuario(d[0]) : null;
            if (titular == null) {
                throw new IOException("Bono invalido: " + linea);
            }
            try {
                BonoDescuento bono = d.length == 5
                        ? new BonoDescuento(d[4], titular, Double.parseDouble(d[1]), d[2])
                        : new BonoDescuento(titular, Double.parseDouble(d[1]), d[2]);
                bono.restaurarUsado(Boolean.parseBoolean(d[3]));
                titular.guardarBono(bono);
            } catch (IllegalArgumentException ex) {
                throw new IOException("Bono invalido: " + linea, ex);
            }
        }
    }

    void cargar(ClubDeportivoSeneca club) throws IOException {
        if (todosLosBonos(club).isEmpty()) {
            cargarBonos(club);
        }
        List<BonoDescuento> bonos = todosLosBonos(club);
        ArrayList<Torneo> cargados = new ArrayList<>();
        for (String linea : leer("torneos.txt")) {
            String[] d = linea.split("\\|", -1);
            try {
                if (d.length != 15) {
                    throw new IllegalArgumentException("Numero de campos incorrecto");
                }
                var area = club.getAreaDeportiva();
                var deporte = area.getDeportes().get(Integer.parseInt(d[2]));
                int catIdx = Integer.parseInt(d[4]);
                Categoria categoria = catIdx >= 0 ? area.getCategorias().get(catIdx) : null;
                String nivel = d[5].isEmpty() ? null : d[5];
                Torneo.Modalidad modalidad = Torneo.Modalidad.valueOf(d[3]);
                Torneo.Formato formato = Torneo.Formato.valueOf(d[6]);
                int cupos = Integer.parseInt(d[7]);
                LocalDate inicio = LocalDate.parse(d[8]);
                LocalDate fin = LocalDate.parse(d[9]);
                Torneo torneo;
                if (d[0].equals("R")) {
                    torneo = new TorneoRecreativo(d[1], deporte, modalidad, categoria, nivel, formato, cupos, inicio, fin,
                            Double.parseDouble(d[13]));
                } else if (d[0].equals("C")) {
                    torneo = new TorneoCompetitivo(d[1], deporte, modalidad, categoria, nivel, formato, cupos, inicio, fin,
                            Double.parseDouble(d[13]), Double.parseDouble(d[14]));
                } else {
                    throw new IllegalArgumentException("Tipo de torneo desconocido");
                }
                torneo.restaurarEstado(Torneo.Estado.valueOf(d[10]), Boolean.parseBoolean(d[11]), Integer.parseInt(d[12]));
                club.getGestionTorneos().restaurarTorneo(torneo);
                cargados.add(torneo);
            } catch (RuntimeException ex) {
                throw new IOException("Torneo invalido: " + linea, ex);
            }
        }
        for (String linea : leer("torneos_inscripciones.txt")) {
            String[] d = linea.split("\\|", -1);
            try {
                Torneo torneo = cargados.get(Integer.parseInt(d[0]));
                torneo.restaurarInscripcion(competidor(club, d[3]), Torneo.TipoCupo.valueOf(d[1]), Double.parseDouble(d[2]));
            } catch (RuntimeException ex) {
                throw new IOException("Inscripcion de torneo invalida: " + linea, ex);
            }
        }
        for (String linea : leer("torneos_encuentros.txt")) {
            String[] d = linea.split("\\|", -1);
            try {
                Torneo torneo = cargados.get(Integer.parseInt(d[0]));
                ArrayList<InscripcionTorneo> inscripciones = torneo.getInscripciones();
                int ib = Integer.parseInt(d[3]);
                torneo.restaurarEncuentro(Integer.parseInt(d[1]), inscripciones.get(Integer.parseInt(d[2])).getCompetidor(),
                        ib >= 0 ? inscripciones.get(ib).getCompetidor() : null, Integer.parseInt(d[4]),
                        Integer.parseInt(d[5]), Boolean.parseBoolean(d[6]));
            } catch (RuntimeException ex) {
                throw new IOException("Encuentro de torneo invalido: " + linea, ex);
            }
        }
        for (String linea : leer("torneos_premios.txt")) {
            String[] d = linea.split("\\|", -1);
            Torneo torneo = cargados.get(Integer.parseInt(d[0]));
            Usuario beneficiario = club.buscarUsuario(d[1]);
            if (!(torneo instanceof TorneoCompetitivo) || beneficiario == null) {
                throw new IOException("Premio de torneo invalido: " + linea);
            }
            ((TorneoCompetitivo) torneo).restaurarPremio(beneficiario, Double.parseDouble(d[2]));
        }
        for (String linea : leer("torneos_bonos.txt")) {
            String[] d = linea.split("\\|", -1);
            Torneo torneo = cargados.get(Integer.parseInt(d[0]));
            int indice = Integer.parseInt(d[1]);
            if (!(torneo instanceof TorneoRecreativo) || indice < 0 || indice >= bonos.size()) {
                throw new IOException("Bono de torneo invalido: " + linea);
            }
            ((TorneoRecreativo) torneo).restaurarBonoOtorgado(bonos.get(indice));
        }
    }

    private int indiceCompetidor(ArrayList<InscripcionTorneo> inscripciones, Competidor competidor) {
        if (competidor == null) {
            return -1;
        }
        for (int i = 0; i < inscripciones.size(); i++) {
            if (inscripciones.get(i).getCompetidor() == competidor) {
                return i;
            }
        }
        throw new IllegalStateException("Competidor sin inscripcion");
    }

    private String referencia(ClubDeportivoSeneca club, Competidor c) {
        if (c instanceof CompetidorEquipo) {
            return "Q:" + club.getAreaDeportiva().getEquipos().indexOf(((CompetidorEquipo) c).getEquipo());
        }
        if (c instanceof CompetidorPareja) {
            CompetidorPareja p = (CompetidorPareja) c;
            return "P:" + limpiar(p.getPrimero().getLogin()) + ";" + limpiar(p.getSegundo().getLogin());
        }
        return "I:" + limpiar(((CompetidorIndividual) c).getUsuario().getLogin());
    }

    private Competidor competidor(ClubDeportivoSeneca club, String ref) {
        String valor = ref.substring(2);
        if (ref.startsWith("Q:")) {
            Equipo equipo = club.getAreaDeportiva().getEquipos().get(Integer.parseInt(valor));
            return new CompetidorEquipo(equipo);
        }
        if (ref.startsWith("P:")) {
            String[] logins = valor.split(";", -1);
            return new CompetidorPareja(usuario(club, logins[0]), usuario(club, logins[1]));
        }
        return new CompetidorIndividual(usuario(club, valor));
    }

    private Usuario usuario(ClubDeportivoSeneca club, String login) {
        Usuario u = club.buscarUsuario(login);
        if (u == null) {
            throw new IllegalArgumentException("Usuario inexistente: " + login);
        }
        return u;
    }

    private List<String> leer(String nombre) throws IOException {
        File archivo = new File(carpeta, nombre);
        return archivo.exists() ? Files.readAllLines(archivo.toPath()) : new ArrayList<>();
    }

    private String limpiar(String texto) {
        return texto.replace("|", " ").replace("\n", " ").replace("\r", " ");
    }
}
