package persistencia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import club.ClubDeportivoSeneca;
import club.PagoMensualidad;
import java.time.YearMonth;
import usuarios.Usuario;
import usuarios.Socio;
import usuarios.Jugador;
import usuarios.Administrador;
import deportes.Deporte;
import deportes.ControlSanciones;
import deportes.Categoria;
import deportes.Equipo;
import deportes.InscripcionEquipo;
import deportes.Competencia;
import deportes.CompetenciaLiga;
import deportes.CompetenciaFases;
import deportes.Nomina;
import deportes.PartidoCompetencia;
import deportes.Estadistica;
import deportes.EstadisticaFutbol;
import deportes.EstadisticaBaloncesto;
import deportes.EstadisticaVoleibol;
import reservas.Instalacion;
import reservas.Reserva;
import academia.InscripcionIndividual;
import academia.Entrenamiento;
import academia.AsistenciaEntrenamiento;
import academia.PartidoPractica;
import usuarios.Entrenador;
import usuarios.Fisioterapeuta;
import usuarios.Empleado;
import usuarios.TurnoEmpleado;
import usuarios.SolicitudCambioTurno;
import cafeteria.VentaCafeteria;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import Tienda.Articulo;
import Tienda.tienda;
import Tienda.Venta;
import Tienda.DetalleVenta;
import cafeteria.Producto;
import cafeteria.bebida;
import cafeteria.snack;
import administracion.SugerenciaSnack;

public class PersistenciaArchivos {
    private File carpeta;

    public PersistenciaArchivos(String ruta) {
        carpeta = new File(ruta);
    }

    private void guardarInventariosUbicaciones(ClubDeportivoSeneca club) throws IOException {
        PrintWriter archivo = new PrintWriter(new File(carpeta, "inventarios_ubicaciones.txt"), "UTF-8");
        try {
            for (String ubicacion : club.getGestionInventarios().getUbicaciones()) {
                tienda inventario = club.getGestionInventarios().consultarTienda(ubicacion);
                archivo.println("T|" + limpiar(ubicacion));
                for (Articulo articulo : inventario.getArticulos()) {
                    archivo.println("A|" + limpiar(ubicacion) + "|" + limpiar(articulo.getNombre())
                            + "|" + limpiar(articulo.getCategoria()) + "|" + articulo.getPrecio()
                            + "|" + articulo.getCantidad());
                }
            }
        } finally {
            archivo.close();
        }
    }

    private void cargarInventariosUbicaciones(ClubDeportivoSeneca club) throws IOException {
        File archivo = new File(carpeta, "inventarios_ubicaciones.txt");
        if (!archivo.exists()) {
            return;
        }
        for (String linea : Files.readAllLines(archivo.toPath())) {
            String[] datos = linea.split("\\|", -1);
            try {
                if (datos.length == 2 && datos[0].equals("T")) {
                    club.getGestionInventarios().registrarTienda(datos[1], new tienda());
                } else if (datos.length == 6 && datos[0].equals("A")) {
                    tienda inventario = club.getGestionInventarios().consultarTienda(datos[1]);
                    if (inventario.buscarArticulo(datos[2]) != null) {
                        throw new IllegalStateException("Articulo repetido en la ubicacion");
                    }
                    double precio = Double.parseDouble(datos[4]);
                    int cantidad = Integer.parseInt(datos[5]);
                    if (datos[2].trim().isEmpty() || datos[3].trim().isEmpty()
                            || !Double.isFinite(precio) || precio < 0 || cantidad < 0) {
                        throw new IllegalArgumentException("Articulo invalido");
                    }
                    inventario.agregarArticulo(new Articulo(datos[2], datos[3], precio, cantidad));
                } else {
                    throw new IllegalArgumentException("Registro desconocido");
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                throw new IOException("Inventario por ubicacion invalido: " + linea, ex);
            }
        }
    }

    private void guardarSugerencias(ClubDeportivoSeneca club) throws IOException {
        PrintWriter archivo = new PrintWriter(new File(carpeta, "sugerencias_snacks.txt"), "UTF-8");
        for (SugerenciaSnack sugerencia : club.getGestionSugerencias().getSugerencias()) {
            archivo.println(limpiar(sugerencia.getAutor().getLogin()) + "|"
                    + limpiar(sugerencia.getNombre()) + "|" + sugerencia.getPrecio() + "|"
                    + sugerencia.getCantidadInicial() + "|" + limpiar(sugerencia.getAlergenos()) + "|"
                    + sugerencia.getEstado() + "|" + limpiar(sugerencia.getMotivo()));
        }
        archivo.close();
    }

    private void cargarSugerencias(ClubDeportivoSeneca club) throws IOException {
        for (String linea : leer("sugerencias_snacks.txt")) {
            String[] datos = linea.split("\\|", -1);
            if (datos.length != 7) {
                throw new IOException("Registro de sugerencia invalido");
            }
            Usuario autor = club.buscarUsuario(datos[0]);
            if (!(autor instanceof Empleado)) {
                throw new IOException("Empleado no encontrado: " + datos[0]);
            }
            try {
                club.getGestionSugerencias().restaurarSugerencia((Empleado) autor,
                        datos[1], Double.parseDouble(datos[2]), Integer.parseInt(datos[3]),
                        datos[4], SugerenciaSnack.Estado.valueOf(datos[5]), datos[6]);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                throw new IOException("Sugerencia invalida: " + datos[1], ex);
            }
        }
    }

    private void guardarSanciones(ClubDeportivoSeneca club) throws IOException {
        PrintWriter archivo = new PrintWriter(new File(carpeta, "sanciones.txt"), "UTF-8");
        ControlSanciones control = club.getControlSanciones();
        for (Jugador jugador : control.getJugadoresConAmarillas()) {
            archivo.println(limpiar(jugador.getLogin()) + "|" + control.getAmarillas(jugador) + "|" + control.estaSuspendido(jugador));
        }
        for (Jugador jugador : control.getSuspendidos()) {
            if (!control.getJugadoresConAmarillas().contains(jugador)) {
                archivo.println(limpiar(jugador.getLogin()) + "|0|true");
            }
        }
        archivo.close();
    }

    private void cargarSanciones(ClubDeportivoSeneca club) throws IOException {
        File archivo = new File(carpeta, "sanciones.txt");
        if (!archivo.exists()) {
            return;
        }
        for (String linea : Files.readAllLines(archivo.toPath())) {
            String[] datos = linea.split("\\|", -1);
            if (datos.length == 3 && club.buscarUsuario(datos[0]) instanceof Jugador) {
                Jugador jugador = (Jugador) club.buscarUsuario(datos[0]);
                club.getControlSanciones().recuperarEstado(jugador, Integer.parseInt(datos[1]), Boolean.parseBoolean(datos[2]));
            }
        }
    }

    private void guardarMensualidades(ClubDeportivoSeneca club) throws IOException {
        try (PrintWriter archivo = new PrintWriter(new File(carpeta, "mensualidades.txt"), "UTF-8")) {
            for (PagoMensualidad pago : club.getMensualidades()) {
                archivo.println(limpiar(pago.getSocio().getLogin()) + "|" + pago.getPeriodo() + "|"
                        + pago.getFechaPago() + "|" + pago.getValor());
            }
        }
    }

    private void cargarMensualidades(ClubDeportivoSeneca club) throws IOException {
        for (String linea : leer("mensualidades.txt")) {
            String[] datos = linea.split("\\|", -1);
            if (datos.length != 4 || !(club.buscarUsuario(datos[0]) instanceof Socio)) {
                throw new IOException("Registro de mensualidad invalido: " + linea);
            }
            try {
                club.registrarMensualidad((Socio) club.buscarUsuario(datos[0]),
                        YearMonth.parse(datos[1]), LocalDate.parse(datos[2]), Double.parseDouble(datos[3]));
            } catch (IllegalArgumentException | IllegalStateException e) {
                throw new IOException("No se pudo cargar la mensualidad", e);
            }
        }
    }

    public void guardar(ClubDeportivoSeneca club) throws IOException {
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta de datos");
        }
        PrintWriter archivo = new PrintWriter(new File(carpeta, "usuarios.txt"), "UTF-8");
        for (Usuario u : club.getUsuarios()) {
            if (u instanceof Jugador) {
                Jugador j = (Jugador) u;
                archivo.println("J|" + limpiar(j.getLogin()) + "|" + limpiar(j.getPassword()) + "|" + limpiar(j.getNombre()) + "|" + j.getPuntosFidelidad() + "|" + j.getFechaNacimiento());
            } else if (u instanceof Socio) {
                Socio socio = (Socio) u;
                archivo.println("S|" + limpiar(socio.getLogin()) + "|" + limpiar(socio.getPassword()) + "|" + limpiar(socio.getNombre()) + "|" + socio.getPuntosFidelidad());
            } else if (u instanceof Entrenador) {
                Entrenador entrenador = (Entrenador) u;
                archivo.println("E|" + limpiar(u.getLogin()) + "|" + limpiar(u.getPassword()) + "|" + limpiar(u.getNombre()) + "|" + limpiar(entrenador.getEspecialidad()));
            } else if (u instanceof Fisioterapeuta) {
                archivo.println("F|" + limpiar(u.getLogin()) + "|" + limpiar(u.getPassword()) + "|" + limpiar(u.getNombre()));
            } else if (u instanceof Administrador) {
                archivo.println("A|" + limpiar(u.getLogin()) + "|" + limpiar(u.getPassword()) + "|" + limpiar(u.getNombre()));
            }
        }
        archivo.close();
        archivo = new PrintWriter(new File(carpeta, "deportes.txt"), "UTF-8");
        for (Deporte d : club.getAreaDeportiva().getDeportes()) {
            archivo.println(limpiar(d.getNombre()) + "|" + d.getMinJugadoresPartido() + "|" + d.getPuntosGanar() + "|" + d.getPuntosEmpate());
        }
        archivo.close();
        archivo = new PrintWriter(new File(carpeta, "articulos.txt"), "UTF-8");
        for (Articulo a : club.getTienda().getArticulos()) {
            archivo.println(limpiar(a.getNombre()) + "|" + limpiar(a.getCategoria()) + "|" + a.getPrecio() + "|" + a.getCantidad());
        }
        archivo.close();
        archivo = new PrintWriter(new File(carpeta, "cafeteria.txt"), "UTF-8");
        for (Producto producto : club.getCafeteria().getProductos()) {
            if (producto instanceof bebida) {
                archivo.println("B|" + limpiar(producto.getNombre()) + "|" + producto.getPrecio() + "|" + producto.getCantidad() + "|" + ((bebida) producto).esCaliente());
            } else if (producto instanceof snack) {
                archivo.println("S|" + limpiar(producto.getNombre()) + "|" + producto.getPrecio() + "|" + producto.getCantidad() + "|" + limpiar(((snack) producto).getAlergenos()));
            }
        }
        archivo.close();
        guardarRelaciones(club);
        guardarCompetencias(club);
        guardarVentas(club);
        guardarTurnos(club);
        guardarSolicitudesTurno(club);
        guardarVentasCafeteria(club);
        guardarSanciones(club);
        guardarSugerencias(club);
        guardarInventariosUbicaciones(club);
        guardarMensualidades(club);
    }

    public void cargar(ClubDeportivoSeneca club) throws IOException {
        File archivo = new File(carpeta, "usuarios.txt");
        if (archivo.exists()) {
            List<String> lineas = Files.readAllLines(archivo.toPath());
            for (String linea : lineas) {
                String[] datos = linea.split("\\|", -1);
                if (datos[0].equals("J") && datos.length == 6) {
                    club.registrarUsuario(new Jugador(datos[1], datos[2], datos[3], Integer.parseInt(datos[4]), java.time.LocalDate.parse(datos[5])));
                } else if (datos[0].equals("S") && datos.length == 5) {
                    club.registrarUsuario(new Socio(datos[1], datos[2], datos[3], Integer.parseInt(datos[4])));
                } else if (datos[0].equals("E") && datos.length == 5) {
                    club.registrarUsuario(new Entrenador(datos[1], datos[2], datos[3], datos[4]));
                } else if (datos[0].equals("F") && datos.length == 4) {
                    club.registrarUsuario(new Fisioterapeuta(datos[1], datos[2], datos[3]));
                } else if (datos[0].equals("A") && datos.length == 4) {
                    club.registrarUsuario(new Administrador(datos[1], datos[2], datos[3]));
                }
            }
        }
        archivo = new File(carpeta, "deportes.txt");
        if (archivo.exists()) {
            for (String linea : Files.readAllLines(archivo.toPath())) {
                String[] d = linea.split("\\|", -1);
                if (d.length == 4) {
                    club.getAreaDeportiva().agregarDeporte(new Deporte(d[0], Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])));
                }
            }
        }
        archivo = new File(carpeta, "articulos.txt");
        if (archivo.exists()) {
            for (String linea : Files.readAllLines(archivo.toPath())) {
                String[] d = linea.split("\\|", -1);
                if (d.length == 4) {
                    club.getTienda().agregarArticulo(new Articulo(d[0], d[1], Double.parseDouble(d[2]), Integer.parseInt(d[3])));
                }
            }
        }
        archivo = new File(carpeta, "cafeteria.txt");
        if (archivo.exists()) {
            for (String linea : Files.readAllLines(archivo.toPath())) {
                String[] d = linea.split("\\|", -1);
                if (d.length == 5 && d[0].equals("B")) {
                    club.getCafeteria().agregarProducto(new bebida(d[1], Double.parseDouble(d[2]), Integer.parseInt(d[3]), Boolean.parseBoolean(d[4])));
                } else if (d.length == 5 && d[0].equals("S")) {
                    club.getCafeteria().agregarProducto(new snack(d[1], Double.parseDouble(d[2]), Integer.parseInt(d[3]), d[4]));
                }
            }
        }
        cargarRelaciones(club);
        cargarCompetencias(club);
        cargarVentas(club);
        cargarTurnos(club);
        cargarSolicitudesTurno(club);
        cargarVentasCafeteria(club);
        cargarSanciones(club);
        cargarSugerencias(club);
        cargarInventariosUbicaciones(club);
        cargarMensualidades(club);
    }


    private void guardarRelaciones(ClubDeportivoSeneca club) throws IOException {
        PrintWriter archivo = new PrintWriter(new File(carpeta, "categorias.txt"), "UTF-8");
        for (Categoria c : club.getAreaDeportiva().getCategorias()) {
            archivo.println(limpiar(c.getNombre()) + "|" + c.getEdadMinima() + "|" + c.getEdadMaxima());
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "equipos.txt"), "UTF-8");
        for (Equipo e : club.getAreaDeportiva().getEquipos()) {
            archivo.println(limpiar(e.getNombre()) + "|" + e.getCupoMaximo() + "|" + club.getAreaDeportiva().getDeportes().indexOf(e.getDeporte()) + "|" + club.getAreaDeportiva().getCategorias().indexOf(e.getCategoria()));
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "inscripciones_equipo.txt"), "UTF-8");
        for (Equipo e : club.getAreaDeportiva().getEquipos()) {
            for (InscripcionEquipo i : e.getInscripciones()) {
                archivo.println(club.getAreaDeportiva().getEquipos().indexOf(e) + "|" + limpiar(i.getJugador().getLogin()) + "|" + limpiar(i.getPosicion()) + "|" + i.getNumeroCamiseta());
            }
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "instalaciones.txt"), "UTF-8");
        for (Instalacion i : club.getGestionReservas().getInstalaciones()) {
            String modalidades = "";
            for (String modalidad : i.getModalidades()) {
                if (!modalidades.isEmpty()) {
                    modalidades += ",";
                }
                modalidades += limpiar(modalidad).replace(",", " ");
            }
            archivo.println(limpiar(i.getNombre()) + "|" + club.getAreaDeportiva().getDeportes().indexOf(i.getDeporte()) + "|" + i.getCapacidadMaxima() + "|" + modalidades + "|" + i.esTechada());
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "reservas.txt"), "UTF-8");
        for (Reserva r : club.getGestionReservas().getReservas()) {
            archivo.println(limpiar(r.getUsuario().getLogin()) + "|" + club.getGestionReservas().getInstalaciones().indexOf(r.getInstalacion()) + "|" + r.getFecha() + "|" + r.getInicio() + "|" + r.getFin() + "|" + r.getCantidadJugadores() + "|" + limpiar(r.getModalidad()));
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "inscripciones_academia.txt"), "UTF-8");
        for (InscripcionIndividual i : club.getAcademia().getInscripciones()) {
            archivo.println(limpiar(i.getJugador().getLogin()) + "|" + club.getAreaDeportiva().getDeportes().indexOf(i.getDeporte()) + "|" + limpiar(i.getNivel()) + "|" + i.getRanking());
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "entrenamientos.txt"), "UTF-8");
        for (Entrenamiento e : club.getAcademia().getEntrenamientos()) {
            int instalacion = club.getGestionReservas().getInstalaciones().indexOf(e.getInstalacion());
            archivo.println(e.getFecha() + "|" + club.getAreaDeportiva().getDeportes().indexOf(e.getDeporte()) + "|" + limpiar(e.getEntrenador().getLogin()) + "|" + (e.getHoraInicio() == null ? "" : e.getHoraInicio()) + "|" + e.getDuracionMinutos() + "|" + instalacion);
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "invitaciones_entrenamiento.txt"), "UTF-8");
        for (Entrenamiento e : club.getAcademia().getEntrenamientos()) {
            for (Jugador jugador : e.getInvitados()) {
                archivo.println(club.getAcademia().getEntrenamientos().indexOf(e) + "|" + limpiar(jugador.getLogin()));
            }
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "asistencias.txt"), "UTF-8");
        for (Entrenamiento e : club.getAcademia().getEntrenamientos()) {
            for (AsistenciaEntrenamiento asistencia : e.getAsistencias()) {
                archivo.println(club.getAcademia().getEntrenamientos().indexOf(e) + "|" + limpiar(asistencia.getJugador().getLogin()) + "|" + asistencia.getEstado() + "|" + asistencia.getCalificacion() + "|" + limpiar(asistencia.getObservacion()));
            }
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "partidos_practica.txt"), "UTF-8");
        for (PartidoPractica partido : club.getAcademia().getPartidosPractica()) {
            archivo.println(partido.getFecha() + "|" + club.getAreaDeportiva().getDeportes().indexOf(partido.getDeporte()) + "|" + limpiar(partido.getObservacion()));
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "participantes_practica.txt"), "UTF-8");
        for (PartidoPractica partido : club.getAcademia().getPartidosPractica()) {
            for (Jugador jugador : partido.getJugadores()) {
                archivo.println(club.getAcademia().getPartidosPractica().indexOf(partido) + "|" + limpiar(jugador.getLogin()));
            }
        }
        archivo.close();
    }

    private void cargarRelaciones(ClubDeportivoSeneca club) throws IOException {
        for (String linea : leer("categorias.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 3) {
                club.getAreaDeportiva().agregarCategoria(new Categoria(d[0], Integer.parseInt(d[1]), Integer.parseInt(d[2])));
            }
        }
        for (String linea : leer("equipos.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 4) {
                club.getAreaDeportiva().crearEquipo(d[0], club.getAreaDeportiva().getDeportes().get(Integer.parseInt(d[2])), club.getAreaDeportiva().getCategorias().get(Integer.parseInt(d[3])), Integer.parseInt(d[1]));
            }
        }
        for (String linea : leer("inscripciones_equipo.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 4 && club.buscarUsuario(d[1]) instanceof Jugador) {
                Equipo equipo = club.getAreaDeportiva().getEquipos().get(Integer.parseInt(d[0]));
                club.getAreaDeportiva().inscribirJugadorEnEquipo((Jugador) club.buscarUsuario(d[1]), equipo, d[2], Integer.parseInt(d[3]));
            }
        }
        for (String linea : leer("instalaciones.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 4 || d.length == 5) {
                HashSet<String> modalidades = new HashSet<>();
                for (String modalidad : d[3].split(",")) {
                    if (!modalidad.isEmpty()) {
                        modalidades.add(modalidad);
                    }
                }
                Instalacion instalacion = new Instalacion(d[0], club.getAreaDeportiva().getDeportes().get(Integer.parseInt(d[1])), Integer.parseInt(d[2]), modalidades);
                if (d.length == 5) instalacion.establecerTechada(Boolean.parseBoolean(d[4]));
                club.getGestionReservas().agregarInstalacion(instalacion);
            }
        }
        for (String linea : leer("reservas.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 7) {
                club.getGestionReservas().realizarReserva(club.buscarUsuario(d[0]), club.getGestionReservas().getInstalaciones().get(Integer.parseInt(d[1])), LocalDate.parse(d[2]), LocalTime.parse(d[3]), LocalTime.parse(d[4]), Integer.parseInt(d[5]), d[6]);
            }
        }
        for (String linea : leer("inscripciones_academia.txt")) {
            String[] d = linea.split("\\|", -1);
            if ((d.length == 2 || d.length == 4) && club.buscarUsuario(d[0]) instanceof Jugador) {
                Jugador jugador = (Jugador) club.buscarUsuario(d[0]);
                Deporte deporte = club.getAreaDeportiva().getDeportes().get(Integer.parseInt(d[1]));
                String nivel = d.length == 4 ? d[2] : "INICIAL";
                club.getAcademia().inscribirJugador(jugador, deporte, nivel);
                if (d.length == 4) {
                    InscripcionIndividual inscripcion = null;
                    for (InscripcionIndividual actual : club.getAcademia().consultarInscripciones(jugador)) {
                        if (actual.getDeporte() == deporte) {
                            inscripcion = actual;
                            break;
                        }
                    }
                    inscripcion.setRanking(Integer.parseInt(d[3]));
                }
            }
        }
        for (String linea : leer("entrenamientos.txt")) {
            String[] d = linea.split("\\|", -1);
            if ((d.length == 3 || d.length == 6) && club.buscarUsuario(d[2]) instanceof Entrenador) {
                LocalDate fecha = LocalDate.parse(d[0]);
                Deporte deporte = club.getAreaDeportiva().getDeportes().get(Integer.parseInt(d[1]));
                Entrenador entrenador = (Entrenador) club.buscarUsuario(d[2]);
                if (d.length == 3) {
                    club.getAcademia().programarEntrenamiento(fecha, deporte, entrenador);
                } else {
                    LocalTime hora = d[3].isEmpty() ? null : LocalTime.parse(d[3]);
                    int duracion = Integer.parseInt(d[4]);
                    int indice = Integer.parseInt(d[5]);
                    Instalacion instalacion = indice == -1 ? null : club.getGestionReservas().getInstalaciones().get(indice);
                    club.getAcademia().programarEntrenamientoValidado(fecha, deporte, entrenador, hora, duracion, instalacion);
                }
            }
        }
        for (String linea : leer("invitaciones_entrenamiento.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 2 && club.buscarUsuario(d[1]) instanceof Jugador) {
                club.getAcademia().getEntrenamientos().get(Integer.parseInt(d[0])).invitarJugador((Jugador) club.buscarUsuario(d[1]));
            }
        }
        for (String linea : leer("asistencias.txt")) {
            String[] d = linea.split("\\|", -1);
            if ((d.length == 4 || d.length == 5) && club.buscarUsuario(d[1]) instanceof Jugador) {
                Entrenamiento entrenamiento = club.getAcademia().getEntrenamientos().get(Integer.parseInt(d[0]));
                Jugador jugador = (Jugador) club.buscarUsuario(d[1]);
                if (d.length == 4) {
                    club.getAcademia().registrarAsistencia(entrenamiento, jugador, Boolean.parseBoolean(d[2]), d[3]);
                } else {
                    AsistenciaEntrenamiento.Estado estado = AsistenciaEntrenamiento.Estado.valueOf(d[2]);
                    entrenamiento.restaurarAsistencia(jugador, estado, Integer.parseInt(d[3]), d[4]);
                }
            }
        }
        for (String linea : leer("partidos_practica.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 3) {
                club.getAcademia().programarPartidoPractica(LocalDate.parse(d[0]), club.getAreaDeportiva().getDeportes().get(Integer.parseInt(d[1])), d[2]);
            }
        }
        for (String linea : leer("participantes_practica.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 2 && club.buscarUsuario(d[1]) instanceof Jugador) {
                club.getAcademia().getPartidosPractica().get(Integer.parseInt(d[0])).agregarJugador((Jugador) club.buscarUsuario(d[1]));
            }
        }
    }

    private void guardarCompetencias(ClubDeportivoSeneca club) throws IOException {
        PrintWriter archivo = new PrintWriter(new File(carpeta, "competencias.txt"), "UTF-8");
        for (Competencia c : club.getAreaDeportiva().getCompetencias()) {
            String tipo = "F";
            if (c instanceof CompetenciaLiga) {
                tipo = "L";
            }
            archivo.println(tipo + "|" + limpiar(c.getNombre()) + "|" + limpiar(c.getOrganizador()) + "|" + limpiar(c.getTemporada()) + "|" + c.getNominaMinima() + "|" + club.getAreaDeportiva().getDeportes().indexOf(c.getDeporte()) + "|" + club.getAreaDeportiva().getCategorias().indexOf(c.getCategoria()));
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "nominas.txt"), "UTF-8");
        for (Competencia c : club.getAreaDeportiva().getCompetencias()) {
            for (Nomina n : c.getNominas()) {
                String linea = club.getAreaDeportiva().getCompetencias().indexOf(c) + "|" + club.getAreaDeportiva().getEquipos().indexOf(n.getEquipo());
                for (Jugador j : n.getJugadores()) {
                    linea += "|" + limpiar(j.getLogin());
                }
                archivo.println(linea);
            }
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "partidos_competencia.txt"), "UTF-8");
        for (Competencia c : club.getAreaDeportiva().getCompetencias()) {
            for (PartidoCompetencia partido : c.getPartidos()) {
                archivo.println(club.getAreaDeportiva().getCompetencias().indexOf(c) + "|" + partido.getFecha() + "|" + club.getAreaDeportiva().getEquipos().indexOf(partido.getEquipoClub()) + "|" + limpiar(partido.getInstitucionRival()) + "|" + limpiar(partido.getEscenario()) + "|" + partido.getMarcadorLocal() + "|" + partido.getMarcadorVisitante() + "|" + partido.tieneResultadoRegistrado());
            }
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "estadisticas_competencia.txt"), "UTF-8");
        for (Competencia c : club.getAreaDeportiva().getCompetencias()) {
            for (PartidoCompetencia partido : c.getPartidos()) {
                for (Estadistica e : partido.getEstadisticas()) {
                    String tipo = "";
                    int primero = 0;
                    int segundo = 0;
                    if (e instanceof EstadisticaFutbol) {
                        tipo = "F";
                        primero = ((EstadisticaFutbol) e).getGoles();
                        segundo = ((EstadisticaFutbol) e).getAsistencias();
                    } else if (e instanceof EstadisticaBaloncesto) {
                        tipo = "B";
                        primero = ((EstadisticaBaloncesto) e).getPuntos();
                        segundo = ((EstadisticaBaloncesto) e).getRebotes();
                    } else if (e instanceof EstadisticaVoleibol) {
                        tipo = "V";
                        primero = ((EstadisticaVoleibol) e).getAces();
                        segundo = ((EstadisticaVoleibol) e).getBloqueos();
                    }
                    if (!tipo.isEmpty()) {
                        archivo.println(club.getAreaDeportiva().getCompetencias().indexOf(c) + "|" + c.getPartidos().indexOf(partido) + "|" + tipo + "|" + limpiar(e.getJugador().getLogin()) + "|" + primero + "|" + segundo);
                    }
                }
            }
        }
        archivo.close();
    }

    private void cargarCompetencias(ClubDeportivoSeneca club) throws IOException {
        for (String linea : leer("competencias.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 7) {
                Deporte deporte = club.getAreaDeportiva().getDeportes().get(Integer.parseInt(d[5]));
                Categoria categoria = club.getAreaDeportiva().getCategorias().get(Integer.parseInt(d[6]));
                Competencia competencia;
                if (d[0].equals("L")) {
                    competencia = new CompetenciaLiga(d[1], d[2], d[3], Integer.parseInt(d[4]), deporte, categoria);
                } else {
                    competencia = new CompetenciaFases(d[1], d[2], d[3], Integer.parseInt(d[4]), deporte, categoria);
                }
                club.getAreaDeportiva().crearCompetencia(competencia);
            }
        }
        for (String linea : leer("nominas.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length >= 3) {
                Competencia c = club.getAreaDeportiva().getCompetencias().get(Integer.parseInt(d[0]));
                Equipo e = club.getAreaDeportiva().getEquipos().get(Integer.parseInt(d[1]));
                ArrayList<Jugador> jugadores = new ArrayList<>();
                for (int i = 2; i < d.length; i++) {
                    if (club.buscarUsuario(d[i]) instanceof Jugador) {
                        jugadores.add((Jugador) club.buscarUsuario(d[i]));
                    }
                }
                c.inscribirEquipo(e, jugadores);
            }
        }
        for (String linea : leer("partidos_competencia.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 7 || d.length == 8) {
                Competencia c = club.getAreaDeportiva().getCompetencias().get(Integer.parseInt(d[0]));
                Equipo e = club.getAreaDeportiva().getEquipos().get(Integer.parseInt(d[2]));
                PartidoCompetencia partido = new PartidoCompetencia(LocalDate.parse(d[1]), c.getDeporte(), e, d[3], d[4]);
                if (d.length == 7 || Boolean.parseBoolean(d[7])) {
                    partido.registrarMarcador(Integer.parseInt(d[5]), Integer.parseInt(d[6]));
                }
                club.getAreaDeportiva().registrarPartidoCompetencia(c, partido);
            }
        }
        for (String linea : leer("estadisticas_competencia.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 6 && club.buscarUsuario(d[3]) instanceof Jugador) {
                Competencia c = club.getAreaDeportiva().getCompetencias().get(Integer.parseInt(d[0]));
                PartidoCompetencia p = c.getPartidos().get(Integer.parseInt(d[1]));
                Jugador j = (Jugador) club.buscarUsuario(d[3]);
                int primero = Integer.parseInt(d[4]);
                int segundo = Integer.parseInt(d[5]);
                if (d[2].equals("F")) {
                    p.registrarEstadistica(new EstadisticaFutbol(j, primero, segundo));
                } else if (d[2].equals("B")) {
                    p.registrarEstadistica(new EstadisticaBaloncesto(j, primero, segundo));
                } else if (d[2].equals("V")) {
                    p.registrarEstadistica(new EstadisticaVoleibol(j, primero, segundo));
                }
            }
        }
    }

    private void guardarVentas(ClubDeportivoSeneca club) throws IOException {
        PrintWriter archivo = new PrintWriter(new File(carpeta, "ventas.txt"), "UTF-8");
        for (Venta venta : club.getTienda().getVentas()) {
            if (venta.getComprador() != null) {
                archivo.println(venta.getFecha() + "|" + limpiar(venta.getComprador().getLogin()) + "|" + venta.getSubtotal() + "|" + venta.getDescuento());
            }
        }
        archivo.close();

        archivo = new PrintWriter(new File(carpeta, "detalles_venta.txt"), "UTF-8");
        for (Venta venta : club.getTienda().getVentas()) {
            if (venta.getComprador() != null) {
                int indice = club.getTienda().getVentas().indexOf(venta);
                for (DetalleVenta detalle : venta.getDetalles()) {
                    archivo.println(indice + "|" + club.getTienda().getArticulos().indexOf(detalle.getArticulo()) + "|" + detalle.getCantidad() + "|" + detalle.getPrecioUnitario());
                }
            }
        }
        archivo.close();
    }

    private void cargarVentas(ClubDeportivoSeneca club) throws IOException {
        ArrayList<Venta> ventas = new ArrayList<>();
        for (String linea : leer("ventas.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 4 && club.buscarUsuario(d[1]) != null) {
                Venta venta = new Venta(club.buscarUsuario(d[1]));
                venta.establecerFecha(LocalDate.parse(d[0]));
                ventas.add(venta);
                club.getTienda().restaurarVenta(venta);
            } else {
                ventas.add(null);
            }
        }
        for (String linea : leer("detalles_venta.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 4) {
                int indiceVenta = Integer.parseInt(d[0]);
                int indiceArticulo = Integer.parseInt(d[1]);
                if (indiceVenta >= 0 && indiceVenta < ventas.size() && indiceArticulo >= 0 && indiceArticulo < club.getTienda().getArticulos().size()) {
                    Venta venta = ventas.get(indiceVenta);
                    if (venta != null) {
                        venta.agregarDetalleGuardado(club.getTienda().getArticulos().get(indiceArticulo), Integer.parseInt(d[2]), Double.parseDouble(d[3]));
                    }
                }
            }
        }
        int indice = 0;
        for (String linea : leer("ventas.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 4 && ventas.get(indice) != null) {
                Venta venta = ventas.get(indice);
                if (venta.getSubtotal() > 0) {
                    venta.restaurarDescuento(Double.parseDouble(d[3]) * 100.0 / venta.getSubtotal());
                }
                venta.marcarComoRestaurada();
            }
            indice++;
        }
    }

    private void guardarTurnos(ClubDeportivoSeneca club) throws IOException {
        PrintWriter archivo = new PrintWriter(new File(carpeta, "turnos.txt"), "UTF-8");
        for (TurnoEmpleado turno : club.getGestionTurnos().getTurnos()) {
            Empleado empleado = turno.getEmpleado();
            String tipo = "";
            String especialidad = "";
            if (empleado instanceof Entrenador) {
                tipo = "E";
                especialidad = ((Entrenador) empleado).getEspecialidad();
            } else if (empleado instanceof Fisioterapeuta) {
                tipo = "F";
            }
            if (!tipo.isEmpty()) {
                archivo.println(tipo + "|" + limpiar(empleado.getLogin()) + "|" + limpiar(empleado.getPassword()) + "|" + limpiar(empleado.getNombre()) + "|" + limpiar(especialidad) + "|" + turno.getInicio() + "|" + turno.getFin());
            }
        }
        archivo.close();
    }

    private void cargarTurnos(ClubDeportivoSeneca club) throws IOException {
        for (String linea : leer("turnos.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 7) {
                Usuario usuario = club.buscarUsuario(d[1]);
                if (usuario == null && d[0].equals("E")) {
                    usuario = new Entrenador(d[1], d[2], d[3], d[4]);
                    club.registrarUsuario(usuario);
                } else if (usuario == null && d[0].equals("F")) {
                    usuario = new Fisioterapeuta(d[1], d[2], d[3]);
                    club.registrarUsuario(usuario);
                }
                if (usuario instanceof Empleado) {
                    club.getGestionTurnos().agregarTurno((Empleado) usuario, LocalTime.parse(d[5]), LocalTime.parse(d[6]));
                }
            }
        }
    }

    private void guardarSolicitudesTurno(ClubDeportivoSeneca club) throws IOException {
        PrintWriter archivo = new PrintWriter(new File(carpeta, "solicitudes_turno.txt"), "UTF-8");
        for (SolicitudCambioTurno solicitud : club.getGestionTurnos().getSolicitudes()) {
            String login = solicitud.getTurnoOriginal().getEmpleado().getLogin();
            archivo.println(limpiar(login) + "|" + solicitud.getInicioOriginal() + "|"
                    + solicitud.getFinOriginal() + "|" + solicitud.getNuevoInicio() + "|"
                    + solicitud.getNuevoFin() + "|" + solicitud.getEstado());
        }
        archivo.close();
    }

    private void cargarSolicitudesTurno(ClubDeportivoSeneca club) throws IOException {
        for (String linea : leer("solicitudes_turno.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length != 6) {
                continue;
            }
            Usuario usuario = club.buscarUsuario(d[0]);
            if (!(usuario instanceof Empleado)) {
                continue;
            }
            try {
                LocalTime inicioOriginal = LocalTime.parse(d[1]);
                LocalTime finOriginal = LocalTime.parse(d[2]);
                LocalTime nuevoInicio = LocalTime.parse(d[3]);
                LocalTime nuevoFin = LocalTime.parse(d[4]);
                SolicitudCambioTurno.Estado estado = SolicitudCambioTurno.Estado.valueOf(d[5]);
                LocalTime inicioActual = estado == SolicitudCambioTurno.Estado.APROBADA ? nuevoInicio : inicioOriginal;
                LocalTime finActual = estado == SolicitudCambioTurno.Estado.APROBADA ? nuevoFin : finOriginal;
                TurnoEmpleado encontrado = null;
                for (TurnoEmpleado turno : club.getGestionTurnos().getTurnos()) {
                    if (turno.getEmpleado() == usuario && turno.getInicio().equals(inicioActual)
                            && turno.getFin().equals(finActual)) {
                        encontrado = turno;
                        break;
                    }
                }
                if (encontrado != null) {
                    club.getGestionTurnos().restaurarSolicitud(encontrado, inicioOriginal, finOriginal,
                            nuevoInicio, nuevoFin, estado);
                }
            } catch (IllegalArgumentException excepcion) {
                throw new IOException("Solicitud de turno invalida: " + linea, excepcion);
            }
        }
    }

    private void guardarVentasCafeteria(ClubDeportivoSeneca club) throws IOException {
        PrintWriter archivo = new PrintWriter(new File(carpeta, "ventas_cafeteria.txt"), "UTF-8");
        for (VentaCafeteria venta : club.getCafeteria().getVentas()) {
            int indice = club.getCafeteria().getProductos().indexOf(venta.getProducto());
            if (venta.getComprador() != null && indice >= 0) {
                archivo.println(limpiar(venta.getComprador().getLogin()) + "|" + indice + "|" + venta.getCantidad() + "|" + venta.getPropina() + "|" + venta.getFecha());
            }
        }
        archivo.close();
    }

    private void cargarVentasCafeteria(ClubDeportivoSeneca club) throws IOException {
        for (String linea : leer("ventas_cafeteria.txt")) {
            String[] d = linea.split("\\|", -1);
            if (d.length == 5) {
                Usuario comprador = club.buscarUsuario(d[0]);
                int indice = Integer.parseInt(d[1]);
                if (comprador != null && indice >= 0 && indice < club.getCafeteria().getProductos().size()) {
                    Producto producto = club.getCafeteria().getProductos().get(indice);
                    VentaCafeteria venta = new VentaCafeteria(comprador, producto, Integer.parseInt(d[2]), Double.parseDouble(d[3]));
                    venta.establecerFecha(LocalDate.parse(d[4]));
                    club.getCafeteria().restaurarVenta(venta);
                }
            }
        }
    }

    private List<String> leer(String nombre) throws IOException {
        File archivo = new File(carpeta, nombre);
        if (archivo.exists()) {
            return Files.readAllLines(archivo.toPath());
        }
        return new ArrayList<>();
    }

    private String limpiar(String texto) {
        return texto.replace("|", " ").replace("\n", " ").replace("\r", " ");
    }
}
