/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedReader;

import java.io.FileReader;

import java.io.FileNotFoundException;

/*
 * Tarea 3: Sistema de Gestion de Notas.
 * Permite agregar estudiantes, mostrarlos ordenados por nota (Bubble Sort
 * manual), calcular el promedio con una funcion recursiva, y guardar/cargar
 * los datos desde un archivo de texto plano (notas.txt).
 * Restriccion: no se usa ArrayList, Collections ni ninguna clase de
 * java.util para ordenamiento o calculo; todo se maneja con arreglos.
 */
public class SistemaNotas {

    // Capacidad maxima fija de estudiantes (arreglos estaticos, sin Collections)
    static final int CAPACIDAD_MAXIMA = 100;

    // Arreglos paralelos: la posicion i de "nombres" corresponde a la
    // posicion i de "notas" para el mismo estudiante.
    static String[] nombres = new String[CAPACIDAD_MAXIMA];
    static double[] notas = new double[CAPACIDAD_MAXIMA];
    static int cantidadEstudiantes = 0; // cuantos estudiantes hay registrados actualmente

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            mostrarMenu();
            int opcion = leerOpcionValidada(sc);

            switch (opcion) {
                case 1:
                    agregarEstudiante(sc);
                    break;
                case 2:
                    mostrarEstudiantesOrdenados();
                    break;
                case 3:
                    calcularPromedio();
                    break;
                case 4:
                    guardarEnArchivo();
                    break;
                case 5:
                    cargarDesdeArchivo();
                    break;
                case 6:
                    System.out.println("Hasta luego.");
                    continuar = false;
                    break;
                default:
                    System.out.println("Opcion invalida. Intente de nuevo.");
            }
            System.out.println();
        }

        sc.close();
    }

    /**
     * Imprime el menu principal del sistema.
     */
    public static void mostrarMenu() {
        System.out.println("===== SISTEMA DE NOTAS =====");
        System.out.println("1. Agregar estudiante");
        System.out.println("2. Mostrar estudiantes (ordenados por nota)");
        System.out.println("3. Calcular promedio del grupo");
        System.out.println("4. Guardar datos en archivo");
        System.out.println("5. Cargar datos desde archivo");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    /**
     * Lee la opcion del menu validando que sea un numero entero.
     * Si no lo es, devuelve -1 para que el menu muestre "opcion invalida".
     */
    public static int leerOpcionValidada(Scanner sc) {
        try {
            int opcion = Integer.parseInt(sc.nextLine().trim());
            return opcion;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * OPCION 1: Solicita nombre y nota de un estudiante, valida con
     * try-catch que la nota sea numerica y este entre 0 y 100.
     * Si la validacion falla, no se agrega nada y se regresa al menu.
     */
    public static void agregarEstudiante(Scanner sc) {
        if (cantidadEstudiantes >= CAPACIDAD_MAXIMA) {
            System.out.println("No se pueden agregar mas estudiantes (limite alcanzado).");
            return;
        }

        System.out.print("Ingrese el nombre del estudiante: ");
        String nombre = sc.nextLine().trim();

        System.out.print("Ingrese la nota (0-100): ");
        String entrada = sc.nextLine().trim();

        try {
            double nota = Double.parseDouble(entrada);

            if (nota < 0 || nota > 100) {
                // Se lanza una excepcion propia para reusar el mismo bloque catch
                throw new IllegalArgumentException("La nota debe estar entre 0 y 100.");
            }

            // Si pasa la validacion, se guarda en los arreglos paralelos
            nombres[cantidadEstudiantes] = nombre;
            notas[cantidadEstudiantes] = nota;
            cantidadEstudiantes++;

            System.out.println("Estudiante agregado correctamente: " + nombre + " - " + nota);

        } catch (NumberFormatException e) {
            System.out.println("Error: la nota ingresada no es un numero valido. No se agrego el estudiante.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + " No se agrego el estudiante.");
        }
    }

    /**
     * OPCION 2: Ordena una copia de los arreglos de mayor a menor usando
     * Bubble Sort implementado manualmente, y muestra el listado numerado.
     */
    public static void mostrarEstudiantesOrdenados() {
        if (cantidadEstudiantes == 0) {
            System.out.println("No hay estudiantes registrados.");
            return;
        }

        // Se trabaja sobre copias para no alterar el orden original de ingreso
        String[] nombresOrdenados = new String[cantidadEstudiantes];
        double[] notasOrdenadas = new double[cantidadEstudiantes];
        for (int i = 0; i < cantidadEstudiantes; i++) {
            nombresOrdenados[i] = nombres[i];
            notasOrdenadas[i] = notas[i];
        }

        bubbleSortDescendente(nombresOrdenados, notasOrdenadas);

        System.out.println("--- Estudiantes ordenados por nota (mayor a menor) ---");
        for (int i = 0; i < cantidadEstudiantes; i++) {
            System.out.println((i + 1) + ". " + nombresOrdenados[i] + " - " + notasOrdenadas[i]);
        }
    }

    /**
     * Bubble Sort manual (de mayor a menor). Recorre el arreglo varias
     * "pasadas"; en cada pasada compara elementos adyacentes y los
     * intercambia si estan en el orden incorrecto (el menor antes que el
     * mayor). Tras cada pasada completa, el valor mas pequeño restante
     * "burbujea" hacia el final del arreglo. Se repite hasta que una
     * pasada completa no realice ningun intercambio (arreglo ya ordenado).
     */
    public static void bubbleSortDescendente(String[] nombresArr, double[] notasArr) {
        int n = notasArr.length;
        for (int pasada = 0; pasada < n - 1; pasada++) {
            boolean huboIntercambio = false;

            // En cada pasada se recorre hasta el limite no ordenado aun
            for (int j = 0; j < n - 1 - pasada; j++) {
                if (notasArr[j] < notasArr[j + 1]) {
                    // Intercambio de notas
                    double tempNota = notasArr[j];
                    notasArr[j] = notasArr[j + 1];
                    notasArr[j + 1] = tempNota;

                    // Intercambio del nombre correspondiente (arreglos paralelos)
                    String tempNombre = nombresArr[j];
                    nombresArr[j] = nombresArr[j + 1];
                    nombresArr[j + 1] = tempNombre;

                    huboIntercambio = true;
                }
            }

            // Si en esta pasada no hubo intercambios, el arreglo ya esta ordenado
            if (!huboIntercambio) {
                break;
            }
        }
    }

    /**
     * OPCION 3: Calcula el promedio del grupo usando una funcion recursiva
     * para sumar las notas.
     */
    public static void calcularPromedio() {
        if (cantidadEstudiantes == 0) {
            System.out.println("No hay estudiantes registrados.");
            return;
        }

        double suma = sumaRecursiva(notas, cantidadEstudiantes - 1);
        double promedio = suma / cantidadEstudiantes;

        System.out.printf("Promedio del grupo: %.2f%n", promedio);
    }

    /**
     * Funcion recursiva que suma las notas del arreglo desde el indice 0
     * hasta el indice "indice" (inclusive).
     * Caso base: si el indice es 0, se devuelve el valor de esa unica nota.
     * Caso recursivo: se suma la nota actual con el resultado de llamar a
     * la misma funcion para el indice anterior (indice - 1). Asi el metodo
     * se llama a si mismo reduciendo el problema hasta llegar al caso base.
     */
    public static double sumaRecursiva(double[] arreglo, int indice) {
        if (indice == 0) {
            return arreglo[0]; // caso base
        }
        return arreglo[indice] + sumaRecursiva(arreglo, indice - 1); // llamada recursiva
    }

    /**
     * OPCION 4: Guarda todos los estudiantes en notas.txt, uno por linea,
     * en el formato NombreEstudiante,85
     */
    public static void guardarEnArchivo() {
        if (cantidadEstudiantes == 0) {
            System.out.println("No hay estudiantes registrados para guardar.");
            return;
        }

        try (FileWriter writer = new FileWriter("notas.txt")) {
            for (int i = 0; i < cantidadEstudiantes; i++) {
                writer.write(nombres[i] + "," + notas[i]);
                writer.write(System.lineSeparator());
            }
            System.out.println("Datos guardados exitosamente en notas.txt");
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    }

    /**
     * OPCION 5: Lee notas.txt linea por linea y carga los datos al sistema.
     * Si el archivo no existe, se informa al usuario sin lanzar una
     * excepcion no controlada.
     */
    public static void cargarDesdeArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader("notas.txt"))) {
            cantidadEstudiantes = 0; // se reinicia la lista antes de cargar
            String linea;
            int cargados = 0;

            while ((linea = reader.readLine()) != null && cantidadEstudiantes < CAPACIDAD_MAXIMA) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    try {
                        String nombre = partes[0].trim();
                        double nota = Double.parseDouble(partes[1].trim());

                        nombres[cantidadEstudiantes] = nombre;
                        notas[cantidadEstudiantes] = nota;
                        cantidadEstudiantes++;
                        cargados++;
                    } catch (NumberFormatException e) {
                        System.out.println("Linea ignorada por formato invalido: " + linea);
                    }
                }
            }

            System.out.println("Se cargaron " + cargados + " estudiantes desde notas.txt");

        } catch (FileNotFoundException e) {
            System.out.println("El archivo notas.txt no existe. No se cargaron datos.");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}
