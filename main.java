import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal encargada de organizar los datos de ventas.
 * Cumple con el requerimiento de llamarse estrictamente "main".
 */
public class main {

    /**
     * Clase interna para almacenar la información de un Producto en memoria.
     */
    static class Producto {
        long id;
        String nombre;
        long precio;
        long cantidadVendida;

        public Producto(long id, String nombre, long precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
            this.cantidadVendida = 0;
        }
    }

    /**
     * Clase interna para almacenar la información de un Vendedor y sus recaudos.
     */
    static class Vendedor {
        String tipoDoc;
        long numDoc;
        String nombres;
        String apellidos;
        long recaudoTotal;

        public Vendedor(String tipoDoc, long numDoc, String nombres, String apellidos) {
            this.tipoDoc = tipoDoc;
            this.numDoc = numDoc;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.recaudoTotal = 0;
        }

        public String getNombreCompleto() {
            return nombres + " " + apellidos;
        }
    }

    /**
     * Método principal que coordina la ejecución de las tareas sin solicitar
     * información al usuario por consola.
     * 
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        try {
            // 1. Cargar datos maestros usando estructuras dinámicas
            Map<Long, Producto> productos = cargarProductos("productos.txt");
            Map<Long, Vendedor> vendedores = cargarVendedores("vendedores.txt");

            // 2. Procesar todos los archivos planos de ventas
            procesarVentas(productos, vendedores);

            // 3. Generar los reportes solicitados
            generarReporteVendedores(vendedores, "reporte_vendedores.csv");
            generarReporteProductos(productos, "reporte_productos.csv");

            // Mostrar mensaje de finalización exitosa
            System.out.println("Ejecución finalizada con éxito. Archivos CSV generados.");

        } catch (Exception e) {
            // Mostrar mensaje de error en caso de que algo salga mal
            System.err.println("Se ha producido un error durante la ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lee el archivo de productos y los almacena en un Mapa (Diccionario).
     */
    private static Map<Long, Producto> cargarProductos(String nombreArchivo) throws IOException {
        Map<Long, Producto> mapa = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length >= 3) {
                    long id = Long.parseLong(datos[0].trim());
                    String nombre = datos[1].trim();
                    long precio = Long.parseLong(datos[2].trim());
                    mapa.put(id, new Producto(id, nombre, precio));
                }
            }
        }
        return mapa;
    }

    /**
     * Lee el archivo de información general de vendedores y los almacena en un
     * Mapa.
     */
    private static Map<Long, Vendedor> cargarVendedores(String nombreArchivo) throws IOException {
        Map<Long, Vendedor> mapa = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length >= 4) {
                    String tipo = datos[0].trim();
                    long numDoc = Long.parseLong(datos[1].trim());
                    String nombres = datos[2].trim();
                    String apellidos = datos[3].trim();
                    mapa.put(numDoc, new Vendedor(tipo, numDoc, nombres, apellidos));
                }
            }
        }
        return mapa;
    }

    /**
     * Explora la carpeta del proyecto y procesa cada archivo de vendedor
     * multiplicando la cantidad de productos por su precio.
     */
    private static void procesarVentas(Map<Long, Producto> productos, Map<Long, Vendedor> vendedores)
            throws IOException {
        File carpeta = new File(".");
        File[] archivos = carpeta.listFiles();

        if (archivos == null)
            return;

        for (File archivo : archivos) {
            String nombreArch = archivo.getName();
            // Identificar archivos de ventas (ej: vendedor_101234567.txt)
            if (nombreArch.startsWith("vendedor_") && nombreArch.endsWith(".txt")
                    && !nombreArch.equals("vendedores.txt")) {

                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String primeraLinea = br.readLine();
                    if (primeraLinea == null)
                        continue;

                    // La primera línea tiene el Tipo y Documento del Vendedor
                    String[] cabecera = primeraLinea.split(";");
                    if (cabecera.length < 2)
                        continue;

                    long idVendedor = Long.parseLong(cabecera[1].trim());
                    Vendedor vendedor = vendedores.get(idVendedor);

                    String lineaVenta;
                    // Leer línea por línea las ventas
                    while ((lineaVenta = br.readLine()) != null) {
                        String[] datosVenta = lineaVenta.split(";");
                        if (datosVenta.length >= 2) {
                            long idProducto = Long.parseLong(datosVenta[0].trim());

                            long cantidad = Long.parseLong(datosVenta[1].trim());

                            Producto producto = productos.get(idProducto);
                            if (producto != null && vendedor != null) {
                                // Acumuladores de datos
                                producto.cantidadVendida += cantidad;
                                vendedor.recaudoTotal += (cantidad * producto.precio);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Punto 3: Crea un archivo plano CSV ordenado descendentemente por dinero
     * recaudado.
     */
    private static void generarReporteVendedores(Map<Long, Vendedor> vendedores, String nombreArchivo)
            throws IOException {
        List<Vendedor> listaVendedores = new ArrayList<>(vendedores.values());

        // Ordenar la lista de mayor a menor según el dinero recaudado
        Collections.sort(listaVendedores, new Comparator<Vendedor>() {
            @Override
            public int compare(Vendedor v1, Vendedor v2) {
                return Long.compare(v2.recaudoTotal, v1.recaudoTotal);
            }
        });

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Vendedor v : listaVendedores) {
                bw.write(v.getNombreCompleto() + ";" + v.recaudoTotal);
                bw.newLine();
            }
        }
    }

    /**
     * Punto 4: Crea un archivo plano CSV ordenado descendentemente por cantidad de
     * productos.
     */
    private static void generarReporteProductos(Map<Long, Producto> productos, String nombreArchivo)
            throws IOException {
        List<Producto> listaProductos = new ArrayList<>(productos.values());

        // Ordenar la lista de mayor a menor según la cantidad vendida
        Collections.sort(listaProductos, new Comparator<Producto>() {
            @Override
            public int compare(Producto p1, Producto p2) {
                return Long.compare(p2.cantidadVendida, p1.cantidadVendida);
            }
        });

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Producto p : listaProductos) {
                // Se solicita nombre y precio, separados por punto y coma.
                bw.write(p.nombre + ";" + p.precio + ";" + p.cantidadVendida);
                bw.newLine();
            }
        }
    }
}