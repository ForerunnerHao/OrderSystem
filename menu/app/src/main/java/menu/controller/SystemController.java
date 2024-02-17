package menu.controller;

import menu.App;

import menu.auth.LocalStorage;
import menu.auth.ROLE;
import menu.auth.UserRole;
import menu.dao.OrderDAO;
import menu.dao.SystemDAO;
import java.util.Scanner;
import menu.pojo.Admin;
import menu.pojo.Message;
import menu.pojo.Order;
import menu.utils.ColorfulConsole;

public class SystemController {
    private static final SystemController INSTANCE = new SystemController();
    
    private SystemController() {}
    
    public static SystemController getInstance() {
        return INSTANCE;
    }

    public void login(){
        String username, password;
        if (!UserRole.getInstance().isAdmin()){
            Scanner scanner = App.getScanner();

            System.out.print("Username: ");
            if(scanner.hasNextLine()){
                username = scanner.nextLine().trim();
            }else {
                System.out.println(ColorfulConsole.printFailInfo("Invalid input"));
                return;
            }

            System.out.print("Password: ");
            if(scanner.hasNextLine()){
                password = scanner.nextLine().trim();
            }else {
                System.out.println(ColorfulConsole.printFailInfo("Invalid input"));
                return;
            }

            if (username.isEmpty() || password.isEmpty()){
                System.out.println(ColorfulConsole.printFailInfo("Empty input"));
                return;
            }

            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(password);

            boolean isValid = SystemDAO.getInstance().getUser(admin);

            if (isValid) {
                System.out.println(ColorfulConsole.printSuccessInfo("Login Successful!"));
                UserRole.getInstance().setRole(ROLE.ADMIN);
                AdminController.getInstance().dashboard();
            } else {
                System.out.println(ColorfulConsole.printFailInfo("Invalid Username or Password!"));
            }
        }else {
            System.out.println("Your role is Admin");
        }
    }

    public void getHelp(){
        System.out.println("Help Menu");
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-15s %-40s%n", "Command", "Operation", "Description");
        System.out.println("--------------------------------------------------------------------------------------");

        printHelpRow("menu", "show", "Show all items on menu");
        printHelpRow("menu", "view id", "View item's detail- description, ingredient, weight, price");
        printHelpRow("order", "add id", "Add the specified item into the order cart");
        printHelpRow("order", "show", "Show all items on current order");
        printHelpRow("order", "remove id", "Remove item from order");
        printHelpRow("order", "update id", "Update the quantity of item");
        printHelpRow("order", "pay", "Pay order");
        printHelpRow("history", "show", "Display historical orders, order creation time and Status.");
        printHelpRow("history", "view id", "Check historical order's detail");
        printHelpRow("sys", "role", "Show current role");
        printHelpRow("sys", "login", "Login");
        printHelpRow("sys", "help", "Help menu");
        printHelpRow("sys", "reboot", "Reload data");
        printHelpRow("exit", "", "Exit system");

        System.out.println("--------------------------------------------------------------------------------------");
    }

    public static void printHelpRow(String command, String operation, String description) {
        System.out.printf("%-10s %-15s %-40s%n", command, operation, description);
    }

    public void getRole(){
        System.out.println("Current Role: " + (UserRole.getInstance().isAdmin() ? "Admin" : "Customer"));
    }

    public Message exit() {
        Message message = new Message();
        message.setCode(Message.MESSAGE_OK);
        message.setMessage("No order");
        Order order = LocalStorage.getInstance().getUserOrder();
        if (order != null){
            System.out.print("the system do not save your order" +
                    "\nAre you sure you want to quit the system? (y,n): ");
            Scanner scanner = App.getScanner();
            String input = scanner.nextLine().trim().toLowerCase();

            // Exit system and delete the user order
            if (input.equals("y")){
                message.setMessage("Confirm exit");
                System.out.println("Clean data...");

                //delete order
                Message result = OrderDAO.getInstance().deleteOrder(order.getOrderId());
                if (result.getCode() == Message.MESSAGE_OK){
                    System.out.println(ColorfulConsole.printSuccessInfo(result.getMessage()));
                }else {
                    System.out.println(ColorfulConsole.printFailInfo(result.getMessage()));
                }

                // delete local data
                LocalStorage.getInstance().cleanData();
            } else if (input.equals("n")) {
                message.setCode(Message.MESSAGE_FAIL);
                message.setMessage("Terminate exit");
            }else {
                message.setCode(Message.MESSAGE_FAIL);
                message.setMessage("Invalid input.");
            }
        }
        return message;
    }
}
