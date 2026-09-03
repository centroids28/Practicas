/*
 * ============================================================================
 *  Centro de Rescate Animal: Gestion de Refugio y Adopciones
 *  Proyecto 1 - IPC1 - Segundo Semestre 2026
 *  Universidad de San Carlos de Guatemala - Facultad de Ingenieria
 * ============================================================================
 *  Archivo unico generado a partir del proyecto multi-clase original
 *  (ver carpeta src/refugio/ para la version organizada por paquetes).
 *
 *  Todas las clases se agrupan aqui en el paquete por defecto para poder
 *  compilar y ejecutar con un solo archivo:
 *
 *      javac Main.java
 *      java Main
 *
 *  Orden de clases en este archivo:
 *   1. Modelo:        Animal, Adoptante, Solicitud, Rescate, Usuario, EntradaBitacora
 *   2. Persistencia:  PersistenciaManager
 *   3. Servicios:     BitacoraService, AutenticacionService, AnimalService,
 *                      AdoptanteService, SolicitudService, RescateService,
 *                      UbicacionService
 *   4. Reportes:      ReporteHTML
 *   5. Interfaz (UI): LoginFrame, MainFrame, PanelAnimales, PanelAdoptantes,
 *                      PanelSolicitudes, PanelRescates, PanelUbicaciones,
 *                      PanelReportes
 *   6. Main (clase publica, punto de entrada)
 * ============================================================================
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


// ============================================================================
// MODELO
// ============================================================================

/**
 * Representa un animal rescatado por el refugio.
 * Estados clinicos permitidos: EN_OBSERVACION, EN_TRATAMIENTO, APTO
 * Estados de adopcion permitidos: DISPONIBLE, ADOPTADO, ELIMINADO
 */
class Animal {

    public static final String[] ESPECIES_VALIDAS = {"Perro", "Gato"};
    public static final String[] ESTADOS_CLINICOS = {"EN_OBSERVACION", "EN_TRATAMIENTO", "APTO"};
    public static final String[] ESTADOS_ADOPCION = {"DISPONIBLE", "ADOPTADO", "ELIMINADO"};

    private String codigo;      // Formato "A-014"
    private String nombre;
    private String especie;     // Perro | Gato
    private int edadEstimada;   // 0 - 25
    private String estadoClinico;
    private String estadoAdopcion;
    private String codigoRescateOrigen; // opcional, vincula con Rescate que lo genero

    public Animal(String codigo, String nombre, String especie, int edadEstimada,
                  String estadoClinico, String estadoAdopcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.especie = especie;
        this.edadEstimada = edadEstimada;
        this.estadoClinico = estadoClinico;
        this.estadoAdopcion = estadoAdopcion;
        this.codigoRescateOrigen = "";
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public int getEdadEstimada() { return edadEstimada; }
    public void setEdadEstimada(int edadEstimada) { this.edadEstimada = edadEstimada; }
    public String getEstadoClinico() { return estadoClinico; }
    public void setEstadoClinico(String estadoClinico) { this.estadoClinico = estadoClinico; }
    public String getEstadoAdopcion() { return estadoAdopcion; }
    public void setEstadoAdopcion(String estadoAdopcion) { this.estadoAdopcion = estadoAdopcion; }
    public String getCodigoRescateOrigen() { return codigoRescateOrigen; }
    public void setCodigoRescateOrigen(String c) { this.codigoRescateOrigen = c; }

    public static boolean especieValida(String especie) {
        for (String e : ESPECIES_VALIDAS) if (e.equalsIgnoreCase(especie)) return true;
        return false;
    }

    public static boolean estadoClinicoValido(String estado) {
        for (String e : ESTADOS_CLINICOS) if (e.equalsIgnoreCase(estado)) return true;
        return false;
    }

    public static boolean estadoAdopcionValido(String estado) {
        for (String e : ESTADOS_ADOPCION) if (e.equalsIgnoreCase(estado)) return true;
        return false;
    }

    /** Serializa a linea de archivo separado por "|" */
    public String toLine() {
        return codigo + "|" + nombre + "|" + especie + "|" + edadEstimada + "|" +
               estadoClinico + "|" + estadoAdopcion + "|" + codigoRescateOrigen;
    }

    public static Animal fromLine(String linea) {
        String[] p = linea.split("\\|", -1);
        Animal a = new Animal(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4], p[5]);
        if (p.length > 6) a.setCodigoRescateOrigen(p[6]);
        return a;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + especie + ", " + edadEstimada + " años) [" +
               estadoClinico + " / " + estadoAdopcion + "]";
    }
}

class Adoptante {

    private String codigo;   // "AD-007"
    private String nombre;   // solo letras y espacios
    private String dpi;      // 13 digitos numericos, unico
    private String telefono; // 8 digitos

    public Adoptante(String codigo, String nombre, String dpi, String telefono) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.dpi = dpi;
        this.telefono = telefono;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDpi() { return dpi; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public static boolean nombreValido(String nombre) {
        return nombre != null && nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+") && !nombre.trim().isEmpty();
    }

    public static boolean dpiValido(String dpi) {
        return dpi != null && dpi.matches("\\d{13}");
    }

    public static boolean telefonoValido(String tel) {
        return tel != null && tel.matches("\\d{8}");
    }

    public String toLine() {
        return codigo + "|" + nombre + "|" + dpi + "|" + telefono;
    }

    public static Adoptante fromLine(String linea) {
        String[] p = linea.split("\\|", -1);
        return new Adoptante(p[0], p[1], p[2], p[3]);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (DPI: " + dpi + ", Tel: " + telefono + ")";
    }
}

class Solicitud {

    public static final String[] ESTADOS = {"PENDIENTE", "APROBADA", "RECHAZADA", "COMPLETADA"};

    private String codigo;         // "S-021"
    private String codigoAnimal;   // "A-014"
    private String codigoAdoptante;// "AD-007"
    private String fecha;          // dd/mm/aaaa
    private String estado;

    public Solicitud(String codigo, String codigoAnimal, String codigoAdoptante, String fecha, String estado) {
        this.codigo = codigo;
        this.codigoAnimal = codigoAnimal;
        this.codigoAdoptante = codigoAdoptante;
        this.fecha = fecha;
        this.estado = estado;
    }

    public String getCodigo() { return codigo; }
    public String getCodigoAnimal() { return codigoAnimal; }
    public String getCodigoAdoptante() { return codigoAdoptante; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public static boolean estadoValido(String estado) {
        for (String e : ESTADOS) if (e.equalsIgnoreCase(estado)) return true;
        return false;
    }

    /** Valida formato dd/mm/aaaa y que la fecha exista realmente */
    public static boolean fechaValida(String fecha) {
        if (fecha == null || !fecha.matches("\\d{2}/\\d{2}/\\d{4}")) return false;
        String[] partes = fecha.split("/");
        int d = Integer.parseInt(partes[0]);
        int m = Integer.parseInt(partes[1]);
        int y = Integer.parseInt(partes[2]);
        if (m < 1 || m > 12) return false;
        int[] diasPorMes = {31,28,31,30,31,30,31,31,30,31,30,31};
        boolean bisiesto = (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0);
        int maxDia = diasPorMes[m - 1];
        if (m == 2 && bisiesto) maxDia = 29;
        return d >= 1 && d <= maxDia;
    }

    public String toLine() {
        return codigo + "|" + codigoAnimal + "|" + codigoAdoptante + "|" + fecha + "|" + estado;
    }

    public static Solicitud fromLine(String linea) {
        String[] p = linea.split("\\|", -1);
        return new Solicitud(p[0], p[1], p[2], p[3], p[4]);
    }

    @Override
    public String toString() {
        return codigo + " | Animal: " + codigoAnimal + " | Adoptante: " + codigoAdoptante +
               " | " + fecha + " | " + estado;
    }
}

class Rescate {

    public static final String[] PRIORIDADES = {"ALTA", "MEDIA", "BAJA"};
    public static final String[] ESTADOS = {"PENDIENTE", "ATENDIDO"};

    private String codigo;              // "R-009"
    private String descripcion;         // breve descripcion del caso
    private String prioridad;           // ALTA | MEDIA | BAJA
    private String estado;              // PENDIENTE | ATENDIDO
    private String fechaReporte;        // dd/mm/aaaa
    private String codigoAnimalVinculado; // se llena al atender

    public Rescate(String codigo, String descripcion, String prioridad, String estado, String fechaReporte) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaReporte = fechaReporte;
        this.codigoAnimalVinculado = "";
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getFechaReporte() { return fechaReporte; }
    public String getCodigoAnimalVinculado() { return codigoAnimalVinculado; }
    public void setCodigoAnimalVinculado(String c) { this.codigoAnimalVinculado = c; }

    public static boolean prioridadValida(String p) {
        for (String x : PRIORIDADES) if (x.equalsIgnoreCase(p)) return true;
        return false;
    }

    public static boolean estadoValido(String e) {
        for (String x : ESTADOS) if (x.equalsIgnoreCase(e)) return true;
        return false;
    }

    public String toLine() {
        return codigo + "|" + descripcion + "|" + prioridad + "|" + estado + "|" +
               fechaReporte + "|" + codigoAnimalVinculado;
    }

    public static Rescate fromLine(String linea) {
        String[] p = linea.split("\\|", -1);
        Rescate r = new Rescate(p[0], p[1], p[2], p[3], p[4]);
        if (p.length > 5) r.setCodigoAnimalVinculado(p[5]);
        return r;
    }

    @Override
    public String toString() {
        return codigo + " [" + prioridad + "/" + estado + "] " + descripcion + " (" + fechaReporte + ")";
    }
}

class Usuario {

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_AUXILIAR = "AUXILIAR";

    private String usuario;
    private String contrasena;
    private String rol;

    public Usuario(String usuario, String contrasena, String rol) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getUsuario() { return usuario; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }

    public boolean esAdmin() { return ROL_ADMIN.equalsIgnoreCase(rol); }

    public static boolean usuarioValido(String u) {
        return u != null && u.matches("[a-zA-Z0-9]{4,15}");
    }

    public static boolean contrasenaValida(String c) {
        return c != null && c.length() >= 6;
    }

    public static boolean rolValido(String r) {
        return ROL_ADMIN.equalsIgnoreCase(r) || ROL_AUXILIAR.equalsIgnoreCase(r);
    }
}

/**
 * Representa una linea de bitacora, ya sea de Acciones o de Errores.
 * Acciones: fecha|usuario|modulo|evento|descripcion
 * Errores:  fecha|usuario|modulo|evento|motivo
 */
class EntradaBitacora {

    private String fechaHora;
    private String usuario;
    private String modulo;
    private String evento;
    private String detalle; // descripcion (accion) o motivo (error)

    public EntradaBitacora(String fechaHora, String usuario, String modulo, String evento, String detalle) {
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.modulo = modulo;
        this.evento = evento;
        this.detalle = detalle;
    }

    public String getFechaHora() { return fechaHora; }
    public String getUsuario() { return usuario; }
    public String getModulo() { return modulo; }
    public String getEvento() { return evento; }
    public String getDetalle() { return detalle; }

    public String toLine() {
        return fechaHora + "|" + usuario + "|" + modulo + "|" + evento + "|" + detalle;
    }

    public static EntradaBitacora fromLine(String linea) {
        String[] p = linea.split("\\|", -1);
        return new EntradaBitacora(p[0], p[1], p[2], p[3], p[4]);
    }
}


// ============================================================================
// PERSISTENCIA
// ============================================================================

/**
 * Maneja lectura y escritura de archivos de texto plano usados como
 * mecanismo de persistencia. Se usan unicamente arreglos (nunca
 * ArrayList/Collections), leyendo el archivo en dos pasadas: la primera
 * para contar lineas validas y la segunda para llenarlas en un arreglo
 * de tamaño exacto.
 */
class PersistenciaManager {

    public static final String CARPETA_DATOS = "data";

    static {
        File dir = new File(CARPETA_DATOS);
        if (!dir.exists()) dir.mkdirs();
    }

    /** Escribe (sobrescribe) todas las lineas dadas en el archivo indicado. */
    public static void escribirTodo(String nombreArchivo, String[] lineas, int cantidad) {
        String ruta = CARPETA_DATOS + File.separator + nombreArchivo;
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ruta), StandardCharsets.UTF_8))) {
            for (int i = 0; i < cantidad; i++) {
                bw.write(lineas[i]);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir " + nombreArchivo + ": " + e.getMessage());
        }
    }

    /** Agrega una linea al final del archivo (usado por bitacoras). */
    public static void agregarLinea(String nombreArchivo, String linea) {
        String ruta = CARPETA_DATOS + File.separator + nombreArchivo;
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ruta, true), StandardCharsets.UTF_8))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al agregar linea en " + nombreArchivo + ": " + e.getMessage());
        }
    }

    /** Lee todas las lineas no vacias del archivo. Retorna arreglo de tamaño exacto (dos pasadas, sin ArrayList). */
    public static String[] leerTodo(String nombreArchivo) {
        String ruta = CARPETA_DATOS + File.separator + nombreArchivo;
        File f = new File(ruta);
        if (!f.exists()) return new String[0];

        int total = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) total++;
            }
        } catch (IOException e) {
            System.err.println("Error al leer " + nombreArchivo + ": " + e.getMessage());
            return new String[0];
        }

        String[] resultado = new String[total];
        int idx = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    resultado[idx] = linea;
                    idx++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer " + nombreArchivo + ": " + e.getMessage());
        }
        return resultado;
    }

    public static void guardarTextoPlano(String nombreArchivo, String contenido) {
        String ruta = CARPETA_DATOS + File.separator + nombreArchivo;
        try {
            Files.write(Paths.get(ruta), contenido.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Error al guardar " + nombreArchivo + ": " + e.getMessage());
        }
    }
}


// ============================================================================
// SERVICIOS
// ============================================================================

class BitacoraService {

    private static final int MAX = 1000;
    public static final String ARCHIVO_ACCIONES = "bitacora_acciones.txt";
    public static final String ARCHIVO_ERRORES = "bitacora_errores.txt";

    private EntradaBitacora[] acciones = new EntradaBitacora[MAX];
    private int totalAcciones = 0;
    private EntradaBitacora[] errores = new EntradaBitacora[MAX];
    private int totalErrores = 0;

    public BitacoraService() {
        cargar();
    }

    private String fechaHoraActual() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    public void registrarAccion(String usuario, String modulo, String evento, String descripcion) {
        if (totalAcciones >= MAX) return;
        EntradaBitacora e = new EntradaBitacora(fechaHoraActual(), usuario, modulo, evento, descripcion);
        acciones[totalAcciones] = e;
        totalAcciones++;
        PersistenciaManager.agregarLinea(ARCHIVO_ACCIONES, e.toLine());
    }

    public void registrarError(String usuario, String modulo, String evento, String motivo) {
        if (totalErrores >= MAX) return;
        EntradaBitacora e = new EntradaBitacora(fechaHoraActual(), usuario, modulo, evento, motivo);
        errores[totalErrores] = e;
        totalErrores++;
        PersistenciaManager.agregarLinea(ARCHIVO_ERRORES, e.toLine());
    }

    private void cargar() {
        String[] lineasA = PersistenciaManager.leerTodo(ARCHIVO_ACCIONES);
        for (int i = 0; i < lineasA.length && totalAcciones < MAX; i++) {
            acciones[totalAcciones] = EntradaBitacora.fromLine(lineasA[i]);
            totalAcciones++;
        }
        String[] lineasE = PersistenciaManager.leerTodo(ARCHIVO_ERRORES);
        for (int i = 0; i < lineasE.length && totalErrores < MAX; i++) {
            errores[totalErrores] = EntradaBitacora.fromLine(lineasE[i]);
            totalErrores++;
        }
    }

    public EntradaBitacora[] getAcciones() { return acciones; }
    public int getTotalAcciones() { return totalAcciones; }
    public EntradaBitacora[] getErrores() { return errores; }
    public int getTotalErrores() { return totalErrores; }
}

/**
 * Usuarios cargados desde memoria (arreglo estatico), segun el enunciado.
 * Maximo 3 intentos fallidos antes de bloquear la sesion.
 */
class AutenticacionService {

    private static final int MAX_USUARIOS = 10;
    private Usuario[] usuarios = new Usuario[MAX_USUARIOS];
    private int totalUsuarios = 0;

    private int intentosFallidos = 0;
    private boolean sesionBloqueada = false;
    private Usuario usuarioActual = null;

    private BitacoraService bitacora;

    public AutenticacionService(BitacoraService bitacora) {
        this.bitacora = bitacora;
        // Usuarios precargados en memoria (arreglo estatico)
        usuarios[totalUsuarios++] = new Usuario("admin1", "Refugio2026", Usuario.ROL_ADMIN);
        usuarios[totalUsuarios++] = new Usuario("auxiliar1", "Auxiliar1", Usuario.ROL_AUXILIAR);
    }

    /** Retorna null si no autentica; lanza una razon a traves de mensajeError */
    private String mensajeError = "";

    public String getMensajeError() { return mensajeError; }

    public boolean isSesionBloqueada() { return sesionBloqueada; }

    public Usuario login(String usuario, String contrasena) {
        mensajeError = "";
        if (sesionBloqueada) {
            mensajeError = "Sesión bloqueada, reinicie la aplicación";
            return null;
        }
        if (usuario == null || usuario.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
            mensajeError = "Usuario o contraseña vacíos";
            intentosFallidos++;
            bitacora.registrarError(usuario == null ? "" : usuario, "AUTENTICACION", "LOGIN_FALLIDO",
                    "Campos vacíos (intento " + intentosFallidos + " de 3)");
            verificarBloqueo();
            return null;
        }
        for (int i = 0; i < totalUsuarios; i++) {
            if (usuarios[i].getUsuario().equals(usuario)) {
                if (usuarios[i].getContrasena().equals(contrasena)) {
                    intentosFallidos = 0;
                    usuarioActual = usuarios[i];
                    bitacora.registrarAccion(usuario, "AUTENTICACION", "LOGIN_OK", "Inicio de sesión correcto");
                    return usuarioActual;
                } else {
                    intentosFallidos++;
                    mensajeError = "Contraseña incorrecta";
                    bitacora.registrarError(usuario, "AUTENTICACION", "LOGIN_FALLIDO",
                            "Contraseña incorrecta (intento " + intentosFallidos + " de 3)");
                    verificarBloqueo();
                    return null;
                }
            }
        }
        intentosFallidos++;
        mensajeError = "Usuario no existe";
        bitacora.registrarError(usuario, "AUTENTICACION", "LOGIN_FALLIDO",
                "Usuario no existe (intento " + intentosFallidos + " de 3)");
        verificarBloqueo();
        return null;
    }

    private void verificarBloqueo() {
        if (intentosFallidos >= 3) {
            sesionBloqueada = true;
            mensajeError = "Sesión bloqueada, reinicie la aplicación";
        }
    }

    public void logout() {
        if (usuarioActual != null) {
            bitacora.registrarAccion(usuarioActual.getUsuario(), "AUTENTICACION", "LOGOUT", "Cierre de sesión");
        }
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
}

class AnimalService {

    public static final int MAX_ANIMALES = 200;
    public static final String ARCHIVO = "animales.txt";

    private Animal[] animales = new Animal[MAX_ANIMALES];
    private int total = 0;
    private int consecutivo = 0; // para generar codigos A-xxx

    private BitacoraService bitacora;

    public AnimalService(BitacoraService bitacora) {
        this.bitacora = bitacora;
        cargar();
    }

    private void cargar() {
        String[] lineas = PersistenciaManager.leerTodo(ARCHIVO);
        for (int i = 0; i < lineas.length && total < MAX_ANIMALES; i++) {
            Animal a = Animal.fromLine(lineas[i]);
            animales[total] = a;
            total++;
            int num = extraerNumero(a.getCodigo());
            if (num > consecutivo) consecutivo = num;
        }
    }

    private int extraerNumero(String codigo) {
        try {
            return Integer.parseInt(codigo.replace("A-", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public void guardar() {
        String[] lineas = new String[total];
        for (int i = 0; i < total; i++) lineas[i] = animales[i].toLine();
        PersistenciaManager.escribirTodo(ARCHIVO, lineas, total);
    }

    /** Genera el siguiente codigo consecutivo con prefijo "A-" */
    public String generarCodigo() {
        consecutivo++;
        return "A-" + String.format("%03d", consecutivo);
    }

    /** Genera un codigo reutilizando el mismo consecutivo de un rescate (ej. R-009 -> A-009) */
    public String generarCodigoDesdeRescate(String codigoRescate) {
        String num = codigoRescate.replace("R-", "");
        String codigo = "A-" + num;
        int n = 0;
        try { n = Integer.parseInt(num); } catch (Exception ignored) {}
        if (n > consecutivo) consecutivo = n;
        return codigo;
    }

    public int buscarIndicePorCodigo(String codigo) {
        for (int i = 0; i < total; i++) {
            if (animales[i].getCodigo().equalsIgnoreCase(codigo)
                    && !animales[i].getEstadoAdopcion().equalsIgnoreCase("ELIMINADO")) {
                return i;
            }
        }
        return -1;
    }

    /** Incluye eliminados (para no reutilizar codigo) */
    public boolean codigoExisteAlguna(String codigo) {
        for (int i = 0; i < total; i++) {
            if (animales[i].getCodigo().equalsIgnoreCase(codigo)) return true;
        }
        return false;
    }

    public String registrar(String codigo, String nombre, String especie, int edad,
                             String estadoClinico, String estadoAdopcion, String usuario) {
        if (codigo == null || codigo.trim().isEmpty() || !codigo.matches("A-\\d{3,}")) {
            bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Código inválido: " + codigo);
            return "Código inválido. Formato esperado: A-014";
        }
        if (codigoExisteAlguna(codigo)) {
            bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Código duplicado: " + codigo);
            return "El código ya existe (o fue eliminado y no puede reutilizarse)";
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Nombre vacío");
            return "El nombre no puede estar vacío";
        }
        if (!Animal.especieValida(especie)) {
            bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Especie \"" + especie + "\" no permitida");
            return "Especie inválida. Use Perro o Gato";
        }
        if (edad < 0 || edad > 25) {
            bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Edad fuera de rango: " + edad);
            return "Edad fuera de rango (0-25)";
        }
        if (!Animal.estadoClinicoValido(estadoClinico)) {
            bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Estado clínico inválido: " + estadoClinico);
            return "Estado clínico inválido";
        }
        if (!Animal.estadoAdopcionValido(estadoAdopcion)) {
            bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Estado de adopción inválido: " + estadoAdopcion);
            return "Estado de adopción inválido";
        }
        if (total >= MAX_ANIMALES) {
            return "Capacidad máxima de registros de animales alcanzada";
        }
        Animal a = new Animal(codigo, nombre.trim(), especie, edad, estadoClinico, estadoAdopcion);
        animales[total] = a;
        total++;
        guardar();
        bitacora.registrarAccion(usuario, "ANIMALES", "ALTA", "Animal " + codigo + " registrado");
        return null; // sin error
    }

    public String editarEstado(String codigo, String nuevoEstadoClinico, String nuevoEstadoAdopcion, String usuario) {
        int idx = buscarIndicePorCodigo(codigo);
        if (idx == -1) {
            bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Animal no encontrado: " + codigo);
            return "Animal no encontrado";
        }
        if (nuevoEstadoClinico != null && !nuevoEstadoClinico.isEmpty()) {
            if (!Animal.estadoClinicoValido(nuevoEstadoClinico)) {
                bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Estado clínico inválido: " + nuevoEstadoClinico);
                return "Estado clínico inválido";
            }
            animales[idx].setEstadoClinico(nuevoEstadoClinico);
        }
        if (nuevoEstadoAdopcion != null && !nuevoEstadoAdopcion.isEmpty()) {
            if (!Animal.estadoAdopcionValido(nuevoEstadoAdopcion)) {
                bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Estado de adopción inválido: " + nuevoEstadoAdopcion);
                return "Estado de adopción inválido";
            }
            animales[idx].setEstadoAdopcion(nuevoEstadoAdopcion);
        }
        guardar();
        bitacora.registrarAccion(usuario, "ANIMALES", "EDICION", "Animal " + codigo + " actualizado");
        return null;
    }

    /** Baja logica */
    public String eliminar(String codigo, String usuario) {
        int idx = buscarIndicePorCodigo(codigo);
        if (idx == -1) {
            bitacora.registrarError(usuario, "ANIMALES", "VALIDACION", "Animal no encontrado: " + codigo);
            return "Animal no encontrado";
        }
        animales[idx].setEstadoAdopcion("ELIMINADO");
        guardar();
        bitacora.registrarAccion(usuario, "ANIMALES", "BAJA", "Animal " + codigo + " eliminado (baja lógica)");
        return null;
    }

    /** Recorre y retorna solo los activos (no ELIMINADO) que cumplen filtros; filtros vacios = sin filtro */
    public Animal[] buscar(String codigo, String nombre, String especie, String estado) {
        Animal[] tempResultado = new Animal[total];
        int cnt = 0;
        for (int i = 0; i < total; i++) {
            Animal a = animales[i];
            if (a.getEstadoAdopcion().equalsIgnoreCase("ELIMINADO")) continue;
            if (codigo != null && !codigo.isEmpty() && !a.getCodigo().toLowerCase().contains(codigo.toLowerCase())) continue;
            if (nombre != null && !nombre.isEmpty() && !a.getNombre().toLowerCase().contains(nombre.toLowerCase())) continue;
            if (especie != null && !especie.isEmpty() && !a.getEspecie().equalsIgnoreCase(especie)) continue;
            if (estado != null && !estado.isEmpty() && !a.getEstadoAdopcion().equalsIgnoreCase(estado)) continue;
            tempResultado[cnt] = a;
            cnt++;
        }
        Animal[] resultado = new Animal[cnt];
        for (int i = 0; i < cnt; i++) resultado[i] = tempResultado[i];
        return resultado;
    }

    public Animal[] listarActivos() {
        return buscar(null, null, null, null);
    }

    public Animal obtenerPorCodigo(String codigo) {
        int idx = buscarIndicePorCodigo(codigo);
        return idx == -1 ? null : animales[idx];
    }

    public int getTotal() { return total; }

    /** Retorna copia de tamaño exacto (sin huecos nulos) */
    public Animal[] getTodos() {
        Animal[] copia = new Animal[total];
        for (int i = 0; i < total; i++) copia[i] = animales[i];
        return copia;
    }
}

class AdoptanteService {

    public static final int MAX_ADOPTANTES = 150;
    public static final String ARCHIVO = "adoptantes.txt";

    private Adoptante[] adoptantes = new Adoptante[MAX_ADOPTANTES];
    private int total = 0;
    private int consecutivo = 0;

    private BitacoraService bitacora;

    public AdoptanteService(BitacoraService bitacora) {
        this.bitacora = bitacora;
        cargar();
    }

    private void cargar() {
        String[] lineas = PersistenciaManager.leerTodo(ARCHIVO);
        for (int i = 0; i < lineas.length && total < MAX_ADOPTANTES; i++) {
            Adoptante a = Adoptante.fromLine(lineas[i]);
            adoptantes[total] = a;
            total++;
            int num = 0;
            try { num = Integer.parseInt(a.getCodigo().replace("AD-", "")); } catch (Exception ignored) {}
            if (num > consecutivo) consecutivo = num;
        }
    }

    public void guardar() {
        String[] lineas = new String[total];
        for (int i = 0; i < total; i++) lineas[i] = adoptantes[i].toLine();
        PersistenciaManager.escribirTodo(ARCHIVO, lineas, total);
    }

    public String generarCodigo() {
        consecutivo++;
        return "AD-" + String.format("%03d", consecutivo);
    }

    public int buscarIndicePorCodigo(String codigo) {
        for (int i = 0; i < total; i++) {
            if (adoptantes[i].getCodigo().equalsIgnoreCase(codigo)) return i;
        }
        return -1;
    }

    public boolean dpiExiste(String dpi) {
        for (int i = 0; i < total; i++) {
            if (adoptantes[i].getDpi().equals(dpi)) return true;
        }
        return false;
    }

    public String registrar(String codigo, String nombre, String dpi, String telefono, String usuario) {
        if (codigo == null || !codigo.matches("AD-\\d{3,}")) {
            bitacora.registrarError(usuario, "ADOPTANTES", "VALIDACION", "Código inválido: " + codigo);
            return "Código inválido. Formato esperado: AD-007";
        }
        if (buscarIndicePorCodigo(codigo) != -1) {
            bitacora.registrarError(usuario, "ADOPTANTES", "VALIDACION", "Código duplicado: " + codigo);
            return "El código ya existe";
        }
        if (!Adoptante.nombreValido(nombre)) {
            bitacora.registrarError(usuario, "ADOPTANTES", "VALIDACION", "Nombre inválido: " + nombre);
            return "Nombre inválido (solo letras y espacios)";
        }
        if (!Adoptante.dpiValido(dpi)) {
            bitacora.registrarError(usuario, "ADOPTANTES", "VALIDACION", "DPI inválido: " + dpi);
            return "DPI inválido (13 dígitos numéricos)";
        }
        if (dpiExiste(dpi)) {
            bitacora.registrarError(usuario, "ADOPTANTES", "DUPLICADO", "DPI " + dpi + " ya existe");
            return "Ya existe un adoptante con ese DPI";
        }
        if (!Adoptante.telefonoValido(telefono)) {
            bitacora.registrarError(usuario, "ADOPTANTES", "VALIDACION", "Teléfono inválido: " + telefono);
            return "Teléfono inválido (8 dígitos)";
        }
        if (total >= MAX_ADOPTANTES) return "Capacidad máxima de adoptantes alcanzada";

        adoptantes[total] = new Adoptante(codigo, nombre.trim(), dpi, telefono);
        total++;
        guardar();
        bitacora.registrarAccion(usuario, "ADOPTANTES", "ALTA", "Adoptante " + codigo + " registrado");
        return null;
    }

    public String editar(String codigo, String nombre, String telefono, String usuario) {
        int idx = buscarIndicePorCodigo(codigo);
        if (idx == -1) {
            bitacora.registrarError(usuario, "ADOPTANTES", "VALIDACION", "Adoptante no encontrado: " + codigo);
            return "Adoptante no encontrado";
        }
        if (nombre != null && !nombre.isEmpty()) {
            if (!Adoptante.nombreValido(nombre)) return "Nombre inválido";
            adoptantes[idx].setNombre(nombre.trim());
        }
        if (telefono != null && !telefono.isEmpty()) {
            if (!Adoptante.telefonoValido(telefono)) return "Teléfono inválido";
            adoptantes[idx].setTelefono(telefono);
        }
        guardar();
        bitacora.registrarAccion(usuario, "ADOPTANTES", "EDICION", "Adoptante " + codigo + " actualizado");
        return null;
    }

    public Adoptante[] buscar(String texto) {
        Adoptante[] tempResultado = new Adoptante[total];
        int cnt = 0;
        for (int i = 0; i < total; i++) {
            Adoptante a = adoptantes[i];
            if (texto == null || texto.isEmpty()
                    || a.getCodigo().toLowerCase().contains(texto.toLowerCase())
                    || a.getNombre().toLowerCase().contains(texto.toLowerCase())
                    || a.getDpi().contains(texto)) {
                tempResultado[cnt] = a;
                cnt++;
            }
        }
        Adoptante[] resultado = new Adoptante[cnt];
        for (int i = 0; i < cnt; i++) resultado[i] = tempResultado[i];
        return resultado;
    }

    public Adoptante obtenerPorCodigo(String codigo) {
        int idx = buscarIndicePorCodigo(codigo);
        return idx == -1 ? null : adoptantes[idx];
    }

    public int getTotal() { return total; }

    /** Retorna copia de tamaño exacto (sin huecos nulos) */
    public Adoptante[] getTodos() {
        Adoptante[] copia = new Adoptante[total];
        for (int i = 0; i < total; i++) copia[i] = adoptantes[i];
        return copia;
    }
}

class SolicitudService {

    public static final int MAX_SOLICITUDES = 200;
    public static final String ARCHIVO = "solicitudes.txt";

    private Solicitud[] solicitudes = new Solicitud[MAX_SOLICITUDES];
    private int total = 0;
    private int consecutivo = 0;

    private BitacoraService bitacora;
    private AnimalService animalService;
    private AdoptanteService adoptanteService;

    public SolicitudService(BitacoraService bitacora, AnimalService animalService, AdoptanteService adoptanteService) {
        this.bitacora = bitacora;
        this.animalService = animalService;
        this.adoptanteService = adoptanteService;
        cargar();
    }

    private void cargar() {
        String[] lineas = PersistenciaManager.leerTodo(ARCHIVO);
        for (int i = 0; i < lineas.length && total < MAX_SOLICITUDES; i++) {
            Solicitud s = Solicitud.fromLine(lineas[i]);
            solicitudes[total] = s;
            total++;
            int num = 0;
            try { num = Integer.parseInt(s.getCodigo().replace("S-", "")); } catch (Exception ignored) {}
            if (num > consecutivo) consecutivo = num;
        }
    }

    public void guardar() {
        String[] lineas = new String[total];
        for (int i = 0; i < total; i++) lineas[i] = solicitudes[i].toLine();
        PersistenciaManager.escribirTodo(ARCHIVO, lineas, total);
    }

    public String generarCodigo() {
        consecutivo++;
        return "S-" + String.format("%03d", consecutivo);
    }

    public int buscarIndicePorCodigo(String codigo) {
        for (int i = 0; i < total; i++) {
            if (solicitudes[i].getCodigo().equalsIgnoreCase(codigo)) return i;
        }
        return -1;
    }

    /** Verifica si el animal ya tiene una solicitud APROBADA vigente */
    private boolean tieneAprobadaVigente(String codigoAnimal) {
        for (int i = 0; i < total; i++) {
            if (solicitudes[i].getCodigoAnimal().equalsIgnoreCase(codigoAnimal)
                    && solicitudes[i].getEstado().equalsIgnoreCase("APROBADA")) {
                return true;
            }
        }
        return false;
    }

    public String registrar(String codigoAnimal, String codigoAdoptante, String fecha, String usuario) {
        Animal animal = animalService.obtenerPorCodigo(codigoAnimal);
        if (animal == null) {
            bitacora.registrarError(usuario, "SOLICITUDES", "VALIDACION", "Animal no registrado: " + codigoAnimal);
            return "El animal indicado no existe";
        }
        if (!animal.getEstadoAdopcion().equalsIgnoreCase("DISPONIBLE")) {
            bitacora.registrarError(usuario, "SOLICITUDES", "VALIDACION",
                    "Animal " + codigoAnimal + " no está DISPONIBLE");
            return "El animal no está disponible para adopción";
        }
        Adoptante adoptante = adoptanteService.obtenerPorCodigo(codigoAdoptante);
        if (adoptante == null) {
            bitacora.registrarError(usuario, "SOLICITUDES", "VALIDACION", "Adoptante no registrado: " + codigoAdoptante);
            return "El adoptante indicado no existe";
        }
        if (!Solicitud.fechaValida(fecha)) {
            bitacora.registrarError(usuario, "SOLICITUDES", "VALIDACION", "Fecha inválida: " + fecha);
            return "Fecha inválida. Formato esperado dd/mm/aaaa";
        }
        if (total >= MAX_SOLICITUDES) return "Capacidad máxima de solicitudes alcanzada";

        String codigo = generarCodigo();
        Solicitud s = new Solicitud(codigo, codigoAnimal, codigoAdoptante, fecha, "PENDIENTE");
        solicitudes[total] = s;
        total++;
        guardar();
        bitacora.registrarAccion(usuario, "SOLICITUDES", "ALTA", "Solicitud " + codigo + " registrada");
        return null;
    }

    /** Cambia el estado de la solicitud aplicando las reglas de negocio */
    public String cambiarEstado(String codigo, String nuevoEstado, String usuario) {
        int idx = buscarIndicePorCodigo(codigo);
        if (idx == -1) {
            bitacora.registrarError(usuario, "SOLICITUDES", "VALIDACION", "Solicitud no encontrada: " + codigo);
            return "Solicitud no encontrada";
        }
        if (!Solicitud.estadoValido(nuevoEstado)) {
            bitacora.registrarError(usuario, "SOLICITUDES", "VALIDACION", "Estado inválido: " + nuevoEstado);
            return "Estado inválido";
        }
        Solicitud s = solicitudes[idx];

        if (nuevoEstado.equalsIgnoreCase("APROBADA")) {
            if (tieneAprobadaVigente(s.getCodigoAnimal())) {
                bitacora.registrarError(usuario, "SOLICITUDES", "VALIDACION",
                        "Animal " + s.getCodigoAnimal() + " ya tiene una solicitud aprobada");
                return "Este animal ya tiene una solicitud aprobada";
            }
            s.setEstado("APROBADA");
            animalService.editarEstado(s.getCodigoAnimal(), null, "ADOPTADO", usuario);
            // Rechazar automaticamente otras solicitudes pendientes del mismo animal
            for (int i = 0; i < total; i++) {
                if (i != idx && solicitudes[i].getCodigoAnimal().equalsIgnoreCase(s.getCodigoAnimal())
                        && solicitudes[i].getEstado().equalsIgnoreCase("PENDIENTE")) {
                    solicitudes[i].setEstado("RECHAZADA");
                    bitacora.registrarAccion(usuario, "SOLICITUDES", "RECHAZAR_AUTO",
                            "Solicitud " + solicitudes[i].getCodigo() + " rechazada automáticamente");
                }
            }
            bitacora.registrarAccion(usuario, "SOLICITUDES", "APROBAR",
                    "Solicitud " + codigo + " aprobada, " + s.getCodigoAnimal() + " pasa a ADOPTADO");
        } else {
            s.setEstado(nuevoEstado.toUpperCase());
            bitacora.registrarAccion(usuario, "SOLICITUDES", "CAMBIO_ESTADO",
                    "Solicitud " + codigo + " cambia a " + nuevoEstado.toUpperCase());
        }
        guardar();
        return null;
    }

    public Solicitud[] listarPendientes() {
        return filtrarPorEstado("PENDIENTE");
    }

    public Solicitud[] filtrarPorEstado(String estado) {
        Solicitud[] tempResultado = new Solicitud[total];
        int cnt = 0;
        for (int i = 0; i < total; i++) {
            if (estado == null || estado.isEmpty() || solicitudes[i].getEstado().equalsIgnoreCase(estado)) {
                tempResultado[cnt] = solicitudes[i];
                cnt++;
            }
        }
        Solicitud[] resultado = new Solicitud[cnt];
        for (int i = 0; i < cnt; i++) resultado[i] = tempResultado[i];
        return resultado;
    }

    public int getTotal() { return total; }

    /** Retorna copia de tamaño exacto (sin huecos nulos) */
    public Solicitud[] getTodas() {
        Solicitud[] copia = new Solicitud[total];
        for (int i = 0; i < total; i++) copia[i] = solicitudes[i];
        return copia;
    }
}

class RescateService {

    public static final int MAX_RESCATES = 200;
    public static final String ARCHIVO = "rescates.txt";

    private Rescate[] rescates = new Rescate[MAX_RESCATES];
    private int total = 0;
    private int consecutivo = 0;

    private BitacoraService bitacora;
    private AnimalService animalService;

    public RescateService(BitacoraService bitacora, AnimalService animalService) {
        this.bitacora = bitacora;
        this.animalService = animalService;
        cargar();
    }

    private void cargar() {
        String[] lineas = PersistenciaManager.leerTodo(ARCHIVO);
        for (int i = 0; i < lineas.length && total < MAX_RESCATES; i++) {
            Rescate r = Rescate.fromLine(lineas[i]);
            rescates[total] = r;
            total++;
            int num = 0;
            try { num = Integer.parseInt(r.getCodigo().replace("R-", "")); } catch (Exception ignored) {}
            if (num > consecutivo) consecutivo = num;
        }
    }

    public void guardar() {
        String[] lineas = new String[total];
        for (int i = 0; i < total; i++) lineas[i] = rescates[i].toLine();
        PersistenciaManager.escribirTodo(ARCHIVO, lineas, total);
    }

    public String generarCodigo() {
        consecutivo++;
        return "R-" + String.format("%03d", consecutivo);
    }

    public int buscarIndicePorCodigo(String codigo) {
        for (int i = 0; i < total; i++) {
            if (rescates[i].getCodigo().equalsIgnoreCase(codigo)) return i;
        }
        return -1;
    }

    public String registrar(String descripcion, String prioridad, String fechaReporte, String usuario) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            bitacora.registrarError(usuario, "RESCATES", "VALIDACION", "Descripción vacía");
            return "La descripción no puede estar vacía";
        }
        if (!Rescate.prioridadValida(prioridad)) {
            bitacora.registrarError(usuario, "RESCATES", "VALIDACION", "Prioridad inválida: " + prioridad);
            return "Prioridad inválida (ALTA, MEDIA o BAJA)";
        }
        if (fechaReporte == null || !fechaReporte.matches("\\d{2}/\\d{2}/\\d{4}")) {
            bitacora.registrarError(usuario, "RESCATES", "VALIDACION", "Fecha inválida: " + fechaReporte);
            return "Fecha inválida. Formato esperado dd/mm/aaaa";
        }
        if (total >= MAX_RESCATES) return "Capacidad máxima de rescates alcanzada";

        String codigo = generarCodigo();
        Rescate r = new Rescate(codigo, descripcion.trim(), prioridad.toUpperCase(), "PENDIENTE", fechaReporte);
        rescates[total] = r;
        total++;
        guardar();
        bitacora.registrarAccion(usuario, "RESCATES", "ALTA", "Rescate " + codigo + " registrado");
        return null;
    }

    /**
     * Atiende un caso de rescate: si no se indica codigoAnimalExistente, genera un
     * nuevo animal reutilizando el consecutivo del rescate (R-009 -> A-009).
     */
    public String atender(String codigoRescate, String codigoAnimalExistente, String nombreAnimal,
                           String especie, int edad, String usuario) {
        int idx = buscarIndicePorCodigo(codigoRescate);
        if (idx == -1) {
            bitacora.registrarError(usuario, "RESCATES", "VALIDACION", "Rescate no encontrado: " + codigoRescate);
            return "Rescate no encontrado";
        }
        Rescate r = rescates[idx];
        if (r.getEstado().equalsIgnoreCase("ATENDIDO")) {
            return "Este rescate ya fue atendido";
        }

        String codigoAnimalFinal;
        if (codigoAnimalExistente != null && !codigoAnimalExistente.trim().isEmpty()) {
            if (animalService.obtenerPorCodigo(codigoAnimalExistente) == null) {
                bitacora.registrarError(usuario, "RESCATES", "VALIDACION",
                        "Animal vinculado no existe: " + codigoAnimalExistente);
                return "El código de animal indicado no existe";
            }
            codigoAnimalFinal = codigoAnimalExistente;
        } else {
            codigoAnimalFinal = animalService.generarCodigoDesdeRescate(codigoRescate);
            String error = animalService.registrar(codigoAnimalFinal, nombreAnimal, especie, edad,
                    "EN_TRATAMIENTO", "DISPONIBLE", usuario);
            if (error != null) return error;
        }

        r.setEstado("ATENDIDO");
        r.setCodigoAnimalVinculado(codigoAnimalFinal);
        guardar();
        bitacora.registrarAccion(usuario, "RESCATES", "ATENDER",
                "Rescate " + codigoRescate + " atendido, vinculado a " + codigoAnimalFinal);
        return null;
    }

    /** Reportes activos ordenados con prioridad ALTA primero */
    public Rescate[] reporteActivos() {
        Rescate[] pendientes = filtrarPorEstado("PENDIENTE");
        // ordenamiento simple por prioridad: ALTA, MEDIA, BAJA
        Rescate[] ordenado = new Rescate[pendientes.length];
        int idx = 0;
        String[] orden = {"ALTA", "MEDIA", "BAJA"};
        for (String p : orden) {
            for (Rescate r : pendientes) {
                if (r.getPrioridad().equalsIgnoreCase(p)) {
                    ordenado[idx] = r;
                    idx++;
                }
            }
        }
        return ordenado;
    }

    public Rescate[] filtrarPorEstado(String estado) {
        Rescate[] tempResultado = new Rescate[total];
        int cnt = 0;
        for (int i = 0; i < total; i++) {
            if (estado == null || estado.isEmpty() || rescates[i].getEstado().equalsIgnoreCase(estado)) {
                tempResultado[cnt] = rescates[i];
                cnt++;
            }
        }
        Rescate[] resultado = new Rescate[cnt];
        for (int i = 0; i < cnt; i++) resultado[i] = tempResultado[i];
        return resultado;
    }

    public int getTotal() { return total; }

    /** Retorna copia de tamaño exacto (sin huecos nulos) */
    public Rescate[] getTodos() {
        Rescate[] copia = new Rescate[total];
        for (int i = 0; i < total; i++) copia[i] = rescates[i];
        return copia;
    }
}

/**
 * Panel de ubicaciones del refugio representado con una matriz de Strings.
 * Dimensiones definidas explicitamente: 2 filas x 10 columnas.
 *   Fila 0 = Zona Perros  (10 espacios, capacidad maxima = 10)
 *   Fila 1 = Zona Gatos   (10 espacios, capacidad maxima = 10)
 * Cada celda contiene el codigo del animal asignado, o cadena vacia si esta libre.
 */
class UbicacionService {

    public static final int FILAS = 2;
    public static final int COLUMNAS = 10;
    public static final String[] NOMBRES_ZONA = {"Zona Perros", "Zona Gatos"};
    public static final String ARCHIVO = "ubicaciones.txt";

    private String[][] matriz = new String[FILAS][COLUMNAS];

    private BitacoraService bitacora;

    public UbicacionService(BitacoraService bitacora) {
        this.bitacora = bitacora;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                matriz[i][j] = "";
            }
        }
        cargar();
    }

    private void cargar() {
        String[] lineas = PersistenciaManager.leerTodo(ARCHIVO);
        for (String linea : lineas) {
            // formato: fila|columna|codigoAnimal
            String[] p = linea.split("\\|", -1);
            if (p.length == 3) {
                try {
                    int f = Integer.parseInt(p[0]);
                    int c = Integer.parseInt(p[1]);
                    if (f >= 0 && f < FILAS && c >= 0 && c < COLUMNAS) {
                        matriz[f][c] = p[2];
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    public void guardar() {
        // contar celdas ocupadas
        int total = 0;
        for (int i = 0; i < FILAS; i++)
            for (int j = 0; j < COLUMNAS; j++)
                if (!matriz[i][j].isEmpty()) total++;

        String[] lineas = new String[total];
        int idx = 0;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (!matriz[i][j].isEmpty()) {
                    lineas[idx] = i + "|" + j + "|" + matriz[i][j];
                    idx++;
                }
            }
        }
        PersistenciaManager.escribirTodo(ARCHIVO, lineas, total);
    }

    public String[][] getMatriz() { return matriz; }

    public boolean celdaValida(int fila, int columna) {
        return fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS;
    }

    public boolean celdaLibre(int fila, int columna) {
        return celdaValida(fila, columna) && matriz[fila][columna].isEmpty();
    }

    /** Numero de espacios disponibles en una zona (fila) */
    public int disponibilidad(int fila) {
        if (fila < 0 || fila >= FILAS) return -1;
        int libres = 0;
        for (int j = 0; j < COLUMNAS; j++) {
            if (matriz[fila][j].isEmpty()) libres++;
        }
        return libres;
    }

    public String asignar(int fila, int columna, String codigoAnimal, String usuario) {
        if (!celdaValida(fila, columna)) {
            bitacora.registrarError(usuario, "UBICACIONES", "VALIDACION",
                    "Celda fuera de rango: [" + fila + "][" + columna + "]");
            return "Celda fuera de rango";
        }
        if (!matriz[fila][columna].isEmpty()) {
            bitacora.registrarError(usuario, "UBICACIONES", "CAPACIDAD",
                    "Celda [" + fila + "][" + columna + "] ya ocupada por " + matriz[fila][columna]);
            return "La celda ya está ocupada por " + matriz[fila][columna];
        }
        // liberar cualquier otra celda donde ya estuviera ese animal
        liberarAnimal(codigoAnimal, usuario, false);
        matriz[fila][columna] = codigoAnimal;
        guardar();
        bitacora.registrarAccion(usuario, "UBICACIONES", "ASIGNAR",
                codigoAnimal + " asignado a [" + fila + "][" + columna + "]");
        return null;
    }

    public String liberar(int fila, int columna, String usuario) {
        if (!celdaValida(fila, columna)) return "Celda fuera de rango";
        if (matriz[fila][columna].isEmpty()) return "La celda ya está libre";
        String codigo = matriz[fila][columna];
        matriz[fila][columna] = "";
        guardar();
        bitacora.registrarAccion(usuario, "UBICACIONES", "LIBERAR",
                codigo + " liberado de [" + fila + "][" + columna + "]");
        return null;
    }

    /** Libera automaticamente la celda ocupada por un animal (ej. al eliminarlo). */
    public void liberarAnimal(String codigoAnimal, String usuario, boolean registrarBitacora) {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (matriz[i][j].equals(codigoAnimal)) {
                    matriz[i][j] = "";
                    if (registrarBitacora) {
                        bitacora.registrarAccion(usuario, "UBICACIONES", "LIBERAR_AUTO",
                                codigoAnimal + " liberado automáticamente de [" + i + "][" + j + "]");
                    }
                }
            }
        }
    }

    public int[] buscarCeldaDeAnimal(String codigoAnimal) {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (matriz[i][j].equals(codigoAnimal)) return new int[]{i, j};
            }
        }
        return null;
    }
}


// ============================================================================
// REPORTES
// ============================================================================

class ReporteHTML {

    private static String estiloBase() {
        return "<style>" +
                "body{font-family:Arial,Helvetica,sans-serif;background:#f4f6f8;margin:0;padding:24px;color:#222;}" +
                "h1{color:#1f4e8c;} h2{color:#2b6cb0;border-bottom:2px solid #cbd5e0;padding-bottom:4px;}" +
                "table{border-collapse:collapse;width:100%;margin-bottom:24px;background:#fff;}" +
                "th,td{border:1px solid #cbd5e0;padding:8px 10px;text-align:left;font-size:14px;}" +
                "th{background:#2b6cb0;color:#fff;}" +
                "tr:nth-child(even){background:#f0f4f8;}" +
                ".footer{color:#888;font-size:12px;margin-top:30px;}" +
                "</style>";
    }

    private static String fechaHoraActual() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    private static String nombreArchivoConFecha(String base) {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return base + "_" + ts + ".html";
    }

    private static void guardar(String nombreArchivo, String html) {
        PersistenciaManager.guardarTextoPlano(nombreArchivo, html);
    }

    public static String generarReporteAnimales(Animal[] animales) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Reporte de Animales</title>")
          .append(estiloBase()).append("</head><body>");
        sb.append("<h1>Centro de Rescate Animal - Reporte de Animales</h1>");
        sb.append("<p>Generado: ").append(fechaHoraActual()).append("</p>");
        sb.append("<table><tr><th>Código</th><th>Nombre</th><th>Especie</th><th>Edad</th>")
          .append("<th>Estado Clínico</th><th>Estado Adopción</th></tr>");
        for (Animal a : animales) {
            sb.append("<tr><td>").append(a.getCodigo()).append("</td><td>").append(a.getNombre())
              .append("</td><td>").append(a.getEspecie()).append("</td><td>").append(a.getEdadEstimada())
              .append("</td><td>").append(a.getEstadoClinico()).append("</td><td>")
              .append(a.getEstadoAdopcion()).append("</td></tr>");
        }
        sb.append("</table><div class='footer'>Total de registros: ").append(animales.length).append("</div>");
        sb.append("</body></html>");
        String nombre = nombreArchivoConFecha("reporte_animales");
        guardar(nombre, sb.toString());
        return nombre;
    }

    public static String generarReporteAdopciones(Solicitud[] solicitudes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Reporte de Adopciones</title>")
          .append(estiloBase()).append("</head><body>");
        sb.append("<h1>Centro de Rescate Animal - Reporte de Adopciones</h1>");
        sb.append("<p>Generado: ").append(fechaHoraActual()).append("</p>");
        sb.append("<table><tr><th>Código</th><th>Animal</th><th>Adoptante</th><th>Fecha</th><th>Estado</th></tr>");
        for (Solicitud s : solicitudes) {
            sb.append("<tr><td>").append(s.getCodigo()).append("</td><td>").append(s.getCodigoAnimal())
              .append("</td><td>").append(s.getCodigoAdoptante()).append("</td><td>").append(s.getFecha())
              .append("</td><td>").append(s.getEstado()).append("</td></tr>");
        }
        sb.append("</table><div class='footer'>Total de solicitudes: ").append(solicitudes.length).append("</div>");
        sb.append("</body></html>");
        String nombre = nombreArchivoConFecha("reporte_adopciones");
        guardar(nombre, sb.toString());
        return nombre;
    }

    public static String generarReporteOcupacion(String[][] matriz, String[] nombresZona) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Reporte de Ocupación</title>")
          .append(estiloBase()).append("</head><body>");
        sb.append("<h1>Centro de Rescate Animal - Ocupación del Refugio</h1>");
        sb.append("<p>Generado: ").append(fechaHoraActual()).append("</p>");
        for (int i = 0; i < matriz.length; i++) {
            int ocupados = 0;
            for (String celda : matriz[i]) if (!celda.isEmpty()) ocupados++;
            sb.append("<h2>").append(nombresZona[i]).append(" (Ocupación: ").append(ocupados)
              .append("/").append(matriz[i].length).append(")</h2>");
            sb.append("<table><tr>");
            for (int j = 0; j < matriz[i].length; j++) sb.append("<th>Espacio ").append(j).append("</th>");
            sb.append("</tr><tr>");
            for (int j = 0; j < matriz[i].length; j++) {
                String celda = matriz[i][j].isEmpty() ? "Libre" : matriz[i][j];
                sb.append("<td>").append(celda).append("</td>");
            }
            sb.append("</tr></table>");
        }
        sb.append("</body></html>");
        String nombre = nombreArchivoConFecha("reporte_ocupacion");
        guardar(nombre, sb.toString());
        return nombre;
    }

    public static String generarReporteBitacora(EntradaBitacora[] acciones, int totalAcciones,
                                                  EntradaBitacora[] errores, int totalErrores) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Reporte de Bitácora</title>")
          .append(estiloBase()).append("</head><body>");
        sb.append("<h1>Centro de Rescate Animal - Bitácora del Sistema</h1>");
        sb.append("<p>Generado: ").append(fechaHoraActual()).append("</p>");

        sb.append("<h2>Bitácora de Acciones</h2>");
        sb.append("<table><tr><th>Fecha/Hora</th><th>Usuario</th><th>Módulo</th><th>Evento</th><th>Descripción</th></tr>");
        for (int i = 0; i < totalAcciones; i++) {
            EntradaBitacora e = acciones[i];
            sb.append("<tr><td>").append(e.getFechaHora()).append("</td><td>").append(e.getUsuario())
              .append("</td><td>").append(e.getModulo()).append("</td><td>").append(e.getEvento())
              .append("</td><td>").append(e.getDetalle()).append("</td></tr>");
        }
        sb.append("</table>");

        sb.append("<h2>Bitácora de Errores</h2>");
        sb.append("<table><tr><th>Fecha/Hora</th><th>Usuario</th><th>Módulo</th><th>Evento</th><th>Motivo</th></tr>");
        for (int i = 0; i < totalErrores; i++) {
            EntradaBitacora e = errores[i];
            sb.append("<tr><td>").append(e.getFechaHora()).append("</td><td>").append(e.getUsuario())
              .append("</td><td>").append(e.getModulo()).append("</td><td>").append(e.getEvento())
              .append("</td><td>").append(e.getDetalle()).append("</td></tr>");
        }
        sb.append("</table>");
        sb.append("</body></html>");
        String nombre = nombreArchivoConFecha("reporte_bitacora");
        guardar(nombre, sb.toString());
        return nombre;
    }
}


// ============================================================================
// UI
// ============================================================================

class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JLabel lblMensaje;
    private JButton btnIngresar;

    private BitacoraService bitacoraService;
    private AutenticacionService autenticacionService;
    private AnimalService animalService;
    private AdoptanteService adoptanteService;
    private SolicitudService solicitudService;
    private RescateService rescateService;
    private UbicacionService ubicacionService;

    public LoginFrame() {
        super("Centro de Rescate Animal - Iniciar Sesión");

        bitacoraService = new BitacoraService();
        autenticacionService = new AutenticacionService(bitacoraService);
        animalService = new AnimalService(bitacoraService);
        adoptanteService = new AdoptanteService(bitacoraService);
        solicitudService = new SolicitudService(bitacoraService, animalService, adoptanteService);
        rescateService = new RescateService(bitacoraService, animalService);
        ubicacionService = new UbicacionService(bitacoraService);

        construirUI();
    }

    private void construirUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Centro de Rescate Animal");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        panel.add(txtUsuario, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtContrasena = new JPasswordField(15);
        panel.add(txtContrasena, gbc);

        btnIngresar = new JButton("Ingresar");
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(btnIngresar, gbc);

        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        gbc.gridy = 4;
        panel.add(lblMensaje, gbc);

        JLabel ayuda = new JLabel("<html><i>admin1 / Refugio2026<br>auxiliar1 / Auxiliar1</i></html>");
        ayuda.setForeground(Color.GRAY);
        gbc.gridy = 5;
        panel.add(ayuda, gbc);

        btnIngresar.addActionListener(this::intentarLogin);
        txtContrasena.addActionListener(this::intentarLogin);

        add(panel);
    }

    private void intentarLogin(ActionEvent e) {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        Usuario u = autenticacionService.login(usuario, contrasena);
        if (u != null) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame main = new MainFrame(u, bitacoraService, autenticacionService, animalService,
                        adoptanteService, solicitudService, rescateService, ubicacionService);
                main.setVisible(true);
            });
        } else {
            lblMensaje.setText(autenticacionService.getMensajeError());
            if (autenticacionService.isSesionBloqueada()) {
                btnIngresar.setEnabled(false);
                txtUsuario.setEnabled(false);
                txtContrasena.setEnabled(false);
            }
        }
    }
}

class MainFrame extends JFrame {

    private Usuario usuarioActual;
    private BitacoraService bitacoraService;
    private AutenticacionService autenticacionService;
    private AnimalService animalService;
    private AdoptanteService adoptanteService;
    private SolicitudService solicitudService;
    private RescateService rescateService;
    private UbicacionService ubicacionService;

    public MainFrame(Usuario usuario, BitacoraService bitacoraService, AutenticacionService autenticacionService,
                      AnimalService animalService, AdoptanteService adoptanteService,
                      SolicitudService solicitudService, RescateService rescateService,
                      UbicacionService ubicacionService) {
        super("Centro de Rescate Animal: Gestión de Refugio y Adopciones — " + usuario.getUsuario() +
                " (" + usuario.getRol() + ")");
        this.usuarioActual = usuario;
        this.bitacoraService = bitacoraService;
        this.autenticacionService = autenticacionService;
        this.animalService = animalService;
        this.adoptanteService = adoptanteService;
        this.solicitudService = solicitudService;
        this.rescateService = rescateService;
        this.ubicacionService = ubicacionService;

        construirUI();
    }

    private void construirUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 650);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Animales", new PanelAnimales(usuarioActual, animalService, ubicacionService));
        tabs.addTab("Adoptantes", new PanelAdoptantes(usuarioActual, adoptanteService));
        tabs.addTab("Solicitudes", new PanelSolicitudes(usuarioActual, solicitudService, animalService, adoptanteService));
        tabs.addTab("Rescates", new PanelRescates(usuarioActual, rescateService, animalService));
        tabs.addTab("Ubicaciones", new PanelUbicaciones(usuarioActual, ubicacionService, animalService));
        tabs.addTab("Reportes", new PanelReportes(usuarioActual, animalService, solicitudService, ubicacionService, bitacoraService));

        JPanel top = new JPanel(new BorderLayout());
        JLabel lblUsuario = new JLabel("  Sesión: " + usuarioActual.getUsuario() + " (" + usuarioActual.getRol() + ")");
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> {
            autenticacionService.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        top.add(lblUsuario, BorderLayout.WEST);
        top.add(btnSalir, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}

class PanelAnimales extends JPanel {

    private Usuario usuarioActual;
    private AnimalService animalService;
    private UbicacionService ubicacionService;

    private DefaultTableModel modelo;
    private JTable tabla;

    private JTextField txtCodigo, txtNombre, txtEdad;
    private JComboBox<String> cmbEspecie, cmbEstadoClinico, cmbEstadoAdopcion;
    private JTextField txtBuscarCodigo, txtBuscarNombre;
    private JComboBox<String> cmbBuscarEspecie, cmbBuscarEstado;

    public PanelAnimales(Usuario usuario, AnimalService animalService, UbicacionService ubicacionService) {
        this.usuarioActual = usuario;
        this.animalService = animalService;
        this.ubicacionService = ubicacionService;
        construirUI();
        refrescarTabla(animalService.listarActivos());
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Formulario de registro ----
        JPanel form = new JPanel(new GridLayout(2, 6, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Registrar / Editar Animal"));

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        cmbEspecie = new JComboBox<>(Animal.ESPECIES_VALIDAS);
        txtEdad = new JTextField();
        cmbEstadoClinico = new JComboBox<>(Animal.ESTADOS_CLINICOS);
        cmbEstadoAdopcion = new JComboBox<>(Animal.ESTADOS_ADOPCION);

        form.add(new JLabel("Código (A-014):")); form.add(txtCodigo);
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Especie:")); form.add(cmbEspecie);
        form.add(new JLabel("Edad estimada:")); form.add(txtEdad);
        form.add(new JLabel("Estado clínico:")); form.add(cmbEstadoClinico);
        form.add(new JLabel("Estado adopción:")); form.add(cmbEstadoAdopcion);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar estado");
        JButton btnEliminar = new JButton("Eliminar (baja lógica)");
        JButton btnLimpiar = new JButton("Limpiar");
        botones.add(btnRegistrar); botones.add(btnActualizar); botones.add(btnEliminar); botones.add(btnLimpiar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        // ---- Busqueda ----
        JPanel buscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscar.setBorder(BorderFactory.createTitledBorder("Buscar"));
        txtBuscarCodigo = new JTextField(8);
        txtBuscarNombre = new JTextField(10);
        cmbBuscarEspecie = new JComboBox<>(new String[]{"", "Perro", "Gato"});
        cmbBuscarEstado = new JComboBox<>(new String[]{"", "DISPONIBLE", "ADOPTADO"});
        JButton btnBuscar = new JButton("Buscar");
        JButton btnListarTodos = new JButton("Listar todos");
        buscar.add(new JLabel("Código:")); buscar.add(txtBuscarCodigo);
        buscar.add(new JLabel("Nombre:")); buscar.add(txtBuscarNombre);
        buscar.add(new JLabel("Especie:")); buscar.add(cmbBuscarEspecie);
        buscar.add(new JLabel("Estado:")); buscar.add(cmbBuscarEstado);
        buscar.add(btnBuscar); buscar.add(btnListarTodos);

        JPanel norteCompleto = new JPanel(new BorderLayout());
        norteCompleto.add(norte, BorderLayout.NORTH);
        norteCompleto.add(buscar, BorderLayout.SOUTH);
        add(norteCompleto, BorderLayout.NORTH);

        // ---- Tabla ----
        modelo = new DefaultTableModel(new String[]{"Código", "Nombre", "Especie", "Edad", "Estado Clínico", "Estado Adopción"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccionEnFormulario());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ---- Acciones ----
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizarEstado());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> buscar());
        btnListarTodos.addActionListener(e -> refrescarTabla(animalService.listarActivos()));
    }

    private void refrescarTabla(Animal[] animales) {
        modelo.setRowCount(0);
        for (Animal a : animales) {
            modelo.addRow(new Object[]{a.getCodigo(), a.getNombre(), a.getEspecie(), a.getEdadEstimada(),
                    a.getEstadoClinico(), a.getEstadoAdopcion()});
        }
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        txtCodigo.setText(modelo.getValueAt(fila, 0).toString());
        txtNombre.setText(modelo.getValueAt(fila, 1).toString());
        cmbEspecie.setSelectedItem(modelo.getValueAt(fila, 2).toString());
        txtEdad.setText(modelo.getValueAt(fila, 3).toString());
        cmbEstadoClinico.setSelectedItem(modelo.getValueAt(fila, 4).toString());
        cmbEstadoAdopcion.setSelectedItem(modelo.getValueAt(fila, 5).toString());
    }

    private void registrar() {
        try {
            int edad = Integer.parseInt(txtEdad.getText().trim());
            String error = animalService.registrar(
                    txtCodigo.getText().trim(),
                    txtNombre.getText().trim(),
                    (String) cmbEspecie.getSelectedItem(),
                    edad,
                    (String) cmbEstadoClinico.getSelectedItem(),
                    (String) cmbEstadoAdopcion.getSelectedItem(),
                    usuarioActual.getUsuario());
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Animal registrado correctamente");
                limpiarFormulario();
                refrescarTabla(animalService.listarActivos());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarEstado() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione o escriba un código de animal");
            return;
        }
        String error = animalService.editarEstado(codigo, (String) cmbEstadoClinico.getSelectedItem(),
                (String) cmbEstadoAdopcion.getSelectedItem(), usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Estado actualizado");
            refrescarTabla(animalService.listarActivos());
        }
    }

    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione o escriba un código de animal");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar (baja lógica) el animal " + codigo + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String error = animalService.eliminar(codigo, usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.WARNING_MESSAGE);
        } else {
            // Regla: si ocupaba una celda en ubicaciones, se libera automaticamente
            ubicacionService.liberarAnimal(codigo, usuarioActual.getUsuario(), true);
            JOptionPane.showMessageDialog(this, "Animal eliminado (baja lógica)");
            limpiarFormulario();
            refrescarTabla(animalService.listarActivos());
        }
    }

    private void buscar() {
        Animal[] resultado = animalService.buscar(txtBuscarCodigo.getText().trim(), txtBuscarNombre.getText().trim(),
                (String) cmbBuscarEspecie.getSelectedItem(), (String) cmbBuscarEstado.getSelectedItem());
        refrescarTabla(resultado);
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
        cmbEspecie.setSelectedIndex(0);
        cmbEstadoClinico.setSelectedIndex(0);
        cmbEstadoAdopcion.setSelectedIndex(0);
        tabla.clearSelection();
    }
}

class PanelAdoptantes extends JPanel {

    private Usuario usuarioActual;
    private AdoptanteService adoptanteService;

    private DefaultTableModel modelo;
    private JTable tabla;
    private JTextField txtCodigo, txtNombre, txtDpi, txtTelefono, txtBuscar;

    public PanelAdoptantes(Usuario usuario, AdoptanteService adoptanteService) {
        this.usuarioActual = usuario;
        this.adoptanteService = adoptanteService;
        construirUI();
        refrescarTabla(adoptanteService.getTodos(), adoptanteService.getTotal());
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(2, 4, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Registrar / Editar Adoptante"));
        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtDpi = new JTextField();
        txtTelefono = new JTextField();
        form.add(new JLabel("Código (AD-007):")); form.add(txtCodigo);
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("DPI (13 dígitos):")); form.add(txtDpi);
        form.add(new JLabel("Teléfono (8 dígitos):")); form.add(txtTelefono);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar (nombre/teléfono)");
        JButton btnLimpiar = new JButton("Limpiar");
        botones.add(btnRegistrar); botones.add(btnEditar); botones.add(btnLimpiar);

        JPanel buscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscar.setBorder(BorderFactory.createTitledBorder("Buscar"));
        txtBuscar = new JTextField(15);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnListar = new JButton("Listar todos");
        buscar.add(new JLabel("Código / Nombre / DPI:")); buscar.add(txtBuscar);
        buscar.add(btnBuscar); buscar.add(btnListar);

        JPanel norte = new JPanel(new BorderLayout());
        JPanel formYBotones = new JPanel(new BorderLayout());
        formYBotones.add(form, BorderLayout.CENTER);
        formYBotones.add(botones, BorderLayout.SOUTH);
        norte.add(formYBotones, BorderLayout.NORTH);
        norte.add(buscar, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"Código", "Nombre", "DPI", "Teléfono"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> registrar());
        btnEditar.addActionListener(e -> editar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnBuscar.addActionListener(e -> refrescarTabla(adoptanteService.buscar(txtBuscar.getText().trim()),
                adoptanteService.buscar(txtBuscar.getText().trim()).length));
        btnListar.addActionListener(e -> refrescarTabla(adoptanteService.getTodos(), adoptanteService.getTotal()));
    }

    private void refrescarTabla(Adoptante[] datos, int total) {
        modelo.setRowCount(0);
        for (int i = 0; i < total; i++) {
            Adoptante a = datos[i];
            modelo.addRow(new Object[]{a.getCodigo(), a.getNombre(), a.getDpi(), a.getTelefono()});
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        txtCodigo.setText(modelo.getValueAt(fila, 0).toString());
        txtNombre.setText(modelo.getValueAt(fila, 1).toString());
        txtDpi.setText(modelo.getValueAt(fila, 2).toString());
        txtTelefono.setText(modelo.getValueAt(fila, 3).toString());
    }

    private void registrar() {
        String error = adoptanteService.registrar(txtCodigo.getText().trim(), txtNombre.getText().trim(),
                txtDpi.getText().trim(), txtTelefono.getText().trim(), usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Adoptante registrado correctamente");
            limpiar();
            refrescarTabla(adoptanteService.getTodos(), adoptanteService.getTotal());
        }
    }

    private void editar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione o escriba un código de adoptante");
            return;
        }
        String error = adoptanteService.editar(codigo, txtNombre.getText().trim(), txtTelefono.getText().trim(),
                usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Adoptante actualizado");
            refrescarTabla(adoptanteService.getTodos(), adoptanteService.getTotal());
        }
    }

    private void limpiar() {
        txtCodigo.setText(""); txtNombre.setText(""); txtDpi.setText(""); txtTelefono.setText("");
        tabla.clearSelection();
    }
}

class PanelSolicitudes extends JPanel {

    private Usuario usuarioActual;
    private SolicitudService solicitudService;
    private AnimalService animalService;
    private AdoptanteService adoptanteService;

    private DefaultTableModel modelo;
    private JTable tabla;
    private JTextField txtCodigoAnimal, txtCodigoAdoptante, txtFecha;
    private JComboBox<String> cmbEstadoNuevo;
    private JTextField txtCodigoSolicitud;

    public PanelSolicitudes(Usuario usuario, SolicitudService solicitudService, AnimalService animalService,
                             AdoptanteService adoptanteService) {
        this.usuarioActual = usuario;
        this.solicitudService = solicitudService;
        this.animalService = animalService;
        this.adoptanteService = adoptanteService;
        construirUI();
        refrescarTabla(solicitudService.getTodas(), solicitudService.getTotal());
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(1, 6, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Registrar Solicitud"));
        txtCodigoAnimal = new JTextField();
        txtCodigoAdoptante = new JTextField();
        txtFecha = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        form.add(new JLabel("Código Animal:")); form.add(txtCodigoAnimal);
        form.add(new JLabel("Código Adoptante:")); form.add(txtCodigoAdoptante);
        form.add(new JLabel("Fecha (dd/mm/aaaa):")); form.add(txtFecha);

        JButton btnRegistrar = new JButton("Registrar solicitud");

        JPanel cambioEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cambioEstado.setBorder(BorderFactory.createTitledBorder("Atender / Cambiar estado"));
        txtCodigoSolicitud = new JTextField(10);
        cmbEstadoNuevo = new JComboBox<>(Solicitud.ESTADOS);
        JButton btnCambiarEstado = new JButton("Aplicar cambio");
        cambioEstado.add(new JLabel("Código Solicitud:")); cambioEstado.add(txtCodigoSolicitud);
        cambioEstado.add(new JLabel("Nuevo estado:")); cambioEstado.add(cmbEstadoNuevo);
        cambioEstado.add(btnCambiarEstado);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{"", "PENDIENTE", "APROBADA", "RECHAZADA", "COMPLETADA"});
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnTodas = new JButton("Ver todas");
        filtro.add(new JLabel("Filtrar por estado:")); filtro.add(cmbFiltro);
        filtro.add(btnFiltrar); filtro.add(btnTodas);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        JPanel formConBoton = new JPanel(new BorderLayout());
        formConBoton.add(form, BorderLayout.CENTER);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(btnRegistrar);
        formConBoton.add(p, BorderLayout.SOUTH);
        norte.add(formConBoton);
        norte.add(cambioEstado);
        norte.add(filtro);
        add(norte, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"Código", "Animal", "Adoptante", "Fecha", "Estado"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) txtCodigoSolicitud.setText(modelo.getValueAt(fila, 0).toString());
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> registrar());
        btnCambiarEstado.addActionListener(e -> cambiarEstado());
        btnFiltrar.addActionListener(e -> refrescarTabla(
                solicitudService.filtrarPorEstado((String) cmbFiltro.getSelectedItem()),
                solicitudService.filtrarPorEstado((String) cmbFiltro.getSelectedItem()).length));
        btnTodas.addActionListener(e -> refrescarTabla(solicitudService.getTodas(), solicitudService.getTotal()));
    }

    private void refrescarTabla(Solicitud[] datos, int total) {
        modelo.setRowCount(0);
        for (int i = 0; i < total; i++) {
            Solicitud s = datos[i];
            modelo.addRow(new Object[]{s.getCodigo(), s.getCodigoAnimal(), s.getCodigoAdoptante(), s.getFecha(), s.getEstado()});
        }
    }

    private void registrar() {
        String error = solicitudService.registrar(txtCodigoAnimal.getText().trim(), txtCodigoAdoptante.getText().trim(),
                txtFecha.getText().trim(), usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Solicitud registrada correctamente");
            txtCodigoAnimal.setText(""); txtCodigoAdoptante.setText("");
            refrescarTabla(solicitudService.getTodas(), solicitudService.getTotal());
        }
    }

    private void cambiarEstado() {
        String codigo = txtCodigoSolicitud.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indique el código de la solicitud");
            return;
        }
        String error = solicitudService.cambiarEstado(codigo, (String) cmbEstadoNuevo.getSelectedItem(),
                usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Estado de solicitud actualizado");
            refrescarTabla(solicitudService.getTodas(), solicitudService.getTotal());
        }
    }
}

class PanelRescates extends JPanel {

    private Usuario usuarioActual;
    private RescateService rescateService;
    private AnimalService animalService;

    private DefaultTableModel modelo;
    private JTable tabla;
    private JTextField txtDescripcion, txtFecha;
    private JComboBox<String> cmbPrioridad;

    private JTextField txtCodigoRescate, txtCodigoAnimalExistente, txtNombreNuevo, txtEdadNuevo;
    private JComboBox<String> cmbEspecieNuevo;

    public PanelRescates(Usuario usuario, RescateService rescateService, AnimalService animalService) {
        this.usuarioActual = usuario;
        this.rescateService = rescateService;
        this.animalService = animalService;
        construirUI();
        refrescarTabla(rescateService.getTodos(), rescateService.getTotal());
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formRegistro = new JPanel(new GridLayout(1, 6, 6, 6));
        formRegistro.setBorder(BorderFactory.createTitledBorder("Registrar caso de rescate"));
        txtDescripcion = new JTextField();
        cmbPrioridad = new JComboBox<>(Rescate.PRIORIDADES);
        txtFecha = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        formRegistro.add(new JLabel("Descripción:")); formRegistro.add(txtDescripcion);
        formRegistro.add(new JLabel("Prioridad:")); formRegistro.add(cmbPrioridad);
        formRegistro.add(new JLabel("Fecha:")); formRegistro.add(txtFecha);
        JButton btnRegistrar = new JButton("Registrar caso");

        JPanel formAtender = new JPanel(new GridLayout(1, 8, 6, 6));
        formAtender.setBorder(BorderFactory.createTitledBorder("Atender caso"));
        txtCodigoRescate = new JTextField();
        txtCodigoAnimalExistente = new JTextField();
        txtNombreNuevo = new JTextField();
        cmbEspecieNuevo = new JComboBox<>(new String[]{"Perro", "Gato"});
        txtEdadNuevo = new JTextField();
        formAtender.add(new JLabel("Código Rescate:")); formAtender.add(txtCodigoRescate);
        formAtender.add(new JLabel("Animal existente (opcional):")); formAtender.add(txtCodigoAnimalExistente);
        formAtender.add(new JLabel("Nombre (si es nuevo):")); formAtender.add(txtNombreNuevo);
        formAtender.add(new JLabel("Especie:")); formAtender.add(cmbEspecieNuevo);
        JButton btnAtender = new JButton("Atender caso");

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(formRegistro, BorderLayout.CENTER);
        JPanel bp1 = new JPanel(new FlowLayout(FlowLayout.LEFT)); bp1.add(btnRegistrar);
        p1.add(bp1, BorderLayout.SOUTH);

        JPanel p2 = new JPanel(new BorderLayout());
        JPanel edadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        edadPanel.add(new JLabel("Edad estimada (si es nuevo):")); edadPanel.add(txtEdadNuevo);
        p2.add(formAtender, BorderLayout.CENTER);
        JPanel bp2 = new JPanel(new BorderLayout());
        bp2.add(edadPanel, BorderLayout.NORTH);
        JPanel bp2b = new JPanel(new FlowLayout(FlowLayout.LEFT)); bp2b.add(btnAtender);
        bp2.add(bp2b, BorderLayout.SOUTH);
        p2.add(bp2, BorderLayout.SOUTH);

        norte.add(p1);
        norte.add(p2);
        add(norte, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"Código", "Descripción", "Prioridad", "Estado", "Fecha", "Animal vinculado"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> registrar());
        btnAtender.addActionListener(e -> atender());
    }

    private void refrescarTabla(Rescate[] datos, int total) {
        modelo.setRowCount(0);
        for (int i = 0; i < total; i++) {
            Rescate r = datos[i];
            modelo.addRow(new Object[]{r.getCodigo(), r.getDescripcion(), r.getPrioridad(), r.getEstado(),
                    r.getFechaReporte(), r.getCodigoAnimalVinculado()});
        }
    }

    private void registrar() {
        String error = rescateService.registrar(txtDescripcion.getText().trim(), (String) cmbPrioridad.getSelectedItem(),
                txtFecha.getText().trim(), usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Caso de rescate registrado");
            txtDescripcion.setText("");
            refrescarTabla(rescateService.getTodos(), rescateService.getTotal());
        }
    }

    private void atender() {
        String codigoRescate = txtCodigoRescate.getText().trim();
        if (codigoRescate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indique el código del rescate a atender");
            return;
        }
        int edad = 0;
        String txtEdad = txtEdadNuevo.getText().trim();
        if (!txtEdad.isEmpty()) {
            try {
                edad = Integer.parseInt(txtEdad);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La edad debe ser un número entero", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        String error = rescateService.atender(codigoRescate, txtCodigoAnimalExistente.getText().trim(),
                txtNombreNuevo.getText().trim(), (String) cmbEspecieNuevo.getSelectedItem(), edad,
                usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Caso atendido y vinculado al módulo de Animales");
            txtCodigoRescate.setText(""); txtCodigoAnimalExistente.setText(""); txtNombreNuevo.setText(""); txtEdadNuevo.setText("");
            refrescarTabla(rescateService.getTodos(), rescateService.getTotal());
        }
    }
}

class PanelUbicaciones extends JPanel {

    private Usuario usuarioActual;
    private UbicacionService ubicacionService;
    private AnimalService animalService;

    private JButton[][] celdas;
    private JLabel lblDisponibilidad;
    private JTextField txtCodigoAnimal;
    private int filaSeleccionada = -1, columnaSeleccionada = -1;

    public PanelUbicaciones(Usuario usuario, UbicacionService ubicacionService, AnimalService animalService) {
        this.usuarioActual = usuario;
        this.ubicacionService = ubicacionService;
        this.animalService = animalService;
        construirUI();
        refrescarMatriz();
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controles.setBorder(BorderFactory.createTitledBorder("Asignar / liberar espacio (seleccione una celda)"));
        txtCodigoAnimal = new JTextField(10);
        JButton btnAsignar = new JButton("Asignar animal a celda seleccionada");
        JButton btnLiberar = new JButton("Liberar celda seleccionada");
        controles.add(new JLabel("Código de animal:")); controles.add(txtCodigoAnimal);
        controles.add(btnAsignar); controles.add(btnLiberar);

        lblDisponibilidad = new JLabel(" ");
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(controles, BorderLayout.NORTH);
        norte.add(lblDisponibilidad, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        JPanel matrizPanel = new JPanel(new GridLayout(UbicacionService.FILAS, 1, 4, 12));
        celdas = new JButton[UbicacionService.FILAS][UbicacionService.COLUMNAS];

        for (int i = 0; i < UbicacionService.FILAS; i++) {
            JPanel fila = new JPanel(new BorderLayout(6, 0));
            fila.setBorder(BorderFactory.createTitledBorder(UbicacionService.NOMBRES_ZONA[i]));
            JPanel celdasFila = new JPanel(new GridLayout(1, UbicacionService.COLUMNAS, 4, 4));
            for (int j = 0; j < UbicacionService.COLUMNAS; j++) {
                final int fi = i, cj = j;
                JButton celda = new JButton();
                celda.setPreferredSize(new Dimension(70, 50));
                celda.addActionListener(e -> seleccionarCelda(fi, cj));
                celdas[i][j] = celda;
                celdasFila.add(celda);
            }
            fila.add(celdasFila, BorderLayout.CENTER);
            matrizPanel.add(fila);
        }
        add(matrizPanel, BorderLayout.CENTER);

        btnAsignar.addActionListener(e -> asignar());
        btnLiberar.addActionListener(e -> liberar());
    }

    private void seleccionarCelda(int fila, int columna) {
        filaSeleccionada = fila;
        columnaSeleccionada = columna;
        for (int i = 0; i < UbicacionService.FILAS; i++)
            for (int j = 0; j < UbicacionService.COLUMNAS; j++)
                celdas[i][j].setBorder(UIManager.getBorder("Button.border"));
        celdas[fila][columna].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        lblDisponibilidad.setText("Celda seleccionada: [" + fila + "][" + columna + "] — " +
                UbicacionService.NOMBRES_ZONA[fila]);
    }

    private void refrescarMatriz() {
        String[][] matriz = ubicacionService.getMatriz();
        for (int i = 0; i < UbicacionService.FILAS; i++) {
            for (int j = 0; j < UbicacionService.COLUMNAS; j++) {
                String codigo = matriz[i][j];
                if (codigo.isEmpty()) {
                    celdas[i][j].setText("Libre");
                    celdas[i][j].setBackground(new Color(200, 235, 200));
                } else {
                    celdas[i][j].setText(codigo);
                    celdas[i][j].setBackground(new Color(235, 200, 200));
                }
                celdas[i][j].setOpaque(true);
            }
        }
        StringBuilder sb = new StringBuilder("Disponibilidad: ");
        for (int i = 0; i < UbicacionService.FILAS; i++) {
            sb.append(UbicacionService.NOMBRES_ZONA[i]).append(" = ")
              .append(ubicacionService.disponibilidad(i)).append("/").append(UbicacionService.COLUMNAS).append("   ");
        }
        lblDisponibilidad.setText(sb.toString());
    }

    private void asignar() {
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione primero una celda en la matriz");
            return;
        }
        String codigo = txtCodigoAnimal.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indique el código del animal a asignar");
            return;
        }
        if (animalService.obtenerPorCodigo(codigo) == null) {
            JOptionPane.showMessageDialog(this, "El código de animal no existe", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String error = ubicacionService.asignar(filaSeleccionada, columnaSeleccionada, codigo, usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Animal asignado correctamente");
            txtCodigoAnimal.setText("");
            refrescarMatriz();
        }
    }

    private void liberar() {
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione primero una celda en la matriz");
            return;
        }
        String error = ubicacionService.liberar(filaSeleccionada, columnaSeleccionada, usuarioActual.getUsuario());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Celda liberada");
            refrescarMatriz();
        }
    }
}

class PanelReportes extends JPanel {

    private Usuario usuarioActual;
    private AnimalService animalService;
    private SolicitudService solicitudService;
    private UbicacionService ubicacionService;
    private BitacoraService bitacoraService;

    private JTextArea areaLog;

    public PanelReportes(Usuario usuario, AnimalService animalService, SolicitudService solicitudService,
                          UbicacionService ubicacionService, BitacoraService bitacoraService) {
        this.usuarioActual = usuario;
        this.animalService = animalService;
        this.solicitudService = solicitudService;
        this.ubicacionService = ubicacionService;
        this.bitacoraService = bitacoraService;
        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel botones = new JPanel(new GridLayout(4, 1, 6, 6));
        botones.setBorder(BorderFactory.createTitledBorder("Generar reportes HTML (carpeta data/)"));
        JButton btnAnimales = new JButton("Reporte de Animales");
        JButton btnAdopciones = new JButton("Reporte de Adopciones");
        JButton btnOcupacion = new JButton("Reporte de Ocupación del Refugio");
        JButton btnBitacora = new JButton("Reporte de Bitácora (Acciones y Errores)");
        botones.add(btnAnimales); botones.add(btnAdopciones); botones.add(btnOcupacion); botones.add(btnBitacora);

        add(botones, BorderLayout.NORTH);

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        add(new JScrollPane(areaLog), BorderLayout.CENTER);

        btnAnimales.addActionListener(e -> {
            String archivo = ReporteHTML.generarReporteAnimales(animalService.listarActivos());
            log("Reporte de animales generado: " + rutaCompleta(archivo));
        });
        btnAdopciones.addActionListener(e -> {
            String archivo = ReporteHTML.generarReporteAdopciones(solicitudService.getTodas());
            log("Reporte de adopciones generado: " + rutaCompleta(archivo));
        });
        btnOcupacion.addActionListener(e -> {
            String archivo = ReporteHTML.generarReporteOcupacion(ubicacionService.getMatriz(), UbicacionService.NOMBRES_ZONA);
            log("Reporte de ocupación generado: " + rutaCompleta(archivo));
        });
        btnBitacora.addActionListener(e -> {
            String archivo = ReporteHTML.generarReporteBitacora(bitacoraService.getAcciones(), bitacoraService.getTotalAcciones(),
                    bitacoraService.getErrores(), bitacoraService.getTotalErrores());
            log("Reporte de bitácora generado: " + rutaCompleta(archivo));
        });
    }

    private String rutaCompleta(String archivo) {
        return new File("data", archivo).getAbsolutePath();
    }

    private void log(String mensaje) {
        areaLog.append(mensaje + "\n");
    }
}


// ============================================================================
// PUNTO DE ENTRADA
// ============================================================================

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

