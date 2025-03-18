/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jomax
 */
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class InventoryRepository {

    private static final String CSV_FILE_PATH = "C:\\Users\\Jomax\\OneDrive\\Documents\\NetBeansProjects\\InventoryManagement2\\src\\MotorPH Inventory Data2.csv";

    // Load inventory from CSV file
    public List<InventoryManagement.InventoryItem> loadInventoryFromCSV() {
        List<InventoryManagement.InventoryItem> inventoryList = new ArrayList<>();
        File csvFile = new File(CSV_FILE_PATH);

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile(); // Create the file if it doesn't exist
                // Write header row
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
                    bw.write("Date Entered,Stock Label,Brand,Engine Number,Status");
                    bw.newLine();
                }
                System.out.println("CSV file created with header row.");
            } catch (IOException e) {
                System.err.println("Error creating CSV file: " + e.getMessage());
                e.printStackTrace();
                return inventoryList; // Stop loading if we can't create the file
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
             if(csvFile.length() > 0) {
                br.readLine();
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    InventoryManagement.InventoryItem item = new InventoryManagement.InventoryItem(data[0], data[1], data[2], data[3], data[4]);
                    inventoryList.add(item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading inventory from CSV: " + e.getMessage());
            e.printStackTrace();
        }
        return inventoryList;
    }

    // Save inventory to CSV file
    public void saveInventoryToCSV(String newFilePath, List<InventoryManagement.InventoryItem> inventoryList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(newFilePath))) {
            bw.write("Date Entered,Stock Label,Brand,Engine Number,Status"); // Write the header row
            bw.newLine(); // Start a new line

            for (InventoryManagement.InventoryItem item : inventoryList) {
                bw.write(item.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving inventory to CSV: " + e.getMessage());
            e.printStackTrace(); // Report any errors.
        }
    }

    // Add new Item to CSV File
    public void addItemToCSV(InventoryManagement.InventoryItem newItem) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, true))) {
            writer.newLine();
            writer.write(newItem.toString());
        } catch (IOException e) {
            System.err.println("Error adding item to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to generate a new file path with timestamp
    public String generateNewFilePath() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String baseName = CSV_FILE_PATH.substring(0, CSV_FILE_PATH.lastIndexOf("."));
        String extension = CSV_FILE_PATH.substring(CSV_FILE_PATH.lastIndexOf("."));
        return baseName + "_" + timeStamp + extension;
    }
}
