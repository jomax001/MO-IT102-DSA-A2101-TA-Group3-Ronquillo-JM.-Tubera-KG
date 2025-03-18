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

public class InventoryManagement {

    // This is the class for each item in our inventory.
    public static class InventoryItem { // Made public for accessibility
        String dateEntered; // When the item was added to the inventory
        String stockLabel; // Label of the stock
        String brand; // Brand of the item
        String engineNumber; // Engine number of the item, it's unique!
        String status; // The status of the item

        // Constructor to create a new item
        public InventoryItem(String dateEntered, String stockLabel, String brand, String engineNumber, String status) {
            this.dateEntered = dateEntered;
            this.stockLabel = stockLabel;
            this.brand = brand;
            this.engineNumber = engineNumber;
            this.status = status;
        }

        // To print the item in a nice format
        @Override
        public String toString() {
            return dateEntered + "," + stockLabel + "," + brand + "," + engineNumber + "," + status;
        }
    }

    // This is the class for our AVL tree data structure.
    // Like a family tree, but for our inventory items
    private static class TreeNode {
        InventoryItem item; // The item itself
        TreeNode left, right; // The "children" on the left and right
        int height; // Height of the node in the tree

        // Constructor to create a new "family member"
        public TreeNode(InventoryItem item) {
            this.item = item;
            this.left = null;
            this.right = null;
            this.height = 1; // New node has height 1
        }
    }

    // This is the root of our tree. Where we start.
    private static TreeNode root = null;
    // This is for quickly finding an item based on its engine number
    private static HashMap<String, InventoryItem> inventoryMap = new HashMap<>();

    private static InventoryRepository inventoryRepository = new InventoryRepository(); // Create an instance of InventoryRepository

    // This is our main method. The program starts here.
    public static void main(String[] args) {
        //loadInventoryFromCSV(); // Load our inventory from the CSV file
        List<InventoryItem> initialInventory = inventoryRepository.loadInventoryFromCSV();
        for (InventoryItem item : initialInventory) {
            root = insertIntoAVL(root, item);
            addItemToInventoryMap(item);
        }

        Scanner scanner = new Scanner(System.in); // To get input from the user
        String choice = ""; // Initialize choice

        do {
            // Let's show the menu
            System.out.println("\nInventory Management System");
            System.out.println("1. Add Item");
            System.out.println("2. Delete Item");
            System.out.println("3. Search Item by Engine Number");
            System.out.println("4. Search Item by Brand");
            System.out.println("5. Display Sorted Inventory");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextLine(); // Let's get the user's choice

            String finalChoice = choice;  // Store choice for confirmChoice method
            if (confirmChoice(scanner, finalChoice)) {  // Pass stored choice to confirmChoice
                switch (finalChoice) {
                    case "1":
                        addItem(scanner); // Let's add an item
                        break;
                    case "2":
                        deleteItem(scanner); // Let's delete an item
                        break;
                    case "3":
                        searchItemByEngineNumber(scanner); // Let's search for an item by engine number
                        break;
                    case "4":
                        searchItemByBrand(scanner); // Let's search for an item by brand
                        break;
                    case "5":
                        displaySortedInventory(); // Let's show the inventory, sorted
                        break;
                    case "6":
                        System.out.println("Exiting..."); // Let's exit
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again."); // Wrong choice!
                }
            } else {
                System.out.println("Operation cancelled.");
                if (finalChoice.equals("6")) {  // Check the stored choice here
                    choice = "";  // Reset choice to re-display main menu
                }
            }
        } while (!choice.equals("6")); // Repeat until the user exits

        scanner.close(); // Let's close the scanner
    }

    // This method asks the user to confirm their choice
    private static boolean confirmChoice(Scanner scanner, String choice) {
        System.out.print("Confirm selection '" + choice + "'? (Yes/No): ");
        String confirmation = scanner.nextLine().trim().toLowerCase(); // Let's get the user's answer
        return confirmation.equals("yes"); // If yes, then true!
    }

    // This method saves the inventory to the CSV file
    private static void saveInventoryToCSV(String newFilePath) {
        List<InventoryItem> sortedList = getSortedInventory();
        inventoryRepository.saveInventoryToCSV(newFilePath, sortedList); //Call saveInventoryToCSV with a new filepath
    }

    // This method adds an item to the inventory
    private static void addItem(Scanner scanner) {
        System.out.println("Enter details for the new item:");
        System.out.print("Date Entered: ");
        String dateEntered = scanner.nextLine(); // Let's get the date entered
        System.out.print("Stock Label: ");
        String stockLabel = scanner.nextLine(); // Let's get the stock label
        System.out.print("Brand: ");
        String brand = scanner.nextLine(); // Let's get the brand
        System.out.print("Engine Number: ");
        String engineNumber = scanner.nextLine(); // Let's get the engine number
        System.out.print("Status: ");
        String status = scanner.nextLine(); // Let's get the status

        InventoryItem newItem = new InventoryItem(dateEntered, stockLabel, brand, engineNumber, status); // Create the new item

        inventoryRepository.addItemToCSV(newItem);

        root = insertIntoAVL(root, newItem);
        addItemToInventoryMap(newItem);

        System.out.println("Item added successfully!"); // Yay! Success!
    }

    // This method deletes an item from the inventory
    private static void deleteItem(Scanner scanner) {
        System.out.print("Enter Engine Number to delete: ");
        String engineNumber = scanner.nextLine(); // Let's get the engine number to delete

        if (!inventoryMap.containsKey(engineNumber)) {
            System.out.println("Item not found!"); // We can't find the item
            return;
        }

        if (confirmDelete(scanner, engineNumber)) {
            root = deleteFromAVL(root, engineNumber);
            removeItemFromInventoryMap(engineNumber);

            String newFilePath = inventoryRepository.generateNewFilePath();
            saveInventoryToCSV(newFilePath);  // Save the updated inventory to CSV
            System.out.println("Item deleted successfully!"); // Yay! Success!
            displaySortedInventory(); // Display the updated inventory table
        } else {
            System.out.println("Deletion cancelled."); // Cancelled it turns out
        }
    }

    // This method confirms if the user is sure they want to delete the item
    private static boolean confirmDelete(Scanner scanner, String engineNumber) {
        System.out.print("Are you sure you want to delete item with Engine Number " + engineNumber + "? (Yes/No): ");
        String confirmation = scanner.nextLine().trim().toLowerCase(); // Let's get the user's answer
        return confirmation.equals("yes"); // If yes, then true!
    }

    // Search for an item by engine number.
    private static void searchItemByEngineNumber(Scanner scanner) {
        System.out.print("Enter Engine Number to search: ");
        String engineNumber = scanner.nextLine();
        InventoryItem item = inventoryMap.get(engineNumber);

        if (item != null) {
            System.out.println("Item found with Engine Number " + engineNumber + ":");
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.printf("%-12s %-12s %-10s %-15s %-10s\n", "Date Entered", "Stock Label", "Brand", "Engine Number", "Status");
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.printf("%-12s %-12s %-10s %-15s %-10s\n", item.dateEntered, item.stockLabel, item.brand, item.engineNumber, item.status);
            System.out.println("-----------------------------------------------------------------------------------");
        } else {
            System.out.println("Item not found!"); // We can't find the item
        }
    }

    //New Method for Search by Brand
    private static void searchItemByBrand(Scanner scanner) {
        System.out.print("Enter Brand to search: ");
        String brand = scanner.nextLine();
        boolean found = false;

        System.out.println("Items found with Brand " + brand + ":");
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.printf("%-12s %-12s %-10s %-15s %-10s\n", "Date Entered", "Stock Label", "Brand", "Engine Number", "Status");
        System.out.println("-----------------------------------------------------------------------------------");
        for (InventoryItem item : inventoryMap.values()) {
                if (item != null && item.brand.equalsIgnoreCase(brand)) {
                    System.out.printf("%-12s %-12s %-10s %-15s %-10s\n", item.dateEntered, item.stockLabel, item.brand, item.engineNumber, item.status);
                    found = true;
                }
        }

        if (!found) {
            System.out.println("No items found with that brand."); // We can't find the item
        }

        System.out.println("-----------------------------------------------------------------------------------");
    }

    // This method shows the inventory, sorted
    private static void displaySortedInventory() {
        List<InventoryItem> sortedList = getSortedInventory();

        System.out.println("-----------------------------------------------------------------------------------");
        System.out.printf("%-12s %-12s %-10s %-15s %-10s\n", "Date Entered", "Stock Label", "Brand", "Engine Number", "Status");
        System.out.println("-----------------------------------------------------------------------------------");
        for (InventoryItem item : sortedList) {
            System.out.printf("%-12s %-12s %-10s %-15s %-10s\n", item.dateEntered, item.stockLabel, item.brand, item.engineNumber, item.status);
        }

        System.out.println("-----------------------------------------------------------------------------------");
    }

    //This is the method we use to display the inventory list
    private static void displayInventoryTable() {
        List<InventoryItem> inventoryList = new ArrayList<>(); // Let's make a list
        inOrderTraversal(root, inventoryList); // Let's get the items from the "family tree"

        //Let's show the header
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.printf("%-12s %-12s %-10s %-15s %-10s\n", "Date Entered", "Stock Label", "Brand", "Engine Number", "Status");
        System.out.println("-----------------------------------------------------------------------------------");

        //Let's show each item in the list
        for (InventoryItem item : inventoryList) {
            System.out.printf("%-12s %-12s %-10s %-15s %-10s\n", item.dateEntered, item.stockLabel, item.brand, item.engineNumber, item.status);
        }

        System.out.println("-----------------------------------------------------------------------------------");
    }

    // --------------------------------------------------------------
    //  Helper Methods For Hash Map Implementation
    // --------------------------------------------------------------
    private static void addItemToInventoryMap(InventoryItem item) {
        inventoryMap.put(item.engineNumber, item);
    }

    private static void removeItemFromInventoryMap(String engineNumber) {
        inventoryMap.remove(engineNumber);
    }

    // --------------------------------------------------------------
    //   Methods for AVL Tree Implementation
    // --------------------------------------------------------------

    private static int height(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    private static TreeNode rightRotate(TreeNode y) {
        TreeNode x = y.left;
        TreeNode T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private static TreeNode leftRotate(TreeNode x) {
        TreeNode y = x.right;
        TreeNode T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private static int getBalance(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    private static TreeNode insertIntoAVL(TreeNode node, InventoryItem item) {
        if (node == null) {
            return new TreeNode(item);
        }

        if (item.engineNumber.compareTo(node.item.engineNumber) < 0) {
            node.left = insertIntoAVL(node.left, item);
        } else if (item.engineNumber.compareTo(node.item.engineNumber) > 0) {
            node.right = insertIntoAVL(node.right, item);
        } else {
            // Duplicate keys not allowed
            return node;
        }

        // Update height of current node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Get the balance factor
        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && item.engineNumber.compareTo(node.left.item.engineNumber) < 0) {
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && item.engineNumber.compareTo(node.right.item.engineNumber) > 0) {
            return leftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && item.engineNumber.compareTo(node.left.item.engineNumber) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && item.engineNumber.compareTo(node.right.item.engineNumber) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private static TreeNode deleteFromAVL(TreeNode root, String engineNumber) {
        if (root == null) {
            return null;
        }

        if (engineNumber.compareTo(root.item.engineNumber) < 0) {
            root.left = deleteFromAVL(root.left, engineNumber);
        } else if (engineNumber.compareTo(root.item.engineNumber) > 0) {
            root.right = deleteFromAVL(root.right, engineNumber);
        } else {
            if ((root.left == null) || (root.right == null)) {
                TreeNode temp = null;
                if (temp == root.left)
                    temp = root.right;
                else
                    temp = root.left;

                if (temp == null) {
                    temp = root;
                    root = null;
                } else
                    root = temp;
            } else {
                TreeNode temp = minValueNode(root.right);
                root.item = temp.item;
                root.right = deleteFromAVL(root.right, temp.item.engineNumber);
            }
        }

        if (root == null)
            return root;

        root.height = Math.max(height(root.left), height(root.right)) + 1;

        int balance = getBalance(root);

        // Left Left Case
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);

        // Left Right Case
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        // Right Right Case
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);

        // Right Left Case
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    private static TreeNode minValueNode(TreeNode node) {
        TreeNode current = node;
        while (current.left != null)
            current = current.left;

        return current;
    }

    private static void inOrderTraversal(TreeNode root, List<InventoryItem> inventoryList) {
        if (root != null) {
            inOrderTraversal(root.left, inventoryList);
            inventoryList.add(root.item);
            inOrderTraversal(root.right, inventoryList);
        }
    }

    private static List<InventoryItem> getSortedInventory() {
        List<InventoryItem> sortedList = new ArrayList<>();
        inOrderTraversal(root, sortedList);
        return sortedList;
    }
}
