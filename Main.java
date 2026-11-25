import java.io.*;
import java.util.*;

/**
 * E-Commerce Product Management System (PS No. 13)
 * Single-file Java Console Application using HashMap, ArrayList, Sorting, Searching, and File Handling.
 */
public class Main {

    /**
     * Product Model Class representing an item in the e-commerce store.
     */
    static class Product {
        private String id;
        private String name;
        private String category;
        private double price;
        private int stock;

        public Product(String id, String name, String category, double price, int stock) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public double getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        /**
         * Formats the price cleanly (drops .0 if it is a whole number, matching test cases).
         */
        public String getFormattedPrice() {
            if (price == (long) price) {
                return String.format("%d", (long) price);
            }
            return String.format("%.2f", price);
        }

        /**
         * Serializes product fields into a pipe-delimited string for products.txt storage.
         */
        public String toFileString() {
            return id + "|" + name + "|" + category + "|" + price + "|" + stock;
        }

        @Override
        public String toString() {
            String stockStatus = (stock == 0) ? "Out of Stock (0)" : String.valueOf(stock);
            return id + " | " + name + " | " + category + " | " + getFormattedPrice() + " | Stock: " + stockStatus;
        }
    }

    /**
     * ProductManager handles all DSA operations and file persistence.
     */
    static class ProductManager {
        // HashMap for O(1) lookup and updates by Product ID (Key = Product ID)
        private HashMap<String, Product> productMap;
        private int nextIdNumber;
        private static final String FILE_NAME = "products.txt";

        public ProductManager() {
            this.productMap = new HashMap<>();
            this.nextIdNumber = 101; // First generated product ID is P101
            loadFromFile();
        }

        /**
         * Loads existing product records from products.txt at application startup.
         */
        public void loadFromFile() {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int maxId = 100;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String[] parts = line.split("\\|");
                    if (parts.length == 5) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        String category = parts[2].trim();
                        double price = Double.parseDouble(parts[3].trim());
                        int stock = Integer.parseInt(parts[4].trim());

                        Product product = new Product(id, name, category, price, stock);
                        productMap.put(id, product);

                        // Keep auto-increment ID counter synchronized with loaded data
                        if (id.startsWith("P") && id.length() > 1) {
                            try {
                                int num = Integer.parseInt(id.substring(1));
                                if (num > maxId) {
                                    maxId = num;
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
                this.nextIdNumber = maxId + 1;
            } catch (IOException | NumberFormatException e) {
                System.out.println("Warning: Could not load previous data from " + FILE_NAME + ": " + e.getMessage());
            }
        }

        /**
         * Saves all products in the HashMap to products.txt upon exit.
         */
        public void saveToFile() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                // Sort by ID before saving for clean file organization
                ArrayList<Product> list = new ArrayList<>(productMap.values());
                list.sort(Comparator.comparing(Product::getId));

                for (Product p : list) {
                    writer.write(p.toFileString());
                    writer.newLine();
                }
                System.out.println("Data saved to " + FILE_NAME + " successfully.");
            } catch (IOException e) {
                System.out.println("Error saving data to " + FILE_NAME + ": " + e.getMessage());
            }
        }

        /**
         * Option 1: Adds a new product with validation (Price > 0, Stock >= 0) and auto-ID (P101+).
         */
        public void addProduct(String name, String category, double price, int stock) {
            if (name == null || name.trim().isEmpty() || category == null || category.trim().isEmpty()) {
                System.out.println("Error: Product name and category cannot be empty.");
                return;
            }
            if (price <= 0) {
                System.out.println("Error: Price must be greater than 0.");
                return;
            }
            if (stock < 0) {
                System.out.println("Error: Stock cannot be negative.");
                return;
            }

            String id = "P" + nextIdNumber++;
            Product product = new Product(id, name.trim(), category.trim(), price, stock);
            productMap.put(id, product);

            System.out.println("Product added. ID: " + id + " | " + product.getName() + " | "
                    + product.getCategory() + " | " + product.getFormattedPrice() + " | Stock: " + product.getStock());
        }

        /**
         * Option 2: Displays all products currently in the store.
         */
        public void viewAllProducts() {
            if (productMap.isEmpty()) {
                System.out.println("No products available in the store.");
                return;
            }

            ArrayList<Product> list = new ArrayList<>(productMap.values());
            list.sort(Comparator.comparing(Product::getId));

            System.out.println("\n--- All Store Products ---");
            for (Product p : list) {
                System.out.println(p);
            }
        }

        /**
         * Option 3a: Searches for a product by its unique ID in O(1) time using HashMap.
         */
        public void searchById(String id) {
            if (id == null || id.trim().isEmpty()) {
                System.out.println("Invalid Product ID.");
                return;
            }
            String formattedId = id.trim().toUpperCase();
            Product product = productMap.get(formattedId);

            if (product != null) {
                System.out.println(product);
            } else {
                System.out.println("Product " + formattedId + " not found.");
            }
        }

        /**
         * Option 3b: Searches for all products matching a given category.
         */
        public void searchByCategory(String category) {
            if (category == null || category.trim().isEmpty()) {
                System.out.println("Invalid Category.");
                return;
            }

            String targetCategory = category.trim();
            ArrayList<Product> matches = new ArrayList<>();

            for (Product p : productMap.values()) {
                if (p.getCategory().equalsIgnoreCase(targetCategory)) {
                    matches.add(p);
                }
            }

            if (matches.isEmpty()) {
                System.out.println("No products found in category: " + targetCategory);
                return;
            }

            // Sort matches by ID for consistent output order
            matches.sort(Comparator.comparing(Product::getId));

            for (Product p : matches) {
                System.out.println(p.getId() + " | " + p.getName() + " | " + p.getFormattedPrice());
            }
        }

        /**
         * Option 4: Copies HashMap values into an ArrayList and sorts by price (low to high).
         */
        public void sortByPriceLowToHigh() {
            if (productMap.isEmpty()) {
                System.out.println("No products available to sort.");
                return;
            }

            // Use ArrayList for sorting as required
            ArrayList<Product> productList = new ArrayList<>(productMap.values());
            productList.sort(Comparator.comparingDouble(Product::getPrice).thenComparing(Product::getId));

            System.out.println("\n--- Products Sorted by Price (Low to High) ---");
            for (int i = 0; i < productList.size(); i++) {
                Product p = productList.get(i);
                System.out.println((i + 1) + ". " + p.getId() + " | " + p.getFormattedPrice()
                        + " (" + p.getName() + ")");
            }
        }

        /**
         * Option 5: Updates the stock quantity of an existing product in O(1) time.
         */
        public void updateStock(String id, int newStock) {
            if (newStock < 0) {
                System.out.println("Error: Stock cannot be negative.");
                return;
            }

            String formattedId = id.trim().toUpperCase();
            Product product = productMap.get(formattedId);

            if (product == null) {
                System.out.println("Product " + formattedId + " not found.");
                return;
            }

            product.setStock(newStock);
            if (newStock == 0) {
                System.out.println("Stock updated. " + formattedId + " is Out of Stock.");
            } else {
                System.out.println("Stock updated. " + formattedId + " new stock: " + newStock);
            }
        }
    }

    /**
     * Console Entry Point & Menu Loop
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProductManager manager = new ProductManager();
        boolean running = true;

        while (running) {
            System.out.println("\n=== E-Commerce Product Management System ===");
            System.out.println("1. Add Product");
            System.out.println("2. View All Products");
            System.out.println("3. Search Product (by ID or Category)");
            System.out.println("4. Sort Products by Price (Low to High)");
            System.out.println("5. Update Stock");
            System.out.println("6. Save & Exit");
            System.out.print("Enter your choice: ");

            String choiceInput = scanner.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                continue;
            }

            switch (choice) {
                case 1:
                    try {
                        System.out.print("Enter Product Name: ");
                        String name = scanner.nextLine();

                        System.out.print("Enter Category: ");
                        String category = scanner.nextLine();

                        System.out.print("Enter Price: ");
                        double price = Double.parseDouble(scanner.nextLine().trim());

                        System.out.print("Enter Stock: ");
                        int stock = Integer.parseInt(scanner.nextLine().trim());

                        manager.addProduct(name, category, price, stock);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid numeric format for price or stock.");
                    }
                    break;

                case 2:
                    manager.viewAllProducts();
                    break;

                case 3:
                    System.out.println("Search by: 1. Product ID   2. Category");
                    System.out.print("Enter search option (1 or 2): ");
                    String searchType = scanner.nextLine().trim();

                    if ("1".equals(searchType)) {
                        System.out.print("Enter Product ID (e.g., P101): ");
                        String id = scanner.nextLine();
                        manager.searchById(id);
                    } else if ("2".equals(searchType)) {
                        System.out.print("Enter Category: ");
                        String category = scanner.nextLine();
                        manager.searchByCategory(category);
                    } else {
                        System.out.println("Invalid search option.");
                    }
                    break;

                case 4:
                    manager.sortByPriceLowToHigh();
                    break;

                case 5:
                    try {
                        System.out.print("Enter Product ID: ");
                        String id = scanner.nextLine();

                        System.out.print("Enter New Stock: ");
                        int newStock = Integer.parseInt(scanner.nextLine().trim());

                        manager.updateStock(id, newStock);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Stock must be a valid integer.");
                    }
                    break;

                case 6:
                    manager.saveToFile();
                    running = false;
                    System.out.println("Exiting E-Commerce Product Management System. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Please select an option from 1 to 6.");
            }
        }

        scanner.close();
    }
}
