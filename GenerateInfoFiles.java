import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Clase principal para generar archivos planos de prueba.
 */
public class GenerateInfoFiles {

    private static final Random RANDOM = new Random();
    private static final String[] DOCUMENT_TYPES = {"CC", "CE", "TI", "PAS"};
    private static final String[] NAMES = {"Carlos", "María", "Juan", "Ana", "Luis", "Laura", "Pedro", "Sofia", "Diego", "Valentina"};
    private static final String[] LAST_NAMES = {"Gómez", "Rodríguez", "López", "Martínez", "Pérez", "García", "Torres", "Ramírez", "Sánchez", "Díaz"};
    private static final String[] PRODUCT_NAMES = {"Laptop Pro 15", "Mouse Inalámbrico", "Teclado Mecánico", "Monitor 27 IPS", "Audífonos NC", "Silla Ergonómica", "Disco SSD 1TB", "Memoria RAM 16GB", "Cámara Web", "Impresora"};

    public static void main(String[] args) {
        try {
            int productsCount = 10;
            int salesmanCount = 5;
            int maxSalesPerSalesman = 8;

            System.out.println("=== Generando Archivos de Prueba ===");
            createProductsFile(productsCount);
            createSalesManInfoFile(salesmanCount);

            for (int i = 1; i <= salesmanCount; i++) {
                long salesmanId = 100000000L + (i * 1234567L);
                String salesmanName = NAMES[(i - 1) % NAMES.length] + " " + LAST_NAMES[(i - 1) % LAST_NAMES.length];
                int salesCount = 1 + RANDOM.nextInt(maxSalesPerSalesman);
                createSalesMenFile(salesCount, salesmanName, salesmanId);
            }

            System.out.println("=== Finalizado con éxito ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSalesMenFile(int randomSalesCount, String name, long id) {
        String fileName = "vendedor_" + id + ".txt";
        String docType = DOCUMENT_TYPES[RANDOM.nextInt(DOCUMENT_TYPES.length)];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(docType + ";" + id);
            writer.newLine();

            for (int i = 0; i < randomSalesCount; i++) {
                int productId = 1 + RANDOM.nextInt(10);
                int quantitySold = 1 + RANDOM.nextInt(15);
                writer.write(productId + ";" + quantitySold + ";");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createProductsFile(int productsCount) {
        String fileName = "productos.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i <= productsCount; i++) {
                String productName = PRODUCT_NAMES[(i - 1) % PRODUCT_NAMES.length];
                long unitPrice = 15000L + (RANDOM.nextInt(97) * 5000L);
                writer.write(i + ";" + productName + ";" + unitPrice);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createSalesManInfoFile(int salesmanCount) {
        String fileName = "vendedores.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i <= salesmanCount; i++) {
                String docType = DOCUMENT_TYPES[(i - 1) % DOCUMENT_TYPES.length];
                long docNumber = 100000000L + (i * 1234567L);
                String firstName = NAMES[(i - 1) % NAMES.length];
                String lastName = LAST_NAMES[(i - 1) % LAST_NAMES.length];
                writer.write(docType + ";" + docNumber + ";" + firstName + ";" + lastName);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
