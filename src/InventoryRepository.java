import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class InventoryRepository {

    private static final String CSV_FILE_PATH = "C:\\Users\\Jomax\\OneDrive\\Documents\\NetBeansProjects\\InventoryManagement2\\src\\MotorPH Inventory Data2.csv";

    // Load inventory from CSV file
    public List<InventoryManagement.InventoryItem> loadInventoryFromCSV() {
        List<InventoryManagement.InventoryItem> inventoryList = new ArrayList<>(); // Create a new list to store inventory items
        File csvFile = new File(CSV_FILE_PATH); // Create a file object for the CSV file

        // Check if the CSV file exists
        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile(); // Create the file if it doesn't exist
                // Write header row
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
                    bw.write("Date Entered,Stock Label,Brand,Engine Number,Status"); // Write the header
                    bw.newLine(); // Add a new line after the header
                }
                System.out.println("CSV file created with header row.");
            } catch (IOException e) {
                System.err.println("Error creating CSV file: " + e.getMessage()); // Print error message if file creation fails
                e.printStackTrace(); // Print the stack trace for debugging
                return inventoryList; // Stop loading if we can't create the file
            }
        }

        // Try to read the CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
             if(csvFile.length() > 0) {
                br.readLine();
            }
            String line; // Variable to hold each line read from the file
            // Read the file line by line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // Split the line into data fields
                // Check if the line contains the expected number of fields
                if (data.length == 5) {
                    // Create a new inventory item with the data from the CSV
                    InventoryManagement.InventoryItem item = new InventoryManagement.InventoryItem(data[0], data[1], data[2], data[3], data[4]);
                    inventoryList.add(item); // Add the item to the list
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading inventory from CSV: " + e.getMessage()); // Print error message if file reading fails
            e.printStackTrace(); // Print the stack trace for debugging
        }
        return inventoryList; // Return the list of inventory items
    }

    // Save inventory to CSV file
    public void saveInventoryToCSV(String newFilePath, List<InventoryManagement.InventoryItem> inventoryList) {
        // Try to write to the CSV file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(newFilePath))) {
            bw.write("Date Entered,Stock Label,Brand,Engine Number,Status"); // Write the header row
            bw.newLine(); // Start a new line

            // Loop through the inventory list
            for (InventoryManagement.InventoryItem item : inventoryList) {
                bw.write(item.toString()); // Write the item to the file
                bw.newLine(); // Start a new line
            }
        } catch (IOException e) {
            System.err.println("Error saving inventory to CSV: " + e.getMessage()); // Print error message if file writing fails
            e.printStackTrace(); // Report any errors.
        }
    }

    // Add new Item to CSV File
    public void addItemToCSV(InventoryManagement.InventoryItem newItem) {
        // Try to write to the CSV file in append mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, true))) {
            writer.newLine(); // Add a new line
            writer.write(newItem.toString()); // Write the item to the file
        } catch (IOException e) {
            System.err.println("Error adding item to CSV: " + e.getMessage()); // Print error message if file writing fails
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }

    // Helper method to generate a new file path with timestamp
    public String generateNewFilePath() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // Get the current timestamp
        String baseName = CSV_FILE_PATH.substring(0, CSV_FILE_PATH.lastIndexOf(".")); // Get the base name of the file
        String extension = CSV_FILE_PATH.substring(CSV_FILE_PATH.lastIndexOf(".")); // Get the file extension
        return baseName + "_" + timeStamp + extension; // Return the new file path
    }
}
