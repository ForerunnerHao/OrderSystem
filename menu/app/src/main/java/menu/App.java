package menu;

import menu.auth.LocalStorage;
import menu.controller.CommandParser;
import menu.controller.SystemController;
import menu.dao.MySQLOpenHelper;
import menu.pojo.Message;
import menu.utils.ColorfulConsole;


import java.util.Scanner;

public class App {
    private static Scanner scanner = new Scanner(System.in);
    private static final String EXIT_COMMAND = "exit"; 

    public static void main(String[] args) {
        start();

        //A dead loop that repeatedly takes user input and ends if a predefined exit value is entered.
        while (true) {
            System.out.print(">>> ");
            if (!scanner.hasNextLine()){
                System.out.println(ColorfulConsole.printFailInfo("Null value, illegal input exit system."));
                return;
            }
            String command = scanner.nextLine();

            // Exit system
            if (EXIT_COMMAND.equalsIgnoreCase(command)) {
                Message result = SystemController.getInstance().exit();
                if (result.getCode() == Message.MESSAGE_OK){
                    System.out.println(result.getMessage());
                    break;
                }else{
                    System.out.println(result.getMessage());
                }
            }
            try {
                CommandParser.getInstance().execute(command);
            } catch (Exception e) {
                System.out.println("Error executing command: " + e.getMessage());
            }
        }

        cleanup();
    }
    /**
     * Starts the program, prints the relevant prompts,
     * and lets the user select the login role
     */
    private static void start() {
        System.out.println("Welcome to Online Menu System!!!");

        //Load all data from database
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ALL);

        while (true) {
            System.out.print("Please choose your role:\n1. Customer\n2. Admin\nPlease enter number(default is customer): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.isEmpty() || "1".equals(input) || "customer".equals(input)) {
                CommandParser.getInstance().execute("sys help");
                return;
            } else if ("2".equals(input) || "admin".equals(input)) {
                CommandParser.getInstance().execute("sys login");
                return;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    /**
     * Disconnect the connection pool and close scanner,
     * eventually printing the exit prompt message
     */
    private static void cleanup() {
        System.out.println("HikariDataSource closing...");
        MySQLOpenHelper.getInstance().closeDataSource();
        System.out.println("Scanner closing...");
        scanner.close();
        System.out.println("Goodbye!");
    }

    /**
     * Get scanner obj from App.java
     * @return get scanner object
     */
    public static Scanner getScanner() {
        return scanner;
    }

    /**
     * Test only
     * @param newScanner a new scanner
     */
    public static void setScanner(Scanner newScanner) {
        scanner = newScanner;
    }
}
